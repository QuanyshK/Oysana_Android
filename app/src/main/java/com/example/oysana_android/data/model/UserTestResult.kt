package com.example.oysana_android.data.model

import com.google.gson.annotations.SerializedName

data class UserTestResult(
    @SerializedName("id")
    val id: Int,

    @SerializedName("user")
    val userId: Int,

    @SerializedName("test")
    val testId: Int,

    @SerializedName("score")
    val score: Int,

    @SerializedName("passed")
    val passed: Boolean,

    @SerializedName("created_at")
    val createdAt: String // ISO 8601
)
