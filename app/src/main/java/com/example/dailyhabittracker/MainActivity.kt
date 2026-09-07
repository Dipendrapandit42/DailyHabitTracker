package com.example.dailyhabittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var btnAddHabit: Button
    private lateinit var tvStreak: TextView
    private lateinit var tvHabits: TextView

    private lateinit var database: HabitDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAddHabit = findViewById(R.id.btnAddHabit)
        tvStreak = findViewById(R.id.tvStreak)
        tvHabits = findViewById(R.id.tvHabits)

        database = HabitDatabase.getDatabase(this)

        btnAddHabit.setOnClickListener {
            val intent = Intent(this, AddHabitActivity::class.java)
            startActivity(intent)
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

                if (habits.isEmpty()) {

                    tvHabits.text = getString(R.string.no_habits)
                    tvStreak.text = getString(R.string.current_streak, 0)

                } else {

                    tvHabits.text = habits.joinToString("\n\n") {
                        getString(R.string.habit_format, it.name, it.currentStreak)
                    }

                    val maxStreak = habits.maxOf { it.currentStreak }

                    tvStreak.text = getString(R.string.current_streak, maxStreak)
                }
            }
        }
    }
}