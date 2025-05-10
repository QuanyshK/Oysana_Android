package com.example.oysana_android.data.network

import com.example.oysana_android.data.model.ChatCreateRequest
import com.example.oysana_android.data.model.ChatMessageResponse
import retrofit2.Response
import retrofit2.http.*

interface AIService {

    @GET("api/me/")
    suspend fun getCurrentUser(): Response<Any>

    @POST("api/users/create/")
    suspend fun createUser(
        @Body body: Map<String, String>
    ): Response<Any>

    @GET("api/chats/")
    suspend fun getChats(): Response<List<ChatMessageResponse>>

    @POST("api/chats/create/")
    suspend fun createChat(
        @Body request: ChatCreateRequest
    ): Response<ChatMessageResponse>

    @GET("api/chats/{id}/")
    suspend fun getChatDetail(
        @Path("id") id: Int
    ): Response<ChatMessageResponse>

    @DELETE("api/chats/{id}/delete/")
    suspend fun deleteChat(
        @Path("id") id: Int
    ): Response<Unit>
}
