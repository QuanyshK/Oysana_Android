package com.example.oysana_android.data.network

import android.content.Context
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AIClient {

    fun create(context: Context): AIService {
        val authManager = AuthManager(context)
        val username = authManager.getUsername()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor { chain ->
            val original = chain.request()
            val requestWithHeader = original.newBuilder()
                .addHeader("X-Username", username ?: "")
                .build()
            chain.proceed(requestWithHeader)
        }.build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_AI)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AIService::class.java)
    }
}
