package com.example.dailyhabittracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val currentStreak: Int = 0,

    val lastCompletedDate: String? = null,

    val missedDays: Int = 0,

    val isCompletedToday: Boolean = false,

    val dailyTarget: Double = 0.0,

    val targetUnit: String = "",

    val todayProgress: Double = 0.0,
)
