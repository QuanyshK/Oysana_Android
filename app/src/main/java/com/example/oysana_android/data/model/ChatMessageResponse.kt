package com.example.oysana_android.data.model

data class ChatMessageResponse(
    val id: Int = 0,
    val user_message: String,
    val bot_response: String?,
    val created_at: String
) {
    enum class LocalType {
        USER,
        BOT
    }

    var localType: LocalType = LocalType.BOT
}
