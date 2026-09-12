package com.example.dailyhabittracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val durationMinutes: Int = 0,

    val caloriesBurned: Double = 0.0,

    val isCompleted: Boolean = false,

    val date: String
)