package com.exam.app.ui.screens.student

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.exam.app.ui.navigation.Screen
import com.exam.app.viewmodel.AuthViewModel
import com.exam.app.viewmodel.StudentViewModel
import com.exam.app.data.remote.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    navController: NavController,
    studentViewModel: StudentViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val availableExams by studentViewModel.availableExams.collectAsState()
    val attemptHistory by studentViewModel.attemptHistory.collectAsState()

    LaunchedEffect(Unit) {
        studentViewModel.loadAvailableExams()
        studentViewModel.loadAttemptHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Portal") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text("Available Examinations", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (availableExams.isEmpty()) {
                item {
                    Text("No examinations available at this time.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                items(availableExams) { exam ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(exam.title, style = MaterialTheme.typography.titleMedium)
                            Text("Subject: ${exam.subjectName}")
                            Text("Duration: ${exam.durationMinutes} mins | Questions: ${exam.questionsCount}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { navController.navigate(Screen.TakeExam.createRoute(exam.id ?: 0L)) }) {
                                Text("Start Exam")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Your Attempt History", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(attemptHistory) { attempt ->
                ListItem(
                    headlineContent = { Text(attempt.examTitle) },
                    supportingContent = { Text("Score: ${attempt.score}/${attempt.totalMarks} | ${attempt.status}") },
                    trailingContent = {
                        TextButton(onClick = { navController.navigate(Screen.Results.createRoute(attempt.attemptId)) }) {
                            Text("Details")
                        }
                    }
                )
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeExamScreen(
    examId: Long,
    navController: NavController,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val activeAttempt by viewModel.activeAttempt.collectAsState()
    val timeLeft by viewModel.timeLeftSeconds.collectAsState()
    val submissionState by viewModel.submissionState.collectAsState()
    val context = LocalContext.current

    // SECURE CODE: Disable Screenshots during active exam
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    // Block android back button
    BackHandler {
        // Warning dialog or disable back button
    }

    LaunchedEffect(examId) {
        viewModel.startExam(examId)
    }

    LaunchedEffect(submissionState) {
        if (submissionState is StudentViewModel.SubmissionState.Success) {
            val result = (submissionState as StudentViewModel.SubmissionState.Success).result
            navController.navigate(Screen.Results.createRoute(result.attemptId)) {
                popUpTo(Screen.StudentDashboard.route)
            }
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timerText = String.format("%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(activeAttempt?.examTitle ?: "Examination") },
                actions = {
                    Text(
                        text = "Time Left: $timerText",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (timeLeft < 60) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { padding ->
        val attempt = activeAttempt
        if (attempt == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(attempt.questions) { q ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Q: ${q.questionText}", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))

                                val selected = viewModel.getSelectedAnswer(q.id)

                                if (q.questionType == "MCQ") {
                                    q.optionA?.let { OptionRow("A", it, selected == it) { viewModel.selectAnswer(q.id, it) } }
                                    q.optionB?.let { OptionRow("B", it, selected == it) { viewModel.selectAnswer(q.id, it) } }
                                    q.optionC?.let { OptionRow("C", it, selected == it) { viewModel.selectAnswer(q.id, it) } }
                                    q.optionD?.let { OptionRow("D", it, selected == it) { viewModel.selectAnswer(q.id, it) } }
                                } else if (q.questionType == "TRUE_FALSE") {
                                    OptionRow("True", "True", selected == "True") { viewModel.selectAnswer(q.id, "True") }
                                    OptionRow("False", "False", selected == "False") { viewModel.selectAnswer(q.id, "False") }
                                } else {
                                    // Short Answer text input
                                    OutlinedTextField(
                                        value = selected,
                                        onValueChange = { viewModel.selectAnswer(q.id, it) },
                                        label = { Text("Your answer text...") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.submitExam() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Submit Examination")
                }
            }
        }
    }
}

@Composable
fun OptionRow(
    label: String,
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onSelect)
        Text(text = "($label) $text", modifier = Modifier.padding(start = 8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsDetailScreen(
    attemptId: Long,
    navController: NavController,
    viewModel: StudentViewModel = hiltViewModel()
) {
    var resultState by remember { mutableStateOf<AttemptResultDto?>(null) }

    LaunchedEffect(attemptId) {
        viewModel.getAttemptResult(attemptId).onSuccess { res ->
            resultState = res
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Result Card") }) }
    ) { padding ->
        val res = resultState
        if (res == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(res.examTitle, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Score Obtained: ${res.score} / ${res.totalMarks}")
                        Text("Passing Score: ${res.passingScore}")
                        Text(
                            text = if (res.passed == true) "PASSED" else "FAILED",
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (res.passed == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Answer Details Breakdown", style = MaterialTheme.typography.titleMedium)

                LazyColumn(modifier = Modifier.weight(1f)) {
                    res.answers?.let { answers ->
                        items(answers) { ans ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(ans.questionText)
                                    Text("Your Answer: ${ans.selectedAnswer}", color = if (ans.isCorrect == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                    Text("Correct Answer: ${ans.correctAnswer}")
                                    Text("Marks Awarded: ${ans.marksAwarded} / ${ans.maxMarks}")
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Return to Dashboard")
                }
            }
        }
    }
}
