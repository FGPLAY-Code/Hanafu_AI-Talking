package com.hanafu.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * AI聊天请求体
 * 兼容 OpenAI API 格式
 */
data class ChatRequest(
    @SerializedName("model")
    val model: String = "default",
    
    @SerializedName("messages")
    val messages: List<Message>,
    
    @SerializedName("stream")
    val stream: Boolean = true,
    
    @SerializedName("temperature")
    val temperature: Double = 0.7,
    
    @SerializedName("max_tokens")
    val maxTokens: Int = 2048
)

data class Message(
    @SerializedName("role")
    val role: String, // "user", "assistant", "system"
    
    @SerializedName("content")
    val content: String
)
