package com.exam.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.app.data.remote.*
import com.exam.app.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<AdminDashboardState>(AdminDashboardState.Loading)
    val dashboardState: StateFlow<AdminDashboardState> = _dashboardState.asStateFlow()

    private val _usersList = MutableStateFlow<List<UserDto>>(emptyList())
    val usersList: StateFlow<List<UserDto>> = _usersList.asStateFlow()

    private val _subjectsList = MutableStateFlow<List<SubjectDto>>(emptyList())
    val subjectsList: StateFlow<List<SubjectDto>> = _subjectsList.asStateFlow()

    init {
        loadDashboardStats()
        loadUsers(null, null, null)
        loadSubjects()
    }

    fun loadDashboardStats() {
        viewModelScope.launch {
            _dashboardState.value = AdminDashboardState.Loading
            adminRepository.getDashboardStats()
                .onSuccess { stats ->
                    _dashboardState.value = AdminDashboardState.Success(stats)
                }
                .onFailure { error ->
                    _dashboardState.value = AdminDashboardState.Error(error.localizedMessage ?: "Failed to fetch stats")
                }
        }
    }

    fun loadUsers(search: String?, role: String?, status: String?) {
        viewModelScope.launch {
            adminRepository.getUsers(search, role, status)
                .onSuccess { users -> _usersList.value = users }
        }
    }

    fun createUser(fullName: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            adminRepository.createUser(RegisterRequest(fullName, email, password, role))
                .onSuccess { loadUsers(null, null, null) }
        }
    }

    fun updateUser(id: Long, fullName: String, email: String, role: String, status: String) {
        viewModelScope.launch {
            adminRepository.updateUser(id, UserDto(id, fullName, email, role, status))
                .onSuccess { loadUsers(null, null, null) }
        }
    }

    fun deleteUser(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteUser(id)
                .onSuccess { loadUsers(null, null, null) }
        }
    }

    fun loadSubjects() {
        viewModelScope.launch {
            adminRepository.getSubjects()
                .onSuccess { list -> _subjectsList.value = list }
        }
    }

    fun createSubject(name: String, code: String, description: String) {
        viewModelScope.launch {
            adminRepository.createSubject(SubjectDto(null, name, code, description))
                .onSuccess { loadSubjects() }
        }
    }

    fun updateSubject(id: Long, name: String, code: String, description: String) {
        viewModelScope.launch {
            adminRepository.updateSubject(id, SubjectDto(id, name, code, description))
                .onSuccess { loadSubjects() }
        }
    }

    fun deleteSubject(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteSubject(id)
                .onSuccess { loadSubjects() }
        }
    }

    sealed interface AdminDashboardState {
        object Loading : AdminDashboardState
        data class Success(val stats: AdminDashboardDto) : AdminDashboardState
        data class Error(val message: String) : AdminDashboardState
    }
}
