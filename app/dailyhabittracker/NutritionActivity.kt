package com.example.dailyhabittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class NutritionActivity : AppCompatActivity() {

    private lateinit var tvCalorieGoal: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvProteinGoal: TextView
    private lateinit var tvProtein: TextView
    private lateinit var tvProteinRemaining: TextView
    private lateinit var tvCarbs: TextView
    private lateinit var tvFat: TextView
    private lateinit var tvFiber: TextView
    private lateinit var tvVitamins: TextView
    private lateinit var tvFoodLog: TextView
    private lateinit var btnAddFood: Button

    private lateinit var database: HabitDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_nutrition)

        tvCalorieGoal = findViewById(R.id.tvCalorieGoal)
        tvCalories = findViewById(R.id.tvCalories)
        tvProteinGoal = findViewById(R.id.tvProteinGoal)
        tvProtein = findViewById(R.id.tvProtein)
        tvProteinRemaining = findViewById(R.id.tvProteinRemaining)
        tvCarbs = findViewById(R.id.tvCarbs)
        tvFat = findViewById(R.id.tvFat)
        tvFiber = findViewById(R.id.tvFiber)
        tvVitamins = findViewById(R.id.tvVitamins)
        tvFoodLog = findViewById(R.id.tvFoodLog)
        btnAddFood = findViewById(R.id.btnAddFood)

        database = HabitDatabase.getDatabase(this)

        btnAddFood.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddFoodActivity::class.java
                )
            )
        }

        loadNutrition()
    }

    override fun onResume() {
        super.onResume()
        loadNutrition()
    }

    private fun loadNutrition() {

        lifecycleScope.launch {

            val today = LocalDate.now().toString()

            // ---------------- PROFILE ----------------

            val profile =
                database.userProfileDao().getProfile()

            // ---------------- FOOD NUTRITION ----------------

            val calories =
                database.nutritionDao()
                    .getTotalCalories(today)

            val protein =
                database.nutritionDao()
                    .getTotalProtein(today)

            val carbohydrates =
                database.nutritionDao()
                    .getTotalCarbohydrates(today)

            val fat =
                database.nutritionDao()
                    .getTotalFat(today)

            val fiber =
                database.nutritionDao()
                    .getTotalFiber(today)

            val vitaminA =
                database.nutritionDao()
                    .getTotalVitaminA(today)

            val vitaminC =
                database.nutritionDao()
                    .getTotalVitaminC(today)

            val calcium =
                database.nutritionDao()
                    .getTotalCalcium(today)

            val iron =
                database.nutritionDao()
                    .getTotalIron(today)

            val foodEntries =
                database.nutritionDao()
                    .getEntriesForDate(today)

            // ---------------- EXERCISE CALORIES ----------------

            val exercises =
                database.exerciseDao()
                    .getExercisesForDate(today)

            val exerciseCalories =
                exercises
                    .filter { it.isCompleted }
                    .sumOf { it.caloriesBurned }

            // ---------------- PROFILE EXISTS ----------------

            if (profile != null) {

                // Base calorie requirement
                val baseCalorieGoal =
                    NutritionCalculator.calculateCalories(profile)

                // Add calories burned through completed exercise
                val adjustedCalorieGoal =
                    baseCalorieGoal + exerciseCalories

                // Protein goal
                val proteinGoal =
                    NutritionCalculator.calculateProtein(profile)

                val remainingProtein =
                    (proteinGoal - protein)
                        .coerceAtLeast(0.0)

                // Remaining calories
                val remainingCalories =
                    (adjustedCalorieGoal - calories)
                        .coerceAtLeast(0.0)

                runOnUiThread {

                    // ---------------- CALORIES ----------------

                    tvCalorieGoal.text =
                        "🔥 Daily Calorie Goal: ${adjustedCalorieGoal.toInt()} kcal\n" +
                                "Base Goal: ${baseCalorieGoal.toInt()} kcal\n" +
                                "🏃 Exercise Added: ${exerciseCalories.toInt()} kcal"

                    tvCalories.text =
                        "🍎 Calories Consumed: ${calories.toInt()} kcal\n" +
                                "📊 Calories Remaining: ${remainingCalories.toInt()} kcal"

                    // ---------------- PROTEIN ----------------

                    tvProteinGoal.text =
                        "💪 Protein Goal: ${proteinGoal.toInt()} g"

                    tvProtein.text =
                        "Protein Consumed: ${
                            String.format(
                                "%.1f",
                                protein
                            )
                        } g"

                    tvProteinRemaining.text =
                        if (remainingProtein <= 0) {

                            "✅ Protein Goal Completed!"

                        } else {

                            "Protein Remaining: ${
                                String.format(
                                    "%.1f",
                                    remainingProtein
                                )
                            } g"
                        }

                    // ---------------- MACROS ----------------

                    tvCarbs.text =
                        "🍚 Carbohydrates: ${
                            String.format(
                                "%.1f",
                                carbohydrates
                            )
                        } g"

                    tvFat.text =
                        "🥑 Fat: ${
                            String.format(
                                "%.1f",
                                fat
                            )
                        } g"

                    tvFiber.text =
                        "🌾 Fiber: ${
                            String.format(
                                "%.1f",
                                fiber
                            )
                        } g"

                    // ---------------- VITAMINS & MINERALS ----------------

                    tvVitamins.text =
                        "🍊 Vitamin A: ${
                            String.format(
                                "%.1f",
                                vitaminA
                            )
                        } mg\n" +

                                "🍊 Vitamin C: ${
                                    String.format(
                                        "%.1f",
                                        vitaminC
                                    )
                                } mg\n" +

                                "🦴 Calcium: ${
                                    String.format(
                                        "%.1f",
                                        calcium
                                    )
                                } mg\n" +

                                "🩸 Iron: ${
                                    String.format(
                                        "%.1f",
                                        iron
                                    )
                                } mg"

                    // ---------------- FOOD LOG ----------------

                    if (foodEntries.isEmpty()) {

                        tvFoodLog.text =
                            "🍽️ Food Log\nNo food added today."

                    } else {

                        val foodLog =
                            StringBuilder(
                                "🍽️ Food Log\n\n"
                            )

                        foodEntries.forEach { entry ->

                            foodLog.append(
                                "• ${entry.foodName} - " +
                                        "${entry.quantity.toInt()} " +
                                        "${entry.unit}\n"
                            )

                            foodLog.append(
                                "  Calories: " +
                                        "${entry.calories.toInt()} kcal\n"
                            )

                            foodLog.append(
                                "  Protein: " +
                                        String.format(
                                            "%.1f",
                                            entry.protein
                                        ) +
                                        " g\n\n"
                            )
                        }

                        tvFoodLog.text =
                            foodLog.toString()
                    }
                }

            } else {

                // ---------------- NO PROFILE ----------------

                runOnUiThread {

                    tvCalorieGoal.text =
                        "🔥 Calorie Goal: Set Profile First"

                    tvCalories.text =
                        "Calories Consumed: 0 kcal"

                    tvProteinGoal.text =
                        "💪 Protein Goal: Set Profile First"

                    tvProtein.text =
                        "Protein Consumed: 0 g"

                    tvProteinRemaining.text =
                        "Protein Remaining: Set Profile First"

                    tvCarbs.text =
                        "🍚 Carbohydrates: 0 g"

                    tvFat.text =
                        "🥑 Fat: 0 g"

                    tvFiber.text =
                        "🌾 Fiber: 0 g"

                    tvVitamins.text =
                        "🍊 Vitamin A: 0 mg\n" +
                                "🍊 Vitamin C: 0 mg\n" +
                                "🦴 Calcium: 0 mg\n" +
                                "🩸 Iron: 0 mg"

                    tvFoodLog.text =
                        "🍽️ Food Log\nNo food added today."

                    Toast.makeText(
                        this@NutritionActivity,
                        "Please complete your profile first",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}