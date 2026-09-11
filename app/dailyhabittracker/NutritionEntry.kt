package com.example.dailyhabittracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_entries")
data class NutritionEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Food name
    val foodName: String,

    // Quantity eaten
    val quantity: Double,

    // g or ml
    val unit: String,

    // Calculated nutrition for the quantity consumed
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,

    val vitaminA: Double = 0.0,
    val vitaminC: Double = 0.0,
    val calcium: Double = 0.0,
    val iron: Double = 0.0,

    // Date on which food was consumed
    val date: String
)