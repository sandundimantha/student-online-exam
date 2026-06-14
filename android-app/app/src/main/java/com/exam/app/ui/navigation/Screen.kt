package com.exam.app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    
    // Admin Module
    object AdminDashboard : Screen("admin_dashboard")
    object UserManagement : Screen("user_management")
    object SubjectManagement : Screen("subject_management")
    
    // Lecturer Module
    object LecturerDashboard : Screen("lecturer_dashboard")
    object CreateExam : Screen("create_exam")
    object QuestionBank : Screen("question_bank")
    object StudentResults : Screen("student_results/{examId}") {
        fun createRoute(examId: Long) = "student_results/$examId"
    }

    // Student Module
    object StudentDashboard : Screen("student_dashboard")
    object TakeExam : Screen("take_exam/{examId}") {
        fun createRoute(examId: Long) = "take_exam/$examId"
    }
    object Results : Screen("results/{attemptId}") {
        fun createRoute(attemptId: Long) = "results/$attemptId"
    }
}
