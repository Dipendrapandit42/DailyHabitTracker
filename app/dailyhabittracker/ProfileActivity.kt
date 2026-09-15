package com.example.dailyhabittracker

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var spGender: Spinner
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var spActivity: Spinner
    private lateinit var spGoal: Spinner

    // New XML uses MaterialCardView
    private lateinit var btnSaveProfile: View

    private lateinit var database: HabitDatabase

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_profile
        )

        // ==========================================
        // FIND VIEWS
        // ==========================================

        etName =
            findViewById(R.id.etName)

        etAge =
            findViewById(R.id.etAge)

        spGender =
            findViewById(R.id.spGender)

        etWeight =
            findViewById(R.id.etWeight)

        etHeight =
            findViewById(R.id.etHeight)

        spActivity =
            findViewById(R.id.spActivity)

        spGoal =
            findViewById(R.id.spGoal)

        btnSaveProfile =
            findViewById(R.id.btnSaveProfile)

        // ==========================================
        // DATABASE
        // ==========================================

        database =
            HabitDatabase.getDatabase(this)

        // ==========================================
        // SETUP SPINNERS
        // ==========================================

        setupSpinners()

        // ==========================================
        // LOAD EXISTING PROFILE
        // ==========================================

        loadProfile()

        // ==========================================
        // SAVE PROFILE
        // ==========================================

        btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    // ==========================================
    // SETUP SPINNERS
    // ==========================================

    private fun setupSpinners() {

        val genderList =
            listOf(
                "Male",
                "Female"
            )

        val activityList =
            listOf(
                "Low",
                "Moderate",
                "High"
            )

        val goalList =
            listOf(
                "Maintain",
                "Gain",
                "Lose"
            )

        // ------------------------------------------
        // GENDER
        // ------------------------------------------

        spGender.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                genderList
            )

        // ------------------------------------------
        // ACTIVITY
        // ------------------------------------------

        spActivity.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                activityList
            )

        // ------------------------------------------
        // GOAL
        // ------------------------------------------

        spGoal.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                goalList
            )
    }

    // ==========================================
    // LOAD PROFILE
    // ==========================================

    private fun loadProfile() {

        lifecycleScope.launch {

            val profile =
                database.userProfileDao()
                    .getProfile()

            if (profile != null) {

                runOnUiThread {

                    etName.setText(
                        profile.name
                    )

                    etAge.setText(
                        profile.age.toString()
                    )

                    etWeight.setText(
                        profile.weightKg.toString()
                    )

                    etHeight.setText(
                        profile.heightCm.toString()
                    )

                    setSpinnerValue(
                        spGender,
                        profile.gender
                    )

                    setSpinnerValue(
                        spActivity,
                        profile.activityLevel
                    )

                    setSpinnerValue(
                        spGoal,
                        profile.goal
                    )
                }
            }
        }
    }

    // ==========================================
    // SET SPINNER VALUE
    // ==========================================

    private fun setSpinnerValue(
        spinner: Spinner,
        value: String
    ) {

        val adapter =
            spinner.adapter
                ?: return

        for (i in 0 until adapter.count) {

            val item =
                adapter.getItem(i)
                    ?.toString()
                    ?: continue

            if (
                item.equals(
                    value,
                    ignoreCase = true
                )
            ) {

                spinner.setSelection(i)

                break
            }
        }
    }

    // ==========================================
    // SAVE PROFILE
    // ==========================================

    private fun saveProfile() {

        val name =
            etName.text
                .toString()
                .trim()

        val age =
            etAge.text
                .toString()
                .toIntOrNull()

        val weight =
            etWeight.text
                .toString()
                .toDoubleOrNull()

        val height =
            etHeight.text
                .toString()
                .toDoubleOrNull()

        // ==========================================
        // VALIDATION
        // ==========================================

        if (name.isEmpty()) {

            etName.error =
                "Please enter your name"

            etName.requestFocus()

            return
        }

        if (age == null) {

            etAge.error =
                "Please enter valid age"

            etAge.requestFocus()

            return
        }

        if (weight == null) {

            etWeight.error =
                "Please enter valid weight"

            etWeight.requestFocus()

            return
        }

        if (height == null) {

            etHeight.error =
                "Please enter valid height"

            etHeight.requestFocus()

            return
        }

        // ==========================================
        // PROFILE OBJECT
        // ==========================================

        val profile =
            UserProfile(
                id = 1,
                name = name,
                age = age,
                gender =
                    spGender.selectedItem
                        .toString(),
                weightKg = weight,
                heightCm = height,
                activityLevel =
                    spActivity.selectedItem
                        .toString(),
                goal =
                    spGoal.selectedItem
                        .toString()
            )

        // ==========================================
        // SAVE TO DATABASE
        // ==========================================

        lifecycleScope.launch {

            val existingProfile =
                database.userProfileDao()
                    .getProfile()

            if (existingProfile == null) {

                database.userProfileDao()
                    .insertProfile(
                        profile
                    )

            } else {

                database.userProfileDao()
                    .updateProfile(
                        profile
                    )
            }

            runOnUiThread {

                Toast.makeText(
                    this@ProfileActivity,
                    "Profile saved successfully! ✅",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }
    }
}