package com.exam.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_exam_attempts")
data class LocalExamAttempt(
    @PrimaryKey val id: Long,
    val examId: Long,
    val examTitle: String,
    val durationMinutes: Int,
    val startedAt: String,
    val isSubmitted: Boolean = false
)
