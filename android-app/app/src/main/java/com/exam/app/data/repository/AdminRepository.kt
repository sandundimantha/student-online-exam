package com.exam.app.data.repository

import com.exam.app.data.remote.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getDashboardStats(): Result<AdminDashboardDto> = runCatching {
        apiService.getDashboardStats()
    }

    suspend fun getUsers(search: String?, role: String?, status: String?): Result<List<UserDto>> = runCatching {
        apiService.getUsers(search, role, status)
    }

    suspend fun createUser(request: RegisterRequest): Result<UserDto> = runCatching {
        apiService.createUser(request)
    }

    suspend fun updateUser(id: Long, request: UserDto): Result<UserDto> = runCatching {
        apiService.updateUser(id, request)
    }

    suspend fun deleteUser(id: Long): Result<Unit> = runCatching {
        apiService.deleteUser(id)
        Unit
    }

    suspend fun getSubjects(): Result<List<SubjectDto>> = runCatching {
        apiService.getSubjects()
    }

    suspend fun createSubject(request: SubjectDto): Result<SubjectDto> = runCatching {
        apiService.createSubject(request)
    }

    suspend fun updateSubject(id: Long, request: SubjectDto): Result<SubjectDto> = runCatching {
        apiService.updateSubject(id, request)
    }

    suspend fun deleteSubject(id: Long): Result<Unit> = runCatching {
        apiService.deleteSubject(id)
        Unit
    }
}
