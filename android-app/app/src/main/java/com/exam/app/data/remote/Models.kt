package com.exam.app.data.remote

// --- Auth Models ---
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val role: String
)

data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val id: Long,
    val fullName: String,
    val email: String,
    val role: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class ForgotPasswordRequest(
    val email: String
)

// --- Admin Models ---
data class UserDto(
    val id: Long?,
    val fullName: String,
    val email: String,
    val role: String,
    val status: String
)

data class SubjectDto(
    val id: Long?,
    val subjectName: String,
    val subjectCode: String,
    val description: String
)

data class AdminDashboardDto(
    val totalStudents: Long,
    val totalLecturers: Long,
    val totalSubjects: Long,
    val totalExams: Long,
    val activeExams: Long,
    val passRate: Double,
    val recentActivities: List<RecentActivityDto>
)

data class RecentActivityDto(
    val title: String,
    val message: String,
    val timeAgo: String
)

// --- Lecturer & Exam Models ---
data class ExamDto(
    val id: Long?,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val passingScore: Double,
    val subjectId: Long,
    val subjectName: String?,
    val subjectCode: String?,
    val lecturerId: Long?,
    val lecturerName: String?,
    val status: String?,
    val questionsCount: Int?,
    val totalMarks: Double?,
    val questions: List<QuestionDetailDto>?
)

data class QuestionDetailDto(
    val id: Long,
    val questionText: String,
    val questionType: String,
    val optionA: String?,
    val optionB: String?,
    val optionC: String?,
    val optionD: String?,
    val marks: Double,
    val difficultyLevel: String
)

data class ExamQuestionRequest(
    val questionId: Long,
    val marks: Double
)

data class CreateExamRequest(
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val passingScore: Double,
    val subjectId: Long,
    val questions: List<ExamQuestionRequest>
)

data class QuestionBankDto(
    val id: Long?,
    val subjectId: Long,
    val subjectName: String?,
    val questionText: String,
    val questionType: String,
    val optionA: String?,
    val optionB: String?,
    val optionC: String?,
    val optionD: String?,
    val correctAnswer: String,
    val difficultyLevel: String
)

// --- Student Attempt Models ---
data class StartAttemptResponse(
    val attemptId: Long,
    val examTitle: String,
    val durationMinutes: Int,
    val questions: List<AttemptQuestionDto>
)

data class AttemptQuestionDto(
    val id: Long,
    val questionText: String,
    val questionType: String,
    val optionA: String?,
    val optionB: String?,
    val optionC: String?,
    val optionD: String?,
    val marks: Double,
    val savedAnswer: String?
)

data class SaveAnswerRequest(
    val questionId: Long,
    val selectedAnswer: String
)

data class AttemptResultDto(
    val attemptId: Long,
    val examId: Long,
    val examTitle: String,
    val studentName: String?,
    val studentEmail: String?,
    val score: Double?,
    val totalMarks: Double?,
    val passingScore: Double?,
    val passed: Boolean?,
    val status: String,
    val startedAt: String,
    val submittedAt: String?,
    val answers: List<StudentAnswerDetailDto>?
)

data class StudentAnswerDetailDto(
    val questionId: Long,
    val questionText: String,
    val questionType: String,
    val optionA: String?,
    val optionB: String?,
    val optionC: String?,
    val optionD: String?,
    val correctAnswer: String?,
    val selectedAnswer: String?,
    val isCorrect: Boolean?,
    val marksAwarded: Double?,
    val maxMarks: Double?
)
