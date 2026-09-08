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
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {

    private lateinit var btnAddHabit: Button
    private lateinit var tvStreak: TextView
    private lateinit var recyclerViewHabits: RecyclerView
    private lateinit var habitAdapter: HabitAdapter

    private lateinit var database: HabitDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAddHabit = findViewById(R.id.btnAddHabit)
        tvStreak = findViewById(R.id.tvStreak)
        recyclerViewHabits = findViewById(R.id.recyclerViewHabits)

        database = HabitDatabase.getDatabase(this)

        habitAdapter = HabitAdapter(emptyList()) { habit ->
            completeHabit(habit)
        }

        recyclerViewHabits.layoutManager = LinearLayoutManager(this)
        recyclerViewHabits.adapter = habitAdapter

        btnAddHabit.setOnClickListener {
            startActivity(Intent(this, AddHabitActivity::class.java))
        }

        loadHabits()
    }

    override fun onResume() {
        super.onResume()
        loadHabits()
    }

    private fun loadHabits() {
        lifecycleScope.launch {
            val habits = database.habitDao().getAllHabits()

            runOnUiThread {
                habitAdapter.updateHabits(habits)

                val maxStreak = if (habits.isEmpty()) 0
                else habits.maxOf { it.currentStreak }

                tvStreak.text = "🔥 Current Streak: $maxStreak Days"
            }
        }
    }

    private fun completeHabit(habit: Habit) {
        lifecycleScope.launch {

            val today = LocalDate.now()
            val lastDate = habit.lastCompletedDate

            var newStreak = habit.currentStreak

            if (lastDate == null) {
                newStreak = 1
            } else {
                val previousDate = LocalDate.parse(lastDate)
                val daysMissed = ChronoUnit.DAYS.between(previousDate, today) - 1

                newStreak = when {
                    daysMissed <= 0 -> habit.currentStreak
                    daysMissed <= 3 -> habit.currentStreak + 1
                    else -> 1
                }
            }

            val updatedHabit = habit.copy(
                currentStreak = newStreak,
                lastCompletedDate = today.toString(),
                missedDays = 0,
                isCompletedToday = true
            )

            database.habitDao().updateHabit(updatedHabit)

            loadHabits()
        }
    }
}