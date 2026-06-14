package com.exam.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: LocalExamAttempt)

    @Query("SELECT * FROM local_exam_attempts WHERE isSubmitted = 0 LIMIT 1")
    fun getActiveAttempt(): Flow<LocalExamAttempt?>

    @Query("UPDATE local_exam_attempts SET isSubmitted = 1 WHERE id = :attemptId")
    suspend fun markSubmitted(attemptId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAnswer(answer: LocalAnswer)

    @Query("SELECT * FROM local_answers WHERE attemptId = :attemptId")
    suspend fun getAnswersForAttempt(attemptId: Long): List<LocalAnswer>

    @Query("SELECT * FROM local_answers WHERE attemptId = :attemptId AND questionId = :questionId LIMIT 1")
    suspend fun getAnswerForQuestion(attemptId: Long, questionId: Long): LocalAnswer?

    @Query("DELETE FROM local_answers WHERE attemptId = :attemptId")
    suspend fun clearAnswersForAttempt(attemptId: Long)

    @Query("DELETE FROM local_exam_attempts WHERE id = :attemptId")
    suspend fun deleteAttempt(attemptId: Long)
}
