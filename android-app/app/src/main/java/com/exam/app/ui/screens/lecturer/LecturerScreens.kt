package com.exam.app.ui.screens.lecturer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.exam.app.ui.navigation.Screen
import com.exam.app.viewmodel.AuthViewModel
import com.exam.app.viewmodel.LecturerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerDashboardScreen(
    navController: NavController,
    lecturerViewModel: LecturerViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val exams by lecturerViewModel.exams.collectAsState()

    LaunchedEffect(Unit) {
        lecturerViewModel.loadExams()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lecturer Console") },
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.CreateExam.route) }) {
                Icon(Icons.Default.Add, contentDescription = "New Exam")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Drafted & Published Exams", style = MaterialTheme.typography.titleMedium)
                Button(onClick = { navController.navigate(Screen.QuestionBank.route) }) {
                    Text("Question Bank")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(exams) { exam ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(exam.title, style = MaterialTheme.typography.titleMedium)
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(exam.status ?: "DRAFT") }
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Subject: ${exam.subjectName} (${exam.subjectCode})", style = MaterialTheme.typography.bodyMedium)
                            Text("Duration: ${exam.durationMinutes} mins | Pass Score: ${exam.passingScore}", style = MaterialTheme.typography.bodySmall)

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (exam.status == "DRAFT") {
                                    TextButton(onClick = { exam.id?.let { lecturerViewModel.publishExam(it) } }) {
                                        Text("Publish")
                                    }
                                }
                                TextButton(onClick = { exam.id?.let { navController.navigate(Screen.StudentResults.createRoute(it)) } }) {
                                    Text("View Results")
                                }
                                IconButton(onClick = { exam.id?.let { lecturerViewModel.deleteExam(it) } }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExamScreen(
    navController: NavController,
    viewModel: LecturerViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("60") }
    var passScore by remember { mutableStateOf("50") }
    var subjectId by remember { mutableStateOf("1") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Assemble Exam") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Exam Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Instructions / Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passScore,
                onValueChange = { passScore = it },
                label = { Text("Passing Marks") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.createExam(
                        title = title,
                        description = description,
                        durationMinutes = duration.toIntOrNull() ?: 60,
                        passingScore = passScore.toDoubleOrNull() ?: 50.0,
                        subjectId = subjectId.toLongOrNull() ?: 1L,
                        questions = emptyList() // Typically links exam_questions
                    )
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Draft Exam")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankScreen(
    navController: NavController,
    viewModel: LecturerViewModel = hiltViewModel()
) {
    var text by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("MCQ") }
    var difficulty by remember { mutableStateOf("MEDIUM") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Question Bank Builder") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            item {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Question Text") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Question Type: $type")
                Row {
                    RadioButton(selected = type == "MCQ", onClick = { type = "MCQ" })
                    Text("MCQ", modifier = Modifier.align(Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = type == "TRUE_FALSE", onClick = { type = "TRUE_FALSE" })
                    Text("T/F", modifier = Modifier.align(Alignment.CenterVertically))
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (type == "MCQ") {
                    OutlinedTextField(value = optionA, onValueChange = { optionA = it }, label = { Text("Option A") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = optionB, onValueChange = { optionB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = optionC, onValueChange = { optionC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = optionD, onValueChange = { optionD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Correct Answer (Choice A/B/C/D or True/False)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.createQuestion(
                            subjectId = 1L,
                            text = text,
                            type = type,
                            a = if (type == "MCQ") optionA else null,
                            b = if (type == "MCQ") optionB else null,
                            c = if (type == "MCQ") optionC else null,
                            d = if (type == "MCQ") optionD else null,
                            answer = answer,
                            difficulty = difficulty
                        )
                        navController.navigateUp()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Question")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentResultsScreen(
    examId: Long,
    navController: NavController,
    viewModel: LecturerViewModel = hiltViewModel()
) {
    val results by viewModel.attemptResults.collectAsState()

    LaunchedEffect(examId) {
        viewModel.loadExamResults(examId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Student Performance") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(results) { res ->
                ListItem(
                    headlineContent = { Text(res.studentName ?: "") },
                    supportingContent = { Text("Score: ${res.score} | Status: ${res.status}") },
                    trailingContent = {
                        Text(
                            text = if (res.passed == true) "PASSED" else "FAILED",
                            color = if (res.passed == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
