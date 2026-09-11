package com.example.dailyhabittracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddFoodActivity : AppCompatActivity() {

    private lateinit var etFoodName: EditText
    private lateinit var etQuantity: EditText
    private lateinit var btnSaveFood: Button
    private lateinit var tvNutritionPreview: TextView

    private lateinit var database: HabitDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_food)

        etFoodName = findViewById(R.id.etFoodName)
        etQuantity = findViewById(R.id.etQuantity)
        btnSaveFood = findViewById(R.id.btnSaveFood)
        tvNutritionPreview = findViewById(R.id.tvNutritionPreview)

        database = HabitDatabase.getDatabase(this)

        // Add default foods to database if they are not already present
        lifecycleScope.launch {
            addDefaultFoods()
        }

        btnSaveFood.setOnClickListener {

            val foodName =
                etFoodName.text.toString().trim()

            val quantity =
                etQuantity.text.toString().toDoubleOrNull()

            if (foodName.isEmpty()) {
                etFoodName.error = "Enter food name"
                return@setOnClickListener
            }

            if (quantity == null || quantity <= 0) {
                etQuantity.error = "Enter valid quantity"
                return@setOnClickListener
            }

            lifecycleScope.launch {

                val foods =
                    database.foodDao().getAllFoods()

                val food =
                    foods.firstOrNull {
                        it.name.equals(
                            foodName,
                            ignoreCase = true
                        )
                    }

                if (food == null) {

                    runOnUiThread {
                        Toast.makeText(
                            this@AddFoodActivity,
                            "Food not found. Try Rice, Milk, Chicken, Egg or Banana.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    return@launch
                }

                // Food values are stored per 100g
                val multiplier =
                    quantity / 100.0

                val entry =
                    NutritionEntry(
                        foodName = food.name,
                        quantity = quantity,
                        unit = "g",
                        calories =
                            food.calories * multiplier,
                        protein =
                            food.protein * multiplier,
                        carbohydrates =
                            food.carbohydrates * multiplier,
                        fat =
                            food.fat * multiplier,
                        fiber =
                            food.fiber * multiplier,
                        vitaminA =
                            food.vitaminA * multiplier,
                        vitaminC =
                            food.vitaminC * multiplier,
                        calcium =
                            food.calcium * multiplier,
                        iron =
                            food.iron * multiplier,
                        date =
                            LocalDate.now().toString()
                    )

                database.nutritionDao()
                    .insertEntry(entry)

                runOnUiThread {

                    Toast.makeText(
                        this@AddFoodActivity,
                        "Food added successfully ✅",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        }
    }

    private suspend fun addDefaultFoods() {

        val existingFoods =
            database.foodDao().getAllFoods()

        val defaultFoods = listOf(

            FoodItem(
                name = "Rice",
                calories = 130.0,
                protein = 2.7,
                carbohydrates = 28.2,
                fat = 0.3,
                fiber = 0.4,
                vitaminA = 0.0,
                vitaminC = 0.0,
                calcium = 10.0,
                iron = 0.2
            ),

            FoodItem(
                name = "Milk",
                calories = 61.0,
                protein = 3.2,
                carbohydrates = 4.8,
                fat = 3.3,
                fiber = 0.0,
                vitaminA = 46.0,
                vitaminC = 0.0,
                calcium = 113.0,
                iron = 0.0
            ),

            FoodItem(
                name = "Chicken",
                calories = 165.0,
                protein = 31.0,
                carbohydrates = 0.0,
                fat = 3.6,
                fiber = 0.0,
                vitaminA = 13.0,
                vitaminC = 0.0,
                calcium = 15.0,
                iron = 1.0
            ),

            FoodItem(
                name = "Egg",
                calories = 155.0,
                protein = 13.0,
                carbohydrates = 1.1,
                fat = 11.0,
                fiber = 0.0,
                vitaminA = 160.0,
                vitaminC = 0.0,
                calcium = 50.0,
                iron = 1.2
            ),

            FoodItem(
                name = "Banana",
                calories = 89.0,
                protein = 1.1,
                carbohydrates = 22.8,
                fat = 0.3,
                fiber = 2.6,
                vitaminA = 3.0,
                vitaminC = 8.7,
                calcium = 5.0,
                iron = 0.3
            )
        )

        defaultFoods.forEach { defaultFood ->

            val alreadyExists =
                existingFoods.any {
                    it.name.equals(
                        defaultFood.name,
                        ignoreCase = true
                    )
                }

            if (!alreadyExists) {
                database.foodDao()
                    .insertFood(defaultFood)
            }
        }
    }
}