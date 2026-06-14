package com.exam.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.app.data.remote.*
import com.exam.app.data.repository.LecturerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LecturerViewModel @Inject constructor(
    private val lecturerRepository: LecturerRepository
) : ViewModel() {

    private val _exams = MutableStateFlow<List<ExamDto>>(emptyList())
    val exams: StateFlow<List<ExamDto>> = _exams.asStateFlow()

    private val _questions = MutableStateFlow<List<QuestionBankDto>>(emptyList())
    val questions: StateFlow<List<QuestionBankDto>> = _questions.asStateFlow()

    private val _attemptResults = MutableStateFlow<List<AttemptResultDto>>(emptyList())
    val attemptResults: StateFlow<List<AttemptResultDto>> = _attemptResults.asStateFlow()

    fun loadExams() {
        viewModelScope.launch {
            lecturerRepository.getLecturerExams()
                .onSuccess { list -> _exams.value = list }
        }
    }

    fun createExam(title: String, description: String, durationMinutes: Int, passingScore: Double, subjectId: Long, questions: List<ExamQuestionRequest>) {
        viewModelScope.launch {
            lecturerRepository.createExam(CreateExamRequest(title, description, durationMinutes, passingScore, subjectId, questions))
                .onSuccess { loadExams() }
        }
    }

    fun updateExam(id: Long, title: String, description: String, durationMinutes: Int, passingScore: Double, subjectId: Long, questions: List<ExamQuestionRequest>) {
        viewModelScope.launch {
            lecturerRepository.updateExam(id, CreateExamRequest(title, description, durationMinutes, passingScore, subjectId, questions))
                .onSuccess { loadExams() }
        }
    }

    fun publishExam(id: Long) {
        viewModelScope.launch {
            lecturerRepository.publishExam(id)
                .onSuccess { loadExams() }
        }
    }

    fun deleteExam(id: Long) {
        viewModelScope.launch {
            lecturerRepository.deleteExam(id)
                .onSuccess { loadExams() }
        }
    }

    fun loadQuestions(subjectId: Long) {
        viewModelScope.launch {
            lecturerRepository.getQuestions(subjectId)
                .onSuccess { list -> _questions.value = list }
        }
    }

    fun createQuestion(subjectId: Long, text: String, type: String, a: String?, b: String?, c: String?, d: String?, answer: String, difficulty: String) {
        viewModelScope.launch {
            lecturerRepository.createQuestion(
                QuestionBankDto(null, subjectId, null, text, type, a, b, c, d, answer, difficulty)
            ).onSuccess { loadQuestions(subjectId) }
        }
    }

    fun updateQuestion(id: Long, subjectId: Long, text: String, type: String, a: String?, b: String?, c: String?, d: String?, answer: String, difficulty: String) {
        viewModelScope.launch {
            lecturerRepository.updateQuestion(
                id, QuestionBankDto(id, subjectId, null, text, type, a, b, c, d, answer, difficulty)
            ).onSuccess { loadQuestions(subjectId) }
        }
    }

    fun deleteQuestion(id: Long, subjectId: Long) {
        viewModelScope.launch {
            lecturerRepository.deleteQuestion(id)
                .onSuccess { loadQuestions(subjectId) }
        }
    }

    fun loadExamResults(examId: Long) {
        viewModelScope.launch {
            lecturerRepository.getExamResults(examId)
                .onSuccess { list -> _attemptResults.value = list }
        }
    }
}
