package com.example.dailyhabittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExerciseActivity : AppCompatActivity() {

    private lateinit var btnAddExercise: Button
    private lateinit var recyclerViewExercises: RecyclerView
    private lateinit var tvExercisePercentage: TextView
    private lateinit var tvExerciseStatus: TextView
    private lateinit var tvCaloriesBurned: TextView

    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var database: HabitDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_exercise)

        btnAddExercise = findViewById(R.id.btnAddExercise)
        recyclerViewExercises = findViewById(R.id.recyclerViewExercises)
        tvExercisePercentage = findViewById(R.id.tvExercisePercentage)
        tvExerciseStatus = findViewById(R.id.tvExerciseStatus)
        tvCaloriesBurned = findViewById(R.id.tvCaloriesBurned)

        database = HabitDatabase.getDatabase(this)

        exerciseAdapter = ExerciseAdapter(
            emptyList()
        ) { exercise ->
            completeExercise(exercise)
        }

        recyclerViewExercises.layoutManager =
            LinearLayoutManager(this)

        recyclerViewExercises.adapter =
            exerciseAdapter

        btnAddExercise.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddExerciseActivity::class.java
                )
            )
        }

        loadExercises()
    }

    override fun onResume() {
        super.onResume()
        loadExercises()
    }

    private fun loadExercises() {

        lifecycleScope.launch {

            val today =
                LocalDate.now().toString()

            val exercises =
                database.exerciseDao()
                    .getExercisesForDate(today)

            updateExerciseProgress(exercises)
        }
    }

    private fun completeExercise(
        exercise: Exercise
    ) {

        lifecycleScope.launch {

            val updatedExercise =
                exercise.copy(
                    isCompleted = true
                )

            database.exerciseDao()
                .updateExercise(updatedExercise)

            loadExercises()
        }
    }

    private fun updateExerciseProgress(
        exercises: List<Exercise>
    ) {

        val totalExercises =
            exercises.size

        val completedExercises =
            exercises.count {
                it.isCompleted
            }

        val percentage =
            if (totalExercises == 0) {
                0.0
            } else {
                (completedExercises.toDouble()
                        / totalExercises.toDouble()) * 100
            }

        val caloriesBurned =
            exercises
                .filter { it.isCompleted }
                .sumOf { it.caloriesBurned }

        runOnUiThread {

            exerciseAdapter.updateExercises(
                exercises
            )

            tvExercisePercentage.text =
                "📊 Today's Completion: ${
                    String.format(
                        "%.1f",
                        percentage
                    )
                }%"

            tvCaloriesBurned.text =
                "🔥 Calories Burned: ${
                    caloriesBurned.toInt()
                } kcal"

            when {

                totalExercises == 0 -> {

                    tvExerciseStatus.text =
                        "Add exercises for today."

                }

                percentage >= 75.0 -> {

                    tvExerciseStatus.text =
                        "🎉 75%+ completed! Streak continues 🔥"

                }

                else -> {

                    tvExerciseStatus.text =
                        "⚠️ Below 75%. Complete more exercises."
                }
            }
        }
    }
}
