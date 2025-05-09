package com.example.oysana_android.data.network

import com.example.oysana_android.data.model.*
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/token/")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @GET("api/courses/user/me/")
    suspend fun getCurrentUser(): Response<User>

    @GET("api/courses/public-courses/")
    suspend fun getPublicCourses(): Response<List<Course>>

    @GET("api/courses/my-courses/")
    suspend fun getMyCourses(): Response<List<Course>>

    @GET("api/courses/public-courses/{id}/")
    suspend fun getCourseDetail(@Path("id") courseId: Int): Response<Course>

    @GET("api/courses/my-courses/{id}/topics/")
    suspend fun getCourseTopics(@Path("id") courseId: Int): Response<List<Topic>>

    @GET("api/courses/public-courses/{course_id}/first-topic/")
    suspend fun getCourseFirstTopic(@Path("course_id") courseId: Int): Response<List<Topic>>

    @GET("api/courses/topics/{topic_id}/")
    suspend fun getTopicDetail(@Path("topic_id") topicId: Int): Response<Topic>

    @POST("api/courses/topics/{topic_id}/submit-test/")
    suspend fun submitTest(
        @Path("topic_id") topicId: Int,
        @Body request: SubmitTestRequest
    ): Response<TestResultResponse>

    @POST("api/token/refresh/")
    fun refreshTokenSync(@Body request: RefreshRequest): retrofit2.Call<TokenResponse>
    @POST("api/courses/register/")
    suspend fun registerLead(@Body body: RegistrationRequest): Response<Any>


}

data class RefreshRequest(val refresh: String)

data class TokenResponse(
    val access: String,
    val refresh: String,
    val role: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class SubmitTestRequest(
    @SerializedName("answers")
    val answers: List<AnswerSubmission>
)

data class AnswerSubmission(
    @SerializedName("question_id")
    val questionId: Int,
    @SerializedName("answer_id")
    val answerId: Int
)

data class TestResultResponse(
    @SerializedName("score")
    val score: Int,
    @SerializedName("passed")
    val passed: Boolean,
    @SerializedName("answers_detail")
    val answersDetail: List<AnswerDetail>
)

data class AnswerDetail(
    @SerializedName("question_id")
    val questionId: Int,
    @SerializedName("answered_correctly")
    val answeredCorrectly: Boolean
)


