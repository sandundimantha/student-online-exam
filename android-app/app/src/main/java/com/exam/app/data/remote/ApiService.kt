package com.exam.app.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Authentication ---
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): AuthResponse

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ResponseBody>

    // --- Admin Endpoints ---
    @GET("api/admin/dashboard")
    suspend fun getDashboardStats(): AdminDashboardDto

    @GET("api/admin/users")
    suspend fun getUsers(
        @Query("search") search: String?,
        @Query("role") role: String?,
        @Query("status") status: String?
    ): List<UserDto>

    @POST("api/admin/users")
    suspend fun createUser(@Body request: RegisterRequest): UserDto

    @PUT("api/admin/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body request: UserDto): UserDto

    @DELETE("api/admin/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>

    @GET("api/admin/subjects")
    suspend fun getSubjects(): List<SubjectDto>

    @POST("api/admin/subjects")
    suspend fun createSubject(@Body request: SubjectDto): SubjectDto

    @PUT("api/admin/subjects/{id}")
    suspend fun updateSubject(@Path("id") id: Long, @Body request: SubjectDto): SubjectDto

    @DELETE("api/admin/subjects/{id}")
    suspend fun deleteSubject(@Path("id") id: Long): Response<Unit>

    // --- Lecturer Endpoints ---
    @GET("api/lecturer/exams")
    suspend fun getLecturerExams(): List<ExamDto>

    @POST("api/lecturer/exams")
    suspend fun createExam(@Body request: CreateExamRequest): ExamDto

    @PUT("api/lecturer/exams/{id}")
    suspend fun updateExam(@Path("id") id: Long, @Body request: CreateExamRequest): ExamDto

    @POST("api/lecturer/exams/{id}/publish")
    suspend fun publishExam(@Path("id") id: Long): ExamDto

    @DELETE("api/lecturer/exams/{id}")
    suspend fun deleteExam(@Path("id") id: Long): Response<Unit>

    @GET("api/lecturer/questions/subject/{subjectId}")
    suspend fun getQuestionsBySubject(@Path("subjectId") subjectId: Long): List<QuestionBankDto>

    @POST("api/lecturer/questions")
    suspend fun createQuestion(@Body request: QuestionBankDto): QuestionBankDto

    @PUT("api/lecturer/questions/{id}")
    suspend fun updateQuestion(@Path("id") id: Long, @Body request: QuestionBankDto): QuestionBankDto

    @DELETE("api/lecturer/questions/{id}")
    suspend fun deleteQuestion(@Path("id") id: Long): Response<Unit>

    @GET("api/lecturer/exams/{examId}/results")
    suspend fun getExamResults(@Path("examId") examId: Long): List<AttemptResultDto>

    // --- Student Endpoints ---
    @GET("api/student/exams")
    suspend fun getAvailableExams(): List<ExamDto>

    @POST("api/student/exams/{examId}/start")
    suspend fun startAttempt(@Path("examId") examId: Long): StartAttemptResponse

    @POST("api/student/exams/attempts/{attemptId}/save")
    suspend fun saveAnswer(
        @Path("attemptId") attemptId: Long,
        @Body request: SaveAnswerRequest
    ): Response<Unit>

    @POST("api/student/exams/attempts/{attemptId}/submit")
    suspend fun submitAttempt(@Path("attemptId") attemptId: Long): AttemptResultDto

    @GET("api/student/attempts")
    suspend fun getAttemptHistory(): List<AttemptResultDto>

    @GET("api/student/attempts/{attemptId}")
    suspend fun getAttemptResult(@Path("attemptId") attemptId: Long): AttemptResultDto

    @GET("api/student/attempts/{attemptId}/pdf")
    @Streaming
    suspend fun downloadResultPDF(@Path("attemptId") attemptId: Long): Response<ResponseBody>
}
