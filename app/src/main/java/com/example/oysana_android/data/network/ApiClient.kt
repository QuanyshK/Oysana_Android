package com.example.oysana_android.data.network

import android.util.Log
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var authManager: AuthManager? = null

    fun create(authToken: String? = null): ApiService {
        val clientBuilder = OkHttpClient.Builder()

        clientBuilder.addInterceptor { chain ->
            val originalRequest = chain.request()
            val token = authToken ?: authManager?.getAccessToken()

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

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(clientBuilder.build())
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

                //Обновляем только access-токен, refresh остаётся прежним
                authManager?.saveTokens(
                    access = newAccessToken,
                    refresh = refreshToken
                )

                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "Refresh token failed: ${e.message}")
            false
        }
    }
}
