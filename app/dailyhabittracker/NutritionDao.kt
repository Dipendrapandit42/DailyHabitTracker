package com.example.dailyhabittracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NutritionDao {

    @Insert
    suspend fun insertEntry(entry: NutritionEntry)

    @Delete
    suspend fun deleteEntry(entry: NutritionEntry)

    @Query(
        """
        SELECT * FROM nutrition_entries
        WHERE date = :date
        ORDER BY id DESC
        """
    )
    suspend fun getEntriesForDate(date: String): List<NutritionEntry>

    @Query(
        """
        SELECT COALESCE(SUM(protein), 0.0)
        FROM nutrition_entries
        WHERE date = :date
        """
    )
    suspend fun getTotalProtein(date: String): Double

    @Query(
        """
        SELECT COALESCE(SUM(carbohydrates), 0.0)
        FROM nutrition_entries
        WHERE date = :date
        """
    )
    suspend fun getTotalCarbohydrates(date: String): Double

    @Query(
        """
        SELECT COALESCE(SUM(calories), 0.0)
        FROM nutrition_entries
        WHERE date = :date
        """
    )
    suspend fun getTotalCalories(date: String): Double

    @Query(
        """
        SELECT COALESCE(SUM(fat), 0.0)
        FROM nutrition_entries
        WHERE date = :date
        """
    )
    suspend fun getTotalFat(date: String): Double

    @Query(
        """
        SELECT COALESCE(SUM(fiber), 0.0)
        FROM nutrition_entries
        WHERE date = :date
        """
    )
    suspend fun getTotalFiber(date: String): Double

    @Query(
        """
        SELECT COALESCE(SUM(vitaminA), 0.0)
        FROM nutrition_entries
        WHERE date = :date
        """
    )
    suspend fun getTotalVitaminA(date: String): Double

    @Query(
        """
        SELECT COALESCE(SUM(vitaminC), 0.0)
        FROM nutrition_entries
        WHERE date = :date
        """
    )
    suspend fun getTotalVitaminC(date: String): Double

    @Query(
        """
        SELECT COALESCE(SUM(calcium), 0.0)
        FROM nutrition_entries
        WHERE date = :date
        """
    )
    suspend fun getTotalCalcium(date: String): Double

    @Query(
        """
        SELECT COALESCE(SUM(iron), 0.0)
        FROM nutrition_entries
        WHERE date = :date
        """
    )
    suspend fun getTotalIron(date: String): Double
}