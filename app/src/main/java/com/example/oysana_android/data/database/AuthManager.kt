package com.example.oysana_android.data.database

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AuthManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USERNAME = "username"
    }

    fun saveTokens(access: String, refresh: String) {
        prefs.edit {
            putString(ACCESS_TOKEN, access)
            putString(REFRESH_TOKEN, refresh)
        }
    }

    fun saveUsername(username: String) {
        prefs.edit {
            putString(USERNAME, username)
        }
    }
    fun getTokenExpirationTime(): Long? {
        val token = getAccessToken() ?: return null
        val parts = token.split(".")
        if (parts.size != 3) return null

        return try {
            val payload = android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE)
            val json = String(payload, Charsets.UTF_8)
            val regex = """"exp"\s*:\s*(\d+)""".toRegex()
            val match = regex.find(json)
            match?.groupValues?.get(1)?.toLong()
        } catch (e: Exception) {
            null
        }
    }

    fun getAccessToken(): String? = prefs.getString(ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(REFRESH_TOKEN, null)
    fun getUsername(): String? = prefs.getString(USERNAME, null)

    fun clearTokens() {
        prefs.edit { clear() }
    }

    fun isLoggedIn() = getAccessToken() != null && getRefreshToken() != null
}
