package com.example.oysana_android.data.model

data class PaginatedChatResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ChatMessageResponse>
)
