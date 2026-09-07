package com.example.dailyhabittracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddHabitActivity : AppCompatActivity() {

    private lateinit var etHabitName: EditText
    private lateinit var btnSaveHabit: Button

    private lateinit var database: HabitDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        etHabitName = findViewById(R.id.etHabitName)
        btnSaveHabit = findViewById(R.id.btnSaveHabit)

        database = HabitDatabase.getDatabase(this)

        btnSaveHabit.setOnClickListener {

            val name = etHabitName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.enter_habit_name,
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                database.habitDao().insertHabit(
                    Habit(name = name),
                )

                runOnUiThread {
                    Toast.makeText(
                        this@AddHabitActivity,
                        R.string.habit_added,
                        Toast.LENGTH_SHORT,
                    ).show()

                    finish()
                }
            }
        }
    }
}