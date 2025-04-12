package com.example.oysana_android.data.database

import android.content.Context
import java.util.UUID

object UserTokenManager {
    private const val PREF_NAME = "ai_token_pref"
    private const val KEY_TOKEN = "user_token"

    fun getOrCreateToken(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var token = prefs.getString(KEY_TOKEN, null)
        if (token == null) {
            token = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_TOKEN, token).apply()
        }
        return token
    }
}
