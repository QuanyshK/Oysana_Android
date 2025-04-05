package com.example.oysana_android.data.model

import com.example.oysana_android.data.network.TestResultResponse
import com.google.gson.annotations.SerializedName

data class Topic(
    @SerializedName("id")
    val id: Int,

    @SerializedName("course")
    val courseId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("order")
    val order: Int,

    @SerializedName("video_url")
    val videoUrl: String?,

    @SerializedName("video_title")
    val videoTitle: String,

    @SerializedName("duration_in_minutes")
    val durationInMinutes: Int?,

    @SerializedName("is_unlocked")
    val isUnlocked: Boolean = false,

    @SerializedName("is_test_passed")
    val isTestPassed: Boolean = false,

    @SerializedName("test")
    val test: Test?,
    @SerializedName("user_result")
    val userResult: TestResultResponse?

)