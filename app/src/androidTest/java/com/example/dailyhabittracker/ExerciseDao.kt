package com.example.dailyhabittracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExerciseDao {

    @Insert
    suspend fun insertExercise(exercise: Exercise)

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("""
        SELECT * FROM exercises
        WHERE date = :date
        ORDER BY id ASC
    """)
    suspend fun getExercisesForDate(date: String): List<Exercise>

    @Query("""
        SELECT COUNT(*) FROM exercises
        WHERE date = :date
    """)
    suspend fun getTotalExercises(date: String): Int

    @Query("""
        SELECT COUNT(*) FROM exercises
        WHERE date = :date AND isCompleted = 1
    """)
    suspend fun getCompletedExercises(date: String): Int
}