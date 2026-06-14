package com.exam.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.app.data.remote.AuthResponse
import com.exam.app.data.remote.LoginRequest
import com.exam.app.data.remote.RegisterRequest
import com.exam.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.login(LoginRequest(email, password))
                .onSuccess { response ->
                    _authState.value = AuthState.Success(response)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.localizedMessage ?: "Login failed")
                }
        }
    }

    fun register(fullName: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.register(RegisterRequest(fullName, email, password, role))
                .onSuccess { response ->
                    _authState.value = AuthState.Success(response)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.localizedMessage ?: "Registration failed")
                }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.forgotPassword(email)
                .onSuccess {
                    _authState.value = AuthState.ForgotPasswordSuccess
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.localizedMessage ?: "Password reset failed")
                }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Idle
    }

    fun getRole(): String? = authRepository.getUserRole()
    fun getUserName(): String? = authRepository.getUserName()
    fun getUserEmail(): String? = authRepository.getUserEmail()

    sealed interface AuthState {
        object Idle : AuthState
        object Loading : AuthState
        data class Success(val response: AuthResponse) : AuthState
        object ForgotPasswordSuccess : AuthState
        data class Error(val message: String) : AuthState
    }
}
