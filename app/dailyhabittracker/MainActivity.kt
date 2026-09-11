package com.example.dailyhabittracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {

    private lateinit var btnAddHabit: Button
    private lateinit var btnStatistics: Button
    private lateinit var btnNutrition: Button
    private lateinit var btnProfile: Button
    private lateinit var tvStreak: TextView
    private lateinit var recyclerViewHabits: RecyclerView
    private lateinit var habitAdapter: HabitAdapter

    private lateinit var database: HabitDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAddHabit = findViewById(R.id.btnAddHabit)
        btnStatistics = findViewById(R.id.btnStatistics)
        btnNutrition = findViewById(R.id.btnNutrition)
        btnProfile = findViewById(R.id.btnProfile)
        tvStreak = findViewById(R.id.tvStreak)
        recyclerViewHabits = findViewById(R.id.recyclerViewHabits)

        database = HabitDatabase.getDatabase(this)

        // Schedule daily notifications at 5:00 AM and 5:00 PM
        NotificationScheduler.scheduleNotifications(this)

        // Request notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        habitAdapter = HabitAdapter(
            emptyList(),

            onCompleteClick = { habit ->
                completeHabit(habit)
            },

            onEditClick = { habit ->
                editHabit(habit)
            },

            onDeleteClick = { habit ->
                deleteHabit(habit)
            }
        )

        recyclerViewHabits.layoutManager =
            LinearLayoutManager(this)

        recyclerViewHabits.adapter = habitAdapter

        // Add Habit
        btnAddHabit.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddHabitActivity::class.java
                )
            )
        }

        // Statistics
        btnStatistics.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    StatisticsActivity::class.java
                )
            )
        }

        // Nutrition Tracker
        btnNutrition.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    NutritionActivity::class.java
                )
            )
        }

        // Profile
        btnProfile.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        loadHabits()
    }

    override fun onResume() {
        super.onResume()
        loadHabits()
    }

    private fun loadHabits() {

        lifecycleScope.launch {

            val habits =
                database.habitDao().getAllHabits()

            val today = LocalDate.now()

            val updatedHabits =
                habits.map { habit ->

                    if (habit.lastCompletedDate != null) {

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
                            if (daysSinceLastCompletion > 0)
                                daysSinceLastCompletion - 1
                            else
                                0

                        if (missedDays > 3) {

                            habit.copy(
                                currentStreak = 0,
                                missedDays = missedDays,
                                isCompletedToday = false
                            )

                        } else {

                            habit.copy(
                                missedDays = missedDays,
                                isCompletedToday =
                                    habit.lastCompletedDate ==
                                            today.toString()
                            )
                        }

                    } else {

                        habit
                    }
                }

            updatedHabits.forEach { habit ->

                database.habitDao()
                    .updateHabit(habit)
            }

            runOnUiThread {

                habitAdapter.updateHabits(
                    updatedHabits
                )

                val maxStreak =
                    if (updatedHabits.isEmpty())
                        0
                    else
                        updatedHabits.maxOf {
                            it.currentStreak
                        }

                tvStreak.text =
                    "🔥 Current Streak: $maxStreak Days"
            }
        }
    }

    private fun completeHabit(habit: Habit) {

        lifecycleScope.launch {

            val today = LocalDate.now()

            var newStreak =
                habit.currentStreak

            if (habit.lastCompletedDate == null) {

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

                val missedDays =
                    if (daysSinceLastCompletion > 0)
                        daysSinceLastCompletion - 1
                    else
                        0

                newStreak =
                    when {

                        daysSinceLastCompletion == 0 ->
                            habit.currentStreak

                        missedDays in 1..3 ->
                            habit.currentStreak + 1

                        missedDays > 3 ->
                            1

                        else ->
                            habit.currentStreak
                    }
            }

            val updatedHabit =
                habit.copy(

                    currentStreak =
                        newStreak,

                    lastCompletedDate =
                        today.toString(),

                    missedDays = 0,

                    isCompletedToday = true
                )

            database.habitDao()
                .updateHabit(updatedHabit)

            loadHabits()
        }
    }

    private fun editHabit(habit: Habit) {

        val editText =
            EditText(this)

        editText.setText(habit.name)

        editText.setPadding(
            40,
            20,
            40,
            20
        )

        AlertDialog.Builder(this)

            .setTitle("Edit Habit")

            .setView(editText)

            .setPositiveButton("Save") { _, _ ->

                val newName =
                    editText.text
                        .toString()
                        .trim()

                if (newName.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Habit name cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()

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

                        loadHabits()
                    }
                }
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }

    private fun deleteHabit(habit: Habit) {

        AlertDialog.Builder(this)

            .setTitle("Delete Habit")

            .setMessage(
                "Are you sure you want to delete \"${habit.name}\"?"
            )

            .setPositiveButton("Delete") { _, _ ->

                lifecycleScope.launch {

                    database.habitDao()
                        .deleteHabit(habit)

                    Toast.makeText(
                        this@MainActivity,
                        "Habit deleted",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadHabits()
                }
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }
}