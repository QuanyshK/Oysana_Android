package com.example.oysana_android.data.model

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("id")
    val id: Int,

    @SerializedName("test")
    val testId: Int,

    @SerializedName("text")
    val text: String,

    @SerializedName("answers")
    val answers: List<Answer>
)