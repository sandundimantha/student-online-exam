package com.exam.app.data.repository

import android.content.SharedPreferences
import com.exam.app.data.remote.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPreferences
) {
    suspend fun login(request: LoginRequest): Result<AuthResponse> = runCatching {
        val response = apiService.login(request)
        saveSession(response)
        response
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse> = runCatching {
        val response = apiService.register(request)
        saveSession(response)
        response
    }

    suspend fun forgotPassword(email: String): Result<Unit> = runCatching {
        apiService.forgotPassword(ForgotPasswordRequest(email))
        Unit
    }

    fun logout() {
        sharedPrefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return !sharedPrefs.getString("jwt_token", null).isNullOrEmpty()
    }

    fun getUserRole(): String? {
        return sharedPrefs.getString("user_role", null)
    }

    fun getUserEmail(): String? {
        return sharedPrefs.getString("user_email", null)
    }

    fun getUserName(): String? {
        return sharedPrefs.getString("user_name", null)
    }

    private fun saveSession(response: AuthResponse) {
        sharedPrefs.edit().apply {
            putString("jwt_token", response.token)
            putString("refresh_token", response.refreshToken)
            putLong("user_id", response.id)
            putString("user_name", response.fullName)
            putString("user_email", response.email)
            putString("user_role", response.role)
        }.apply()
    }
}
