package com.hanafu.app.data.repository

import com.hanafu.app.data.local.dao.ChatDao
import com.hanafu.app.data.local.dao.ConversationDao
import com.hanafu.app.data.local.entity.ChatMessageEntity
import com.hanafu.app.data.local.entity.ConversationEntity
import com.hanafu.app.data.remote.ApiClient
import com.hanafu.app.data.remote.dto.ChatRequest
import com.hanafu.app.data.remote.dto.Message
import com.hanafu.app.model.ChatMessage
import com.hanafu.app.model.Conversation
import com.hanafu.app.model.MessageRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 聊天数据仓库
 */
class ChatRepository(
    private val chatDao: ChatDao,
    private val conversationDao: ConversationDao
) {

    // ===== 会话操作 =====

    fun getAllConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getConversationById(id: Long): Flow<Conversation?> {
        return conversationDao.getConversationByIdFlow(id).map { it?.toDomain() }
    }

    suspend fun createConversation(title: String = "新对话", modelName: String = "default"): Long {
        val entity = ConversationEntity(
            title = title,
            modelName = modelName
        )
        return conversationDao.insertConversation(entity)
    }

    suspend fun deleteConversation(id: Long) {
        val entity = conversationDao.getConversationById(id) ?: return
        conversationDao.deleteConversation(entity)
    }

    suspend fun updateConversationTitle(id: Long, title: String) {
        conversationDao.updateConversationTitle(id, title)
    }

    // ===== 消息操作 =====

    fun getMessagesByConversation(conversationId: Long): Flow<List<ChatMessage>> {
        return chatDao.getMessagesByConversation(conversationId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getMessageCount(conversationId: Long): Int {
        return chatDao.getMessageCount(conversationId)
    }

    suspend fun saveMessage(message: ChatMessage): Long {
        val entity = ChatMessageEntity(
            conversationId = message.conversationId,
            role = message.role.name.lowercase(),
            content = message.content,
            timestamp = message.timestamp
        )
        val id = chatDao.insertMessage(entity)
        conversationDao.updateConversationTimestamp(
            message.conversationId,
            System.currentTimeMillis()
        )
        return id
    }

    suspend fun deleteMessage(message: ChatMessage) {
        val entity = ChatMessageEntity(
            id = message.id,
            conversationId = message.conversationId,
            role = message.role.name.lowercase(),
            content = message.content,
            timestamp = message.timestamp
        )
        chatDao.deleteMessage(entity)
    }

    suspend fun deleteAllMessagesInConversation(conversationId: Long) {
        chatDao.deleteAllMessagesInConversation(conversationId)
    }

    // ===== AI 对话 =====

    /**
     * 发送消息并获取流式响应
     * @param conversationId 会话ID
     * @param content 用户消息内容
     * @param systemPrompt 系统人设提示词（可选）
     * @param onChunk 流式回调，每次收到一个文本块
     * @param onComplete 完成回调
     * @param onError 错误回调
     */
    suspend fun sendStreamingMessage(
        conversationId: Long,
        content: String,
        modelName: String = "default",
        systemPrompt: String = "",
        onChunk: (String) -> Unit,
        onComplete: (String) -> Unit,
        onError: (Exception) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            // 1. 保存用户消息
            val userMessage = ChatMessage(
                conversationId = conversationId,
                role = MessageRole.USER,
                content = content
            )
            saveMessage(userMessage)

            // 2. 获取历史消息构建上下文
            val historyMessages = chatDao.getMessagesPaged(
                conversationId = conversationId,
                limit = 50,
                offset = 0
            ).map { entity ->
                Message(
                    role = entity.role,
                    content = entity.content
                )
            }.toMutableList()

            // 3. 如果有 systemPrompt，在历史消息最前面插入
            if (systemPrompt.isNotBlank()) {
                historyMessages.add(0, Message(role = "system", content = systemPrompt))
            }

            // 4. 构建请求
            val request = ChatRequest(
                model = modelName,
                messages = historyMessages,
                stream = true
            )

            // 4. 发送流式请求
            val response = ApiClient.apiService.sendChatMessageStream(request)

            if (!response.isSuccessful) {
                onError(Exception("HTTP ${response.code()}: ${response.message()}"))
                return@withContext
            }

            val body: ResponseBody = response.body() ?: run {
                onError(Exception("响应体为空"))
                return@withContext
            }

            // 5. 解析流式响应
            val reader = BufferedReader(InputStreamReader(body.byteStream()))
            val fullContent = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue

                // 跳过空行和 SSE 注释
                if (currentLine.isEmpty() || currentLine.startsWith(":")) continue

                // 解析 "data: {...}" 格式
                if (currentLine.startsWith("data: ")) {
                    val jsonStr = currentLine.removePrefix("data: ")

                    // 检查结束标记
                    if (jsonStr == "[DONE]") break

                    try {
                        val json = JSONObject(jsonStr)
                        val choices = json.optJSONArray("choices")
                        if (choices != null && choices.length() > 0) {
                            val choice = choices.getJSONObject(0)
                            val delta = choice.optJSONObject("delta")
                            if (delta != null) {
                                val chunkContent = delta.optString("content", "")
                                if (chunkContent.isNotEmpty()) {
                                    fullContent.append(chunkContent)
                                    onChunk(chunkContent)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // 跳过解析失败的块
                    }
                }
            }

            reader.close()

            // 6. 通知完成（AI 回复由 ViewModel 在打字机结束后再保存）
            onComplete(fullContent.toString())

        } catch (e: Exception) {
            onError(e)
        }
    }

    // ===== 扩展函数：Entity 转 Domain =====

    private fun ChatMessageEntity.toDomain(): ChatMessage {
        return ChatMessage(
            id = id,
            conversationId = conversationId,
            role = try {
                MessageRole.valueOf(role.uppercase())
            } catch (e: Exception) {
                MessageRole.ASSISTANT
            },
            content = content,
            timestamp = timestamp
        )
    }

    private fun ConversationEntity.toDomain(): Conversation {
        return Conversation(
            id = id,
            title = title,
            createdAt = createdAt,
            updatedAt = updatedAt,
            modelName = modelName
        )
    }
}
