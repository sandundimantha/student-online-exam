package com.exam.app.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.exam.app.ui.navigation.Screen
import com.exam.app.viewmodel.AdminViewModel
import com.exam.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val dashboardState by adminViewModel.dashboardState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (val state = dashboardState) {
                is AdminViewModel.AdminDashboardState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is AdminViewModel.AdminDashboardState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                is AdminViewModel.AdminDashboardState.Success -> {
                    val stats = state.stats
                    Text("System Overview", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatCard("Total Students", stats.totalStudents.toString(), Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        StatCard("Total Lecturers", stats.totalLecturers.toString(), Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatCard("Total Exams", stats.totalExams.toString(), Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        StatCard("Pass Rate", String.format("%.1f%%", stats.passRate), Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.navigate(Screen.UserManagement.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Manage Students & Lecturers")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate(Screen.SubjectManagement.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Manage Academic Subjects")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Recent Examination Log", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(stats.recentActivities) { act ->
                            ListItem(
                                headlineContent = { Text(act.title) },
                                supportingContent = { Text(act.message) },
                                trailingContent = { Text(act.timeAgo, style = MaterialTheme.typography.bodySmall) }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(4.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val users by viewModel.usersList.collectAsState()
    var search by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("User Directory") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Launch Create User Dialog */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = {
                    search = it
                    viewModel.loadUsers(it, null, null)
                },
                label = { Text("Search by name or email...") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(users) { user ->
                    ListItem(
                        headlineContent = { Text(user.fullName) },
                        supportingContent = { Text("${user.email} - Role: ${user.role}") },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { /* Update User */ }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { user.id?.let { viewModel.deleteUser(it) } }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectManagementScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjectsList.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Subject Directory") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Create Subject Dialog */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Subject")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn {
                items(subjects) { sub ->
                    ListItem(
                        headlineContent = { Text("[${sub.subjectCode}] ${sub.subjectName}") },
                        supportingContent = { Text(sub.description ?: "") },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { /* Update Subject */ }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { sub.id?.let { viewModel.deleteSubject(it) } }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
