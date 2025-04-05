package com.example.oysana_android.data.model

import com.google.gson.annotations.SerializedName

data class Answer(
    @SerializedName("id")
    val id: Int,

    @SerializedName("question")
    val questionId: Int,

    @SerializedName("text")
    val text: String,

    @SerializedName("is_correct")
    val isCorrect: Boolean
)
