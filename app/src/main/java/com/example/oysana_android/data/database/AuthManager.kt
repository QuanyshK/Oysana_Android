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
    }

    fun saveTokens(access: String, refresh: String) {
        prefs.edit {
            putString(ACCESS_TOKEN, access)
            putString(REFRESH_TOKEN, refresh)
        }
    }

    fun getAccessToken(): String? = prefs.getString(ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(REFRESH_TOKEN, null)

    fun clearTokens() {
        prefs.edit { clear() }
    }

    fun isLoggedIn() = getAccessToken() != null && getRefreshToken() != null
}
