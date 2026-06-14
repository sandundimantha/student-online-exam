package com.exam.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_answers")
data class LocalAnswer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val attemptId: Long,
    val questionId: Long,
    val selectedAnswer: String,
    val lastSavedAt: Long
)
