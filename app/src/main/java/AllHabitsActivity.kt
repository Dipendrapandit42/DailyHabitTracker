package com.example.dailyhabittracker

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class AllHabitsActivity : AppCompatActivity() {

    private lateinit var recyclerViewAllHabits: RecyclerView
    private lateinit var tvBackAllHabits: TextView
    private lateinit var tvHabitCount: TextView
    private lateinit var cardEmptyHabits:
            com.google.android.material.card.MaterialCardView

    private lateinit var habitAdapter: HabitAdapter
    private lateinit var database: HabitDatabase

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_all_habits
        )

        tvBackAllHabits =
            findViewById(R.id.tvBackAllHabits)

        tvHabitCount =
            findViewById(R.id.tvHabitCount)

        recyclerViewAllHabits =
            findViewById(R.id.recyclerViewAllHabits)

        cardEmptyHabits =
            findViewById(R.id.cardEmptyHabits)

        database =
            HabitDatabase.getDatabase(this)

        // =====================================
        // HABIT ADAPTER
        // =====================================

        habitAdapter =
            HabitAdapter(
                emptyList(),

                onCompleteClick = { habit ->
                    completeHabit(habit)
                },

                onEditClick = { habit ->
                    showEditDialog(habit)
                },

                onDeleteClick = { habit ->
                    showDeleteDialog(habit)
                }
            )

        // =====================================
        // GRID
        // =====================================

        recyclerViewAllHabits.layoutManager =
            GridLayoutManager(
                this,
                2
            )

        recyclerViewAllHabits.adapter =
            habitAdapter

        // =====================================
        // BACK
        // =====================================

        tvBackAllHabits.setOnClickListener {
            finish()
        }

        loadHabits()
    }

    // =========================================
    // ON RESUME
    // =========================================

    override fun onResume() {
        super.onResume()

        loadHabits()
    }

    // =========================================
    // LOAD ALL HABITS
    // =========================================

    private fun loadHabits() {

        lifecycleScope.launch {

            val habits =
                database.habitDao()
                    .getAllHabits()

            val today =
                LocalDate.now()

            val updatedHabits =
                habits.map { habit ->

                    if (
                        habit.lastCompletedDate != null
                    ) {

                        val lastDate =
                            LocalDate.parse(
                                habit.lastCompletedDate
                            )

                        val daysSinceLastCompletion =
                            ChronoUnit.DAYS.between(
                                lastDate,
                                today
                            ).toInt()

                        val missedDays =
                            if (
                                daysSinceLastCompletion > 0
                            ) {
                                daysSinceLastCompletion - 1
                            } else {
                                0
                            }

                        if (
                            missedDays > 3
                        ) {

                            // More than 3 missed days
                            // → reset streak to 0
                            habit.copy(
                                currentStreak = 0,
                                missedDays = missedDays,
                                isCompletedToday = false
                            )

                        } else {

                            // Keep current streak
                            habit.copy(
                                missedDays = missedDays,
                                isCompletedToday =
                                    habit.lastCompletedDate ==
                                            today.toString()
                            )
                        }

                    } else {

                        habit.copy(
                            isCompletedToday = false
                        )
                    }
                }

            // =====================================
            // SAVE STATUS
            // =====================================

            updatedHabits.forEach { habit ->

                database.habitDao()
                    .updateHabit(habit)
            }

            runOnUiThread {

                habitAdapter.updateHabits(
                    updatedHabits
                )

                tvHabitCount.text =
                    updatedHabits.size.toString()

                // =================================
                // EMPTY STATE
                // =================================

                if (
                    updatedHabits.isEmpty()
                ) {

                    recyclerViewAllHabits.visibility =
                        View.GONE

                    cardEmptyHabits.visibility =
                        View.VISIBLE

                } else {

                    recyclerViewAllHabits.visibility =
                        View.VISIBLE

                    cardEmptyHabits.visibility =
                        View.GONE
                }
            }
        }
    }

    // =========================================
    // COMPLETE HABIT
    // =========================================

    private fun completeHabit(
        habit: Habit
    ) {

        lifecycleScope.launch {

            val today =
                LocalDate.now()

            val newStreak: Int

            // =================================
            // FIRST COMPLETION
            // =================================

            if (
                habit.lastCompletedDate == null
            ) {

                newStreak = 1

            } else {

                val lastDate =
                    LocalDate.parse(
                        habit.lastCompletedDate
                    )

                val daysSinceLastCompletion =
                    ChronoUnit.DAYS.between(
                        lastDate,
                        today
                    ).toInt()

                // =================================
                // STREAK RULES
                // =================================

                newStreak =
                    when {

                        // Already completed today
                        daysSinceLastCompletion == 0 -> {
                            habit.currentStreak
                        }

                        // Completed on next day
                        daysSinceLastCompletion == 1 -> {
                            habit.currentStreak + 1
                        }

                        // Missed 1–3 days
                        // Streak continues
                        daysSinceLastCompletion in 2..4 -> {
                            habit.currentStreak + 1
                        }

                        // Missed more than 3 days
                        // Start new streak
                        daysSinceLastCompletion > 4 -> {
                            1
                        }

                        else -> {
                            habit.currentStreak
                        }
                    }
            }

            // =================================
            // UPDATE HABIT
            // =================================

            val updatedHabit =
                habit.copy(

                    currentStreak =
                        newStreak,

                    lastCompletedDate =
                        today.toString(),

                    missedDays =
                        0,

                    isCompletedToday =
                        true
                )

            database.habitDao()
                .updateHabit(
                    updatedHabit
                )

            loadHabits()
        }
    }

    // =========================================
    // EDIT HABIT
    // =========================================

    private fun showEditDialog(
        habit: Habit
    ) {

        val editText =
            EditText(this)

        editText.setText(
            habit.name
        )

        editText.setSelection(
            editText.text.length
        )

        val padding =
            (
                    20 *
                            resources.displayMetrics.density
                    ).toInt()

        editText.setPadding(
            padding,
            padding,
            padding,
            padding
        )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Edit Habit"
                )
                .setMessage(
                    "Change your habit name"
                )
                .setView(
                    editText
                )
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Save",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val newName =
                    editText.text
                        .toString()
                        .trim()

                if (
                    newName.isEmpty()
                ) {

                    editText.error =
                        "Please enter habit name"

                } else {

                    lifecycleScope.launch {

                        val updatedHabit =
                            habit.copy(
                                name = newName
                            )

                        database.habitDao()
                            .updateHabit(
                                updatedHabit
                            )

                        dialog.dismiss()

                        loadHabits()
                    }
                }
            }
        }

        dialog.show()
    }

    // =========================================
    // DELETE HABIT
    // =========================================

    private fun showDeleteDialog(
        habit: Habit
    ) {

        AlertDialog.Builder(this)
            .setTitle(
                "Delete Habit"
            )
            .setMessage(
                "Are you sure you want to delete \"${habit.name}\"?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                lifecycleScope.launch {

                    database.habitDao()
                        .deleteHabit(
                            habit
                        )

                    loadHabits()
                }
            }
            .show()
    }
}