package com.example.dailyhabittracker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class StatisticsActivity : AppCompatActivity() {

    private lateinit var tvTotalHabits: TextView
    private lateinit var tvCompletedToday: TextView
    private lateinit var tvHighestStreak: TextView
    private lateinit var tvProgress: TextView

    private lateinit var database: HabitDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        tvTotalHabits = findViewById(R.id.tvTotalHabits)
        tvCompletedToday = findViewById(R.id.tvCompletedToday)
        tvHighestStreak = findViewById(R.id.tvHighestStreak)
        tvProgress = findViewById(R.id.tvProgress)

        database = HabitDatabase.getDatabase(this)

        loadStatistics()
    }

    private fun loadStatistics() {

        lifecycleScope.launch {

            val habits =
                database.habitDao().getAllHabits()

            val totalHabits = habits.size

            val completedToday =
                habits.count { it.isCompletedToday }

            val highestStreak =
                if (habits.isEmpty()) {
                    0
                } else {
                    habits.maxOf { it.currentStreak }
                }

            val progress =
                if (totalHabits == 0) {
                    0
                } else {
                    (completedToday * 100) / totalHabits
                }

            runOnUiThread {

                tvTotalHabits.text =
                    "Total Habits: $totalHabits"

                tvCompletedToday.text =
                    "Completed Today: $completedToday"

                tvHighestStreak.text =
                    "🔥 Highest Streak: $highestStreak Days"

                tvProgress.text =
                    "📈 Today's Progress: $progress%"
            }
        }
    }
}