package com.hanafu.app.data.remote

import com.hanafu.app.data.remote.dto.ChatRequest
import com.hanafu.app.data.remote.dto.ChatResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * AI聊天API接口
 * 兼容 OpenAI API 格式
 */
interface ApiService {

    @POST("v1/chat/completions")
    suspend fun sendChatMessage(
        @Body request: ChatRequest
    ): Response<ChatResponse>

    @POST("v1/chat/completions")
    suspend fun sendChatMessageStream(
        @Body request: ChatRequest
    ): Response<ResponseBody>
}
