package com.example.oysana_android.data.network

import android.content.Context
import android.util.Log
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var authManager: AuthManager? = null

    fun init(context: Context) {
        authManager = AuthManager(context)
    }

    fun create(): ApiService {
        if (authManager == null) {
            throw IllegalStateException("ApiClient is not initialized. Call ApiClient.init(context) first.")
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()

                val exp = authManager?.getTokenExpirationTime()
                if (exp != null && System.currentTimeMillis() > exp * 1000) {
                    Log.d("ApiClient", "Access token expired. Attempting refresh...")
                    val refreshed = refreshToken()
                    if (!refreshed) {
                        authManager?.clearTokens()
                    }
                }

                val token = authManager?.getAccessToken()
                val requestWithAuth = if (!token.isNullOrEmpty()) {
                    originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    originalRequest
                }

                val response = chain.proceed(requestWithAuth)

                if (response.code == 401 && authManager?.getRefreshToken() != null) {
                    response.close()

                    val refreshed = refreshToken()
                    if (refreshed) {
                        val newAccessToken = authManager?.getAccessToken()
                        val retryRequest = originalRequest.newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer $newAccessToken")
                            .build()
                        return@addInterceptor chain.proceed(retryRequest)
                    } else {
                        authManager?.clearTokens()
                    }
                }

                response
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun refreshToken(): Boolean {
        val refreshToken = authManager?.getRefreshToken() ?: return false

        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(ApiService::class.java)
            val response = service.refreshTokenSync(RefreshRequest(refreshToken)).execute()

            if (response.isSuccessful && response.body() != null) {
                val newAccessToken = response.body()!!.access
                authManager?.saveTokens(newAccessToken, refreshToken)
                Log.d("ApiClient", "Access token refreshed")
                true
            } else {
                Log.e("ApiClient", "Failed to refresh token: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "Exception during token refresh: ${e.message}")
            false
        }
    }
}
