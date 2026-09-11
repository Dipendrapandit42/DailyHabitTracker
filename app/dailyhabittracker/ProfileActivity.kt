package com.example.dailyhabittracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var etAge: EditText
    private lateinit var spGender: Spinner
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var spActivity: Spinner
    private lateinit var spGoal: Spinner
    private lateinit var btnSaveProfile: Button

    private lateinit var database: HabitDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        etAge = findViewById(R.id.etAge)
        spGender = findViewById(R.id.spGender)
        etWeight = findViewById(R.id.etWeight)
        etHeight = findViewById(R.id.etHeight)
        spActivity = findViewById(R.id.spActivity)
        spGoal = findViewById(R.id.spGoal)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        database = HabitDatabase.getDatabase(this)

        setupSpinners()
        loadProfile()

        btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun setupSpinners() {

        val genderList = listOf(
            "Male",
            "Female"
        )

        val activityList = listOf(
            "Low",
            "Moderate",
            "High"
        )

        val goalList = listOf(
            "Maintain",
            "Gain",
            "Lose"
        )

        spGender.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            genderList
        )

        spActivity.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            activityList
        )

        spGoal.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            goalList
        )
    }

    private fun loadProfile() {

        lifecycleScope.launch {

            val profile = database.userProfileDao().getProfile()

            if (profile != null) {

                etAge.setText(profile.age.toString())
                etWeight.setText(profile.weightKg.toString())
                etHeight.setText(profile.heightCm.toString())

                setSpinnerValue(spGender, profile.gender)
                setSpinnerValue(spActivity, profile.activityLevel)
                setSpinnerValue(spGoal, profile.goal)
            }
        }
    }

    private fun setSpinnerValue(
        spinner: Spinner,
        value: String
    ) {

        val adapter = spinner.adapter

        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString()
                    .equals(value, ignoreCase = true)
            ) {
                spinner.setSelection(i)
                break
            }
        }
    }

    private fun saveProfile() {

        val age = etAge.text.toString().toIntOrNull()
        val weight = etWeight.text.toString().toDoubleOrNull()
        val height = etHeight.text.toString().toDoubleOrNull()

        if (age == null || weight == null || height == null) {

            Toast.makeText(
                this,
                "Please enter valid age, weight and height",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val profile = UserProfile(
            id = 1,
            age = age,
            gender = spGender.selectedItem.toString(),
            weightKg = weight,
            heightCm = height,
            activityLevel = spActivity.selectedItem.toString(),
            goal = spGoal.selectedItem.toString()
        )

        lifecycleScope.launch {

            val existingProfile =
                database.userProfileDao().getProfile()

            if (existingProfile == null) {
                database.userProfileDao().insertProfile(profile)
            } else {
                database.userProfileDao().updateProfile(profile)
            }

            runOnUiThread {

                Toast.makeText(
                    this@ProfileActivity,
                    "Profile saved successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }
    }
}