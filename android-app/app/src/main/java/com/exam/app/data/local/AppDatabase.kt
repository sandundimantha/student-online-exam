package com.exam.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalExamAttempt::class, LocalAnswer::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun examDao(): ExamDao
}
