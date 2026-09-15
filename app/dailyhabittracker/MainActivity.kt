package com.example.dailyhabittracker

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    // =========================================
    // QUICK ACTION CARDS
    // =========================================

    private lateinit var btnAddHabit: View
    private lateinit var btnAlarm: View
    private lateinit var btnStatistics: View
    private lateinit var btnNutrition: View
    private lateinit var btnExercise: View
    private lateinit var btnProfile: View

    // =========================================
    // HOME VIEWS
    // =========================================

    private lateinit var tvGreeting: TextView
    private lateinit var tvStreak: TextView
    private lateinit var tvProfileIcon: TextView
    private lateinit var tvViewAllHabits: TextView

    private lateinit var recyclerViewHabits: RecyclerView
    private lateinit var habitAdapter: HabitAdapter

    private lateinit var database: HabitDatabase

    // =========================================
    // ON CREATE
    // =========================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_main
        )

        // =====================================
        // FIND VIEWS
        // =====================================

        btnAddHabit =
            findViewById(R.id.btnAddHabit)

        btnAlarm =
            findViewById(R.id.btnAlarm)

        btnStatistics =
            findViewById(R.id.btnStatistics)

        btnNutrition =
            findViewById(R.id.btnNutrition)

        btnExercise =
            findViewById(R.id.btnExercise)

        btnProfile =
            findViewById(R.id.btnProfile)

        tvGreeting =
            findViewById(R.id.tvGreeting)

        tvStreak =
            findViewById(R.id.tvStreak)

        tvProfileIcon =
            findViewById(R.id.tvProfileIcon)

        tvViewAllHabits =
            findViewById(R.id.tvViewAllHabits)

        recyclerViewHabits =
            findViewById(R.id.recyclerViewHabits)

        // =====================================
        // DATABASE
        // =====================================

        database =
            HabitDatabase.getDatabase(this)

        // =====================================
        // EXACT ALARM PERMISSION
        // =====================================

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            val alarmManager =
                getSystemService(
                    Context.ALARM_SERVICE
                ) as AlarmManager

            if (
                !alarmManager.canScheduleExactAlarms()
            ) {

                try {

                    val intent =
                        Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        )

                    startActivity(intent)

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }
        }

        // =====================================
        // DAILY NOTIFICATIONS
        // =====================================

        NotificationScheduler.scheduleNotifications(
            this
        )

        // =====================================
        // NOTIFICATION PERMISSION
        // =====================================

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    101
                )
            }
        }

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
                    editHabit(habit)
                },

                onDeleteClick = { habit ->
                    deleteHabit(habit)
                }
            )

        // =====================================
        // HABIT GRID
        // =====================================

        recyclerViewHabits.layoutManager =
            GridLayoutManager(
                this,
                2
            )

        recyclerViewHabits.adapter =
            habitAdapter

        // =====================================
        // ADD HABIT
        // =====================================

        btnAddHabit.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddHabitActivity::class.java
                )
            )
        }

        // =====================================
        // ALARM
        // =====================================

        btnAlarm.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ReminderActivity::class.java
                )
            )
        }

        // =====================================
        // NUTRITION
        // =====================================

        btnNutrition.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    NutritionActivity::class.java
                )
            )
        }

        // =====================================
        // EXERCISE
        // =====================================

        btnExercise.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ExerciseActivity::class.java
                )
            )
        }

        // =====================================
        // STATISTICS
        // =====================================

        btnStatistics.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    StatisticsActivity::class.java
                )
            )
        }

        // =====================================
        // PROFILE CARD
        // =====================================

        btnProfile.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        // =====================================
        // PROFILE ICON
        // =====================================

        tvProfileIcon.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        // =====================================
        // VIEW ALL HABITS ARROW
        // =====================================

        tvViewAllHabits.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AllHabitsActivity::class.java
                )
            )
        }

        // =====================================
        // FIRST LOAD
        // =====================================

        updateGreeting()
        loadHabits()
    }

    // =========================================
    // ON RESUME
    // =========================================

    override fun onResume() {
        super.onResume()

        updateGreeting()
        loadHabits()
    }

    // =========================================
    // DYNAMIC GREETING
    // =========================================

    private fun updateGreeting() {

        lifecycleScope.launch {

            val profile =
                database.userProfileDao()
                    .getProfile()

            val savedName =
                profile?.name
                    ?.trim()
                    ?: ""

            val name =
                if (
                    savedName.isNotEmpty()
                ) {
                    savedName
                } else {
                    "there"
                }

            val hour =
                Calendar.getInstance()
                    .get(Calendar.HOUR_OF_DAY)

            val greeting =
                when (hour) {

                    in 5..11 ->
                        "Good Morning ☀️"

                    in 12..16 ->
                        "Good Afternoon 🌤️"

                    in 17..20 ->
                        "Good Evening 🌇"

                    else ->
                        "Good Night 🌙"
                }

            runOnUiThread {

                tvGreeting.text =
                    "$greeting, $name"
            }
        }
    }

    // =========================================
    // LOAD HABITS
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

                            habit.copy(
                                currentStreak = 0,
                                missedDays = missedDays,
                                isCompletedToday = false
                            )

                        } else {

                            habit.copy(
                                missedDays =
                                    missedDays,

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

            // =================================
            // SAVE UPDATED HABIT STATUS
            // =================================

            updatedHabits.forEach { habit ->

                database.habitDao()
                    .updateHabit(
                        habit
                    )
            }

            runOnUiThread {

                habitAdapter.updateHabits(
                    updatedHabits
                )

                // =================================
                // MAX STREAK
                // =================================

                val maxStreak =
                    if (
                        updatedHabits.isEmpty()
                    ) {

                        0

                    } else {

                        updatedHabits.maxOf {
                            it.currentStreak
                        }
                    }

                tvStreak.text =
                    "🔥 Current Streak: $maxStreak Days"
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

            var newStreak =
                habit.currentStreak

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

                val missedDays =
                    if (
                        daysSinceLastCompletion > 0
                    ) {

                        daysSinceLastCompletion - 1

                    } else {

                        0
                    }

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

    private fun editHabit(
        habit: Habit
    ) {

        val editText =
            EditText(this)

        editText.setText(
            habit.name
        )

        editText.setPadding(
            40,
            20,
            40,
            20
        )

        AlertDialog.Builder(this)
            .setTitle(
                "Edit Habit"
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

    // =========================================
    // DELETE HABIT
    // =========================================

    private fun deleteHabit(
        habit: Habit
    ) {

        AlertDialog.Builder(this)
            .setTitle(
                "Delete Habit"
            )
            .setMessage(
                "Are you sure you want to delete \"${habit.name}\"?"
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                lifecycleScope.launch {

                    database.habitDao()
                        .deleteHabit(
                            habit
                        )

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