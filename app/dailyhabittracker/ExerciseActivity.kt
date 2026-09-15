package com.example.dailyhabittracker

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExerciseActivity : AppCompatActivity() {

    // New XML uses MaterialCardView
    private lateinit var btnAddExercise: View
    private lateinit var btnExerciseReminder: View

    private lateinit var recyclerViewExercises: RecyclerView
    private lateinit var tvExercisePercentage: TextView
    private lateinit var tvExerciseStatus: TextView
    private lateinit var tvCaloriesBurned: TextView

    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var database: HabitDatabase

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_exercise
        )

        // ==========================================
        // FIND VIEWS
        // ==========================================

        btnAddExercise =
            findViewById(
                R.id.btnAddExercise
            )

        btnExerciseReminder =
            findViewById(
                R.id.btnExerciseReminder
            )

        recyclerViewExercises =
            findViewById(
                R.id.recyclerViewExercises
            )

        tvExercisePercentage =
            findViewById(
                R.id.tvExercisePercentage
            )

        tvExerciseStatus =
            findViewById(
                R.id.tvExerciseStatus
            )

        tvCaloriesBurned =
            findViewById(
                R.id.tvCaloriesBurned
            )

        // ==========================================
        // DATABASE
        // ==========================================

        database =
            HabitDatabase.getDatabase(this)

        // ==========================================
        // ADAPTER
        // ==========================================

        exerciseAdapter =
            ExerciseAdapter(
                emptyList(),

                onCompleteClick = { exercise ->
                    completeExercise(
                        exercise
                    )
                },

                onEditClick = { exercise ->
                    editExercise(
                        exercise
                    )
                },

                onDeleteClick = { exercise ->
                    deleteExercise(
                        exercise
                    )
                }
            )

        recyclerViewExercises.layoutManager =
            LinearLayoutManager(this)

        recyclerViewExercises.adapter =
            exerciseAdapter

        // ==========================================
        // ADD EXERCISE
        // ==========================================

        btnAddExercise.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddExerciseActivity::class.java
                )
            )
        }

        // ==========================================
        // EXERCISE REMINDER
        // ==========================================

        btnExerciseReminder.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ReminderActivity::class.java
                )
            )
        }

        // ==========================================
        // LOAD EXERCISES
        // ==========================================

        loadExercises()
    }

    override fun onResume() {
        super.onResume()

        loadExercises()
    }

    // ==========================================
    // LOAD EXERCISES
    // ==========================================

    private fun loadExercises() {

        lifecycleScope.launch {

            val today =
                LocalDate.now().toString()

            val exercises =
                database.exerciseDao()
                    .getExercisesForDate(
                        today
                    )

            updateExerciseProgress(
                exercises
            )
        }
    }

    // ==========================================
    // COMPLETE EXERCISE
    // ==========================================

    private fun completeExercise(
        exercise: Exercise
    ) {

        lifecycleScope.launch {

            val updatedExercise =
                exercise.copy(
                    isCompleted = true
                )

            database.exerciseDao()
                .updateExercise(
                    updatedExercise
                )

            loadExercises()
        }
    }

    // ==========================================
    // EDIT EXERCISE
    // ==========================================

    private fun editExercise(
        exercise: Exercise
    ) {

        val editText =
            EditText(this)

        editText.setText(
            exercise.name
        )

        editText.setSingleLine(true)

        editText.setPadding(
            40,
            20,
            40,
            20
        )

        AlertDialog.Builder(this)
            .setTitle(
                "Edit Exercise"
            )
            .setView(
                editText
            )
            .setPositiveButton(
                "Save"
            ) { _, _ ->

                val newName =
                    editText.text
                        .toString()
                        .trim()

                if (
                    newName.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Exercise name cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    lifecycleScope.launch {

                        val updatedExercise =
                            exercise.copy(
                                name = newName
                            )

                        database.exerciseDao()
                            .updateExercise(
                                updatedExercise
                            )

                        loadExercises()
                    }
                }
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    // ==========================================
    // DELETE EXERCISE
    // ==========================================

    private fun deleteExercise(
        exercise: Exercise
    ) {

        AlertDialog.Builder(this)
            .setTitle(
                "Delete Exercise"
            )
            .setMessage(
                "Are you sure you want to delete \"${exercise.name}\"?"
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                lifecycleScope.launch {

                    database.exerciseDao()
                        .deleteExercise(
                            exercise
                        )

                    Toast.makeText(
                        this@ExerciseActivity,
                        "Exercise deleted",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadExercises()
                }
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    // ==========================================
    // UPDATE PROGRESS
    // ==========================================

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

                (
                        completedExercises.toDouble()
                                / totalExercises.toDouble()
                        ) * 100
            }

        // ==========================================
        // CALORIES BURNED
        // ==========================================

        val caloriesBurned =
            exercises
                .filter {
                    it.isCompleted
                }
                .sumOf {
                    it.caloriesBurned
                }

        // ==========================================
        // UPDATE UI
        // ==========================================

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

                // No exercises
                totalExercises == 0 -> {

                    tvExerciseStatus.text =
                        "Add exercises for today."
                }

                // 75% or more
                percentage >= 75.0 -> {

                    tvExerciseStatus.text =
                        "🎉 75%+ completed! Streak continues 🔥"
                }

                // Less than 75%
                else -> {

                    tvExerciseStatus.text =
                        "⚠️ Below 75%. Complete more exercises."
                }
            }
        }
    }
}