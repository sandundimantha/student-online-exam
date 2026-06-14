package com.exam.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.exam.app.ui.navigation.Screen
import com.exam.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            val role = viewModel.getRole()
            when (role) {
                "ADMIN" -> navController.navigate(Screen.AdminDashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
                "LECTURER" -> navController.navigate(Screen.LecturerDashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
                "STUDENT" -> navController.navigate(Screen.StudentDashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Online Exam System") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome Back", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Please login to your account", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (authState is AuthViewModel.AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign In")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route) }) {
                Text("Forgot Password?")
            }

            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text("Don't have an account? Sign Up")
            }

            if (authState is AuthViewModel.AuthState.Error) {
                Text(
                    text = (authState as AuthViewModel.AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("STUDENT") }
    var expanded by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            val role = viewModel.getRole()
            when (role) {
                "ADMIN" -> navController.navigate(Screen.AdminDashboard.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
                "LECTURER" -> navController.navigate(Screen.LecturerDashboard.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
                "STUDENT" -> navController.navigate(Screen.StudentDashboard.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Create Account") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Role: $selectedRole")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Student") },
                        onClick = {
                            selectedRole = "STUDENT"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Lecturer") },
                        onClick = {
                            selectedRole = "LECTURER"
                            expanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (authState is AuthViewModel.AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.register(fullName, email, password, selectedRole) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigateUp() }) {
                Text("Already have an account? Login")
            }

            if (authState is AuthViewModel.AuthState.Error) {
                Text(
                    text = (authState as AuthViewModel.AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Forgot Password") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Reset Password", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Enter your email to receive recovery instructions", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (authState is AuthViewModel.AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.forgotPassword(email) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send Recovery Email")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigateUp() }) {
                Text("Back to Login")
            }

            if (authState is AuthViewModel.AuthState.ForgotPasswordSuccess) {
                Text(
                    text = "If user exists, password reset instruction has been sent.",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            if (authState is AuthViewModel.AuthState.Error) {
                Text(
                    text = (authState as AuthViewModel.AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

