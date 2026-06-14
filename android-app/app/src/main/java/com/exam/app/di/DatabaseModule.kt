package com.exam.app.di

import android.content.Context
import androidx.room.Room
import com.exam.app.data.local.AppDatabase
import com.exam.app.data.local.ExamDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "exam_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideExamDao(database: AppDatabase): ExamDao {
        return database.examDao()
    }
}
