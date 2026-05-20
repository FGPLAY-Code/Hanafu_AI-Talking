package com.hanafu.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * AI聊天响应体（非流式）
 */
data class ChatResponse(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("object")
    val `object`: String? = null,
    
    @SerializedName("created")
    val created: Long? = null,
    
    @SerializedName("model")
    val model: String? = null,
    
    @SerializedName("choices")
    val choices: List<Choice>? = null,
    
    @SerializedName("usage")
    val usage: Usage? = null
)

data class Choice(
    @SerializedName("index")
    val index: Int? = null,
    
    @SerializedName("message")
    val message: Message? = null,
    
    @SerializedName("finish_reason")
    val finishReason: String? = null
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int? = null,
    
    @SerializedName("completion_tokens")
    val completionTokens: Int? = null,
    
    @SerializedName("total_tokens")
    val totalTokens: Int? = null
)

/**
 * 流式响应数据块
 */
data class ChatStreamChunk(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("object")
    val `object`: String? = null,
    
    @SerializedName("created")
    val created: Long? = null,
    
    @SerializedName("model")
    val model: String? = null,
    
    @SerializedName("choices")
    val choices: List<StreamChoice>? = null
)

data class StreamChoice(
    @SerializedName("index")
    val index: Int? = null,
    
    @SerializedName("delta")
    val delta: Delta? = null,
    
    @SerializedName("finish_reason")
    val finishReason: String? = null
)

data class Delta(
    @SerializedName("role")
    val role: String? = null,
    
    @SerializedName("content")
    val content: String? = null
)
