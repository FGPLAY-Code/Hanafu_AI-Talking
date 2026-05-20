package com.hanafu.app.model

/**
 * UI层会话数据模型
 */
data class Conversation(
    val id: Long = 0,
    val title: String = "新对话",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val modelName: String = "default",
    val messageCount: Int = 0
)
