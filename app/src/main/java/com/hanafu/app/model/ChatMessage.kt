package com.hanafu.app.model

/**
 * UI层聊天消息数据模型
 */
data class ChatMessage(
    val id: Long = 0,
    val conversationId: Long = 0,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}
