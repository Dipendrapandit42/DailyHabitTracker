package com.example.dailyhabittracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(

    @PrimaryKey
    val id: Int = 1,

    val age: Int = 0,

    val gender: String = "",

    val weightKg: Double = 0.0,

    val heightCm: Double = 0.0,

    val activityLevel: String = "Moderate",

    val goal: String = "Maintain"
)