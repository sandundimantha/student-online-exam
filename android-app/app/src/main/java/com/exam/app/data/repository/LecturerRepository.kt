package com.exam.app.data.repository

import com.exam.app.data.remote.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LecturerRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getLecturerExams(): Result<List<ExamDto>> = runCatching {
        apiService.getLecturerExams()
    }

    suspend fun createExam(request: CreateExamRequest): Result<ExamDto> = runCatching {
        apiService.createExam(request)
    }

    suspend fun updateExam(id: Long, request: CreateExamRequest): Result<ExamDto> = runCatching {
        apiService.updateExam(id, request)
    }

    suspend fun publishExam(id: Long): Result<ExamDto> = runCatching {
        apiService.publishExam(id)
    }

    suspend fun deleteExam(id: Long): Result<Unit> = runCatching {
        apiService.deleteExam(id)
        Unit
    }

    suspend fun getQuestions(subjectId: Long): Result<List<QuestionBankDto>> = runCatching {
        apiService.getQuestionsBySubject(subjectId)
    }

    suspend fun createQuestion(request: QuestionBankDto): Result<QuestionBankDto> = runCatching {
        apiService.createQuestion(request)
    }

    suspend fun updateQuestion(id: Long, request: QuestionBankDto): Result<QuestionBankDto> = runCatching {
        apiService.updateQuestion(id, request)
    }

    suspend fun deleteQuestion(id: Long): Result<Unit> = runCatching {
        apiService.deleteQuestion(id)
        Unit
    }

    suspend fun getExamResults(examId: Long): Result<List<AttemptResultDto>> = runCatching {
        apiService.getExamResults(examId)
    }
}
