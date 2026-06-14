package com.exam.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.app.data.remote.*
import com.exam.app.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _availableExams = MutableStateFlow<List<ExamDto>>(emptyList())
    val availableExams: StateFlow<List<ExamDto>> = _availableExams.asStateFlow()

    private val _attemptHistory = MutableStateFlow<List<AttemptResultDto>>(emptyList())
    val attemptHistory: StateFlow<List<AttemptResultDto>> = _attemptHistory.asStateFlow()

    private val _activeAttempt = MutableStateFlow<StartAttemptResponse?>(null)
    val activeAttempt: StateFlow<StartAttemptResponse?> = _activeAttempt.asStateFlow()

    private val _timeLeftSeconds = MutableStateFlow(0)
    val timeLeftSeconds: StateFlow<Int> = _timeLeftSeconds.asStateFlow()

    private val _submissionState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submissionState: StateFlow<SubmissionState> = _submissionState.asStateFlow()

    // Temporary storage for answers selected during the exam
    private val answersMap = mutableMapOf<Long, String>()

    private var timerJob: Job? = null
    private var autoSaveJob: Job? = null

    fun loadAvailableExams() {
        viewModelScope.launch {
            studentRepository.getAvailableExams()
                .onSuccess { list -> _availableExams.value = list }
        }
    }

    fun loadAttemptHistory() {
        viewModelScope.launch {
            studentRepository.getAttemptHistory()
                .onSuccess { list -> _attemptHistory.value = list }
        }
    }

    fun startExam(examId: Long) {
        viewModelScope.launch {
            _submissionState.value = SubmissionState.Idle
            studentRepository.startExamAttempt(examId)
                .onSuccess { response ->
                    _activeAttempt.value = response
                    answersMap.clear()
                    
                    // Set up local answer mappings
                    response.questions.forEach { q ->
                        q.savedAnswer?.let { answersMap[q.id] = it }
                    }

                    // Start timer
                    val totalSeconds = response.durationMinutes * 60
                    _timeLeftSeconds.value = totalSeconds
                    startTimer(totalSeconds)

                    // Start 15s autosave ticker
                    startAutoSaveTicker()
                }
                .onFailure { error ->
                    _submissionState.value = SubmissionState.Error(error.localizedMessage ?: "Failed to start exam")
                }
        }
    }

    fun selectAnswer(questionId: Long, answer: String) {
        answersMap[questionId] = answer
        // Save to cache immediately in background
        viewModelScope.launch {
            _activeAttempt.value?.attemptId?.let { attemptId ->
                studentRepository.saveAnswerProgress(attemptId, questionId, answer)
            }
        }
    }

    fun getSelectedAnswer(questionId: Long): String {
        return answersMap[questionId] ?: ""
    }

    private fun startTimer(seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _timeLeftSeconds.value = remaining
            }
            // Timer expired: auto-submit
            submitExam()
        }
    }

    private fun startAutoSaveTicker() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            while (true) {
                delay(15000) // 15 seconds
                val attemptId = _activeAttempt.value?.attemptId
                if (attemptId != null) {
                    answersMap.forEach { (qId, ans) ->
                        studentRepository.saveAnswerProgress(attemptId, qId, ans)
                    }
                }
            }
        }
    }

    fun submitExam() {
        timerJob?.cancel()
        autoSaveJob?.cancel()
        
        val attemptId = _activeAttempt.value?.attemptId ?: return
        
        viewModelScope.launch {
            _submissionState.value = SubmissionState.Submitting
            
            // Sync final answers map
            answersMap.forEach { (qId, ans) ->
                studentRepository.saveAnswerProgress(attemptId, qId, ans)
            }

            studentRepository.submitExamAttempt(attemptId)
                .onSuccess { result ->
                    _submissionState.value = SubmissionState.Success(result)
                    _activeAttempt.value = null
                    loadAvailableExams()
                    loadAttemptHistory()
                }
                .onFailure { error ->
                    _submissionState.value = SubmissionState.Error(error.localizedMessage ?: "Failed to submit exam")
                }
        }
    }

    suspend fun getAttemptResult(attemptId: Long): Result<AttemptResultDto> {
        return studentRepository.getAttemptResult(attemptId)
    }

    sealed interface SubmissionState {
        object Idle : SubmissionState
        object Submitting : SubmissionState
        data class Success(val result: AttemptResultDto) : SubmissionState
        data class Error(val message: String) : SubmissionState
    }
}
