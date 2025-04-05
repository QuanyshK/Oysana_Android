package com.example.oysana_android.data.model

import com.google.gson.annotations.SerializedName

data class Course(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("course_type")
    val courseType: String = "student", // только для студентов

    @SerializedName("sub_type")
    val subType: String?,

    @SerializedName("img")
    val img: String?
)