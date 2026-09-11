package com.example.dailyhabittracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodDao {

    @Insert
    suspend fun insertFood(food: FoodItem)

    @Delete
    suspend fun deleteFood(food: FoodItem)

    @Query("SELECT * FROM food_items ORDER BY name ASC")
    suspend fun getAllFoods(): List<FoodItem>

    @Query("SELECT * FROM food_items WHERE name LIKE :searchQuery")
    suspend fun searchFoods(searchQuery: String): List<FoodItem>
}