package com.exam.app.data.repository

import com.exam.app.data.local.ExamDao
import com.exam.app.data.local.LocalAnswer
import com.exam.app.data.local.LocalExamAttempt
import com.exam.app.data.remote.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val apiService: ApiService,
    private val examDao: ExamDao
) {
    fun getActiveLocalAttempt(): Flow<LocalExamAttempt?> {
        return examDao.getActiveAttempt()
    }

    suspend fun getAvailableExams(): Result<List<ExamDto>> = runCatching {
        apiService.getAvailableExams()
    }

    suspend fun startExamAttempt(examId: Long): Result<StartAttemptResponse> = runCatching {
        val response = apiService.startAttempt(examId)
        // Cache attempt locally in Room database
        val localAttempt = LocalExamAttempt(
            id = response.attemptId,
            examId = examId,
            examTitle = response.examTitle,
            durationMinutes = response.durationMinutes,
            startedAt = LocalDateTime.now().toString()
        )
        examDao.insertAttempt(localAttempt)
        response
    }

    suspend fun saveAnswerProgress(
        attemptId: Long,
        questionId: Long,
        selectedAnswer: String
    ): Result<Unit> = runCatching {
        // 1. Cache to Room SQLite
        examDao.saveAnswer(
            LocalAnswer(
                attemptId = attemptId,
                questionId = questionId,
                selectedAnswer = selectedAnswer,
                lastSavedAt = System.currentTimeMillis()
            )
        )
        // 2. Sync to Spring Boot REST API
        apiService.saveAnswer(attemptId, SaveAnswerRequest(questionId, selectedAnswer))
        Unit
    }

    suspend fun submitExamAttempt(attemptId: Long): Result<AttemptResultDto> = runCatching {
        val response = apiService.submitAttempt(attemptId)
        // Update local database status
        examDao.markSubmitted(attemptId)
        examDao.clearAnswersForAttempt(attemptId)
        examDao.deleteAttempt(attemptId)
        response
    }

    suspend fun getAttemptHistory(): Result<List<AttemptResultDto>> = runCatching {
        apiService.getAttemptHistory()
    }

    suspend fun getAttemptResult(attemptId: Long): Result<AttemptResultDto> = runCatching {
        apiService.getAttemptResult(attemptId)
    }
}
