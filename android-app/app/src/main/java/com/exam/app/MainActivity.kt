package com.exam.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.exam.app.ui.navigation.Screen
import com.exam.app.ui.screens.admin.AdminDashboardScreen
import com.exam.app.ui.screens.admin.SubjectManagementScreen
import com.exam.app.ui.screens.admin.UserManagementScreen
import com.exam.app.ui.screens.auth.ForgotPasswordScreen
import com.exam.app.ui.screens.auth.LoginScreen
import com.exam.app.ui.screens.auth.RegisterScreen
import com.exam.app.ui.screens.lecturer.CreateExamScreen
import com.exam.app.ui.screens.lecturer.LecturerDashboardScreen
import com.exam.app.ui.screens.lecturer.QuestionBankScreen
import com.exam.app.ui.screens.lecturer.StudentResultsScreen
import com.exam.app.ui.screens.student.ResultsDetailScreen
import com.exam.app.ui.screens.student.StudentDashboardScreen
import com.exam.app.ui.screens.student.TakeExamScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExamAppNavigation()
                }
            }
        }
    }
}

@Composable
fun ExamAppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Authentication routes
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }

        // Admin routes
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(navController)
        }
        composable(Screen.UserManagement.route) {
            UserManagementScreen(navController)
        }
        composable(Screen.SubjectManagement.route) {
            SubjectManagementScreen(navController)
        }

        // Lecturer routes
        composable(Screen.LecturerDashboard.route) {
            LecturerDashboardScreen(navController)
        }
        composable(Screen.CreateExam.route) {
            CreateExamScreen(navController)
        }
        composable(Screen.QuestionBank.route) {
            QuestionBankScreen(navController)
        }
        composable(
            route = Screen.StudentResults.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId") ?: 0L
            StudentResultsScreen(examId, navController)
        }

        // Student routes
        composable(Screen.StudentDashboard.route) {
            StudentDashboardScreen(navController)
        }
        composable(
            route = Screen.TakeExam.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId") ?: 0L
            TakeExamScreen(examId, navController)
        }
        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("attemptId") { type = NavType.LongType })
        ) { backStackEntry ->
            val attemptId = backStackEntry.arguments?.getLong("attemptId") ?: 0L
            ResultsDetailScreen(attemptId, navController)
        }
    }
}
