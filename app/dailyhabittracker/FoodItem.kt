package com.example.dailyhabittracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItem(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Food name
    val name: String,

    // Nutrients per 100 g / 100 ml
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,

    // Vitamins and minerals
    val vitaminA: Double = 0.0,
    val vitaminC: Double = 0.0,
    val calcium: Double = 0.0,
    val iron: Double = 0.0
)