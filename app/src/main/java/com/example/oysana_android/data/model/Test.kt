package com.example.oysana_android.data.model

import com.google.gson.annotations.SerializedName

data class Test(
    @SerializedName("id")
    val id: Int,

    @SerializedName("topic")
    val topicId: Int,

    @SerializedName("questions")
    val questions: List<Question>
)