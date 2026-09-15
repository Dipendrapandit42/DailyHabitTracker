package com.example.dailyhabittracker

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddExerciseActivity : AppCompatActivity() {

    private lateinit var etExerciseName: EditText
    private lateinit var etDuration: EditText
    private lateinit var tvCaloriesPreview: TextView

    // New XML uses MaterialCardView
    private lateinit var btnSaveExercise: View

    private lateinit var database: HabitDatabase

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_add_exercise
        )

        // ==========================================
        // FIND VIEWS
        // ==========================================

        etExerciseName =
            findViewById(R.id.etExerciseName)

        etDuration =
            findViewById(R.id.etDuration)

        tvCaloriesPreview =
            findViewById(R.id.tvCaloriesPreview)

        btnSaveExercise =
            findViewById(R.id.btnSaveExercise)

        // ==========================================
        // DATABASE
        // ==========================================

        database =
            HabitDatabase.getDatabase(this)

        // ==========================================
        // LIVE CALORIE PREVIEW
        // ==========================================

        etExerciseName.setOnKeyListener { _, _, _ ->
            updateCaloriesPreview()
            false
        }

        etDuration.setOnKeyListener { _, _, _ ->
            updateCaloriesPreview()
            false
        }

        // ==========================================
        // SAVE EXERCISE
        // ==========================================

        btnSaveExercise.setOnClickListener {

            val exerciseName =
                etExerciseName.text
                    .toString()
                    .trim()

            val duration =
                etDuration.text
                    .toString()
                    .toIntOrNull()

            // ------------------------------------------
            // VALIDATE NAME
            // ------------------------------------------

            if (exerciseName.isEmpty()) {

                etExerciseName.error =
                    "Enter exercise name"

                etExerciseName.requestFocus()

                return@setOnClickListener
            }

            // ------------------------------------------
            // VALIDATE DURATION
            // ------------------------------------------

            if (
                duration == null ||
                duration <= 0
            ) {

                etDuration.error =
                    "Enter valid duration"

                etDuration.requestFocus()

                return@setOnClickListener
            }

            // ==========================================
            // CALCULATE CALORIES
            // ==========================================

            val caloriesBurned =
                calculateCalories(
                    exerciseName,
                    duration
                )

            // ==========================================
            // SAVE TO DATABASE
            // ==========================================

            lifecycleScope.launch {

                val exercise =
                    Exercise(
                        name = exerciseName,
                        durationMinutes = duration,
                        caloriesBurned = caloriesBurned,
                        isCompleted = false,
                        date = LocalDate.now().toString()
                    )

                database.exerciseDao()
                    .insertExercise(
                        exercise
                    )

                runOnUiThread {

                    Toast.makeText(
                        this@AddExerciseActivity,
                        "Exercise added successfully ✅",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        }
    }

    // ==========================================
    // CALORIE PREVIEW
    // ==========================================

    private fun updateCaloriesPreview() {

        val exerciseName =
            etExerciseName.text
                .toString()
                .trim()

        val duration =
            etDuration.text
                .toString()
                .toIntOrNull()

        if (
            exerciseName.isEmpty() ||
            duration == null ||
            duration <= 0
        ) {

            tvCaloriesPreview.text =
                "🔥 Calories Burned: Will be calculated"

            return
        }

        val calories =
            calculateCalories(
                exerciseName,
                duration
            )

        tvCaloriesPreview.text =
            "🔥 Estimated Calories Burned: ${
                calories.toInt()
            } kcal"
    }

    // ==========================================
    // CALCULATE CALORIES
    // ==========================================

    private fun calculateCalories(
        exerciseName: String,
        duration: Int
    ): Double {

        val caloriesPerMinute =
            when {

                exerciseName.contains(
                    "running",
                    ignoreCase = true
                ) -> 10.0

                exerciseName.contains(
                    "cycling",
                    ignoreCase = true
                ) -> 8.0

                exerciseName.contains(
                    "walking",
                    ignoreCase = true
                ) -> 4.0

                exerciseName.contains(
                    "jogging",
                    ignoreCase = true
                ) -> 8.0

                exerciseName.contains(
                    "swimming",
                    ignoreCase = true
                ) -> 9.0

                exerciseName.contains(
                    "jumping",
                    ignoreCase = true
                ) -> 10.0

                exerciseName.contains(
                    "push",
                    ignoreCase = true
                ) -> 7.0

                exerciseName.contains(
                    "squat",
                    ignoreCase = true
                ) -> 6.0

                exerciseName.contains(
                    "plank",
                    ignoreCase = true
                ) -> 5.0

                exerciseName.contains(
                    "yoga",
                    ignoreCase = true
                ) -> 3.0

                else -> 5.0
            }

        return caloriesPerMinute *
                duration
    }
}