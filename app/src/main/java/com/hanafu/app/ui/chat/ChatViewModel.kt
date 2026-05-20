package com.hanafu.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hanafu.app.data.repository.ChatRepository
import com.hanafu.app.data.repository.SettingsRepository
import com.hanafu.app.model.ChatMessage
import com.hanafu.app.model.Conversation
import com.hanafu.app.model.MessageRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val currentConversation: Conversation? = null,
    val isLoading: Boolean = false,
    val isStreaming: Boolean = false,
    val streamingContent: String = "",
    val isTypewriterMode: Boolean = false,
    val error: String? = null,
    val characterName: String = "花谱",
    val characterPersona: String = "",
    val characterGreeting: String = ""
)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository,
    private val conversationId: Long
) : ViewModel() {

    companion object {
        private const val MODEL_NAME = "qwen2.5:1.5b-instruct"
        private const val TYPEWRITER_DELAY_MS = 200L // 5字符/秒 = 200ms/字符
    }

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var typewriterJob: Job? = null

    init {
        loadCharacterConfig()
        initializeConversation()
    }

    private fun loadCharacterConfig() {
        viewModelScope.launch {
            val name = settingsRepository.getCharacterName()
            val persona = settingsRepository.getCharacterPersona()
            val greeting = settingsRepository.getCharacterGreeting()
            _uiState.value = _uiState.value.copy(
                characterName = name,
                characterPersona = persona,
                characterGreeting = greeting
            )
        }
    }

    private fun initializeConversation() {
        viewModelScope.launch {
            try {
                var convId = conversationId

                if (convId == -1L) {
                    convId = chatRepository.createConversation()
                }

                viewModelScope.launch {
                    chatRepository.getConversationById(convId).collect { conversation ->
                        if (conversation != null) {
                            _uiState.value = _uiState.value.copy(
                                currentConversation = conversation
                            )
                        }
                    }
                }

                viewModelScope.launch {
                    chatRepository.getMessagesByConversation(convId).collect { messages ->
                        val currentState = _uiState.value
                        var newState = currentState.copy(messages = messages)

                        // 流式响应完成后，等 Room 确认消息已持久化再清空流式状态
                        // 打字机模式期间不清空
                        if (!currentState.isLoading && currentState.isStreaming &&
                            !currentState.isTypewriterMode &&
                            currentState.streamingContent.isNotEmpty() && messages.isNotEmpty()
                        ) {
                            val lastMsg = messages.last()
                            if (lastMsg.role == MessageRole.ASSISTANT) {
                                newState = newState.copy(
                                    isStreaming = false,
                                    streamingContent = ""
                                )
                            }
                        }

                        _uiState.value = newState
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "初始化失败: ${e.message}"
                )
            }
        }
    }

    fun sendMessage(content: String) {
        val conversation = _uiState.value.currentConversation ?: return
        if (content.isBlank()) return

        // 取消正在进行的打字机效果
        typewriterJob?.cancel()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isStreaming = true,
                streamingContent = "",
                isTypewriterMode = false,
                error = null
            )

            chatRepository.sendStreamingMessage(
                conversationId = conversation.id,
                content = content,
                modelName = MODEL_NAME,
                systemPrompt = _uiState.value.characterPersona,
                onChunk = { chunk ->
                    // 不实时更新 UI，只是缓冲收到的内容
                },
                onComplete = { fullContent ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isTypewriterMode = true
                    )

                    // 启动打字机效果：5字符/秒
                    typewriterJob = viewModelScope.launch {
                        val buffer = fullContent
                        val conversation = _uiState.value.currentConversation
                        for (i in buffer.indices) {
                            _uiState.value = _uiState.value.copy(
                                streamingContent = buffer.substring(0, i + 1)
                            )
                            delay(TYPEWRITER_DELAY_MS)
                        }
                        // 打字机完成，先标记状态，再保存到 Room
                        _uiState.value = _uiState.value.copy(
                            isTypewriterMode = false,
                            streamingContent = ""  // 立即清空，避免重复显示
                        )
                        // 保存 AI 回复到 Room（Room Flow 会刷新 messages）
                        if (buffer.isNotEmpty() && conversation != null) {
                            val aiMessage = ChatMessage(
                                conversationId = conversation.id,
                                role = MessageRole.ASSISTANT,
                                content = buffer
                            )
                            chatRepository.saveMessage(aiMessage)
                        }
                    }
                },
                onError = { error ->
                    typewriterJob?.cancel()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isStreaming = false,
                        streamingContent = "",
                        isTypewriterMode = false,
                        error = "发送失败: ${error.message}"
                    )
                }
            )
        }
    }

    fun updateCharacter(name: String, persona: String, greeting: String) {
        viewModelScope.launch {
            settingsRepository.saveCharacterConfig(name, persona, greeting)
            _uiState.value = _uiState.value.copy(
                characterName = name,
                characterPersona = persona,
                characterGreeting = greeting
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun deleteMessage(message: ChatMessage) {
        viewModelScope.launch {
            chatRepository.deleteMessage(message)
        }
    }

    fun clearConversation() {
        val conversation = _uiState.value.currentConversation ?: return
        viewModelScope.launch {
            chatRepository.deleteAllMessagesInConversation(conversation.id)
        }
    }

    class Factory(
        private val chatRepository: ChatRepository,
        private val settingsRepository: SettingsRepository,
        private val conversationId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(chatRepository, settingsRepository, conversationId) as T
        }
    }
}
