package com.example.dailyhabittracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val time: String,
    val message: String,
    val isEnabled: Boolean = true
)