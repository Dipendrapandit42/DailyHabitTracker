package com.example.dailyhabittracker

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.util.Calendar

class ReminderActivity : AppCompatActivity() {

    private lateinit var etReminderMessage: EditText
    private lateinit var tvSelectedTime: TextView
    private lateinit var btnSelectTime: Button
    private lateinit var btnSaveReminder: Button
    private lateinit var recyclerViewReminders: RecyclerView

    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var database: HabitDatabase

    private var selectedHour = -1
    private var selectedMinute = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reminder)

        etReminderMessage =
            findViewById(R.id.etReminderMessage)

        tvSelectedTime =
            findViewById(R.id.tvSelectedTime)

        btnSelectTime =
            findViewById(R.id.btnSelectTime)

        btnSaveReminder =
            findViewById(R.id.btnSaveReminder)

        recyclerViewReminders =
            findViewById(R.id.recyclerViewReminders)

        database =
            HabitDatabase.getDatabase(this)

        reminderAdapter = ReminderAdapter(
            emptyList(),

            onEditClick = { reminder ->
                editReminder(reminder)
            },

            onDeleteClick = { reminder ->
                deleteReminder(reminder)
            }
        )

        recyclerViewReminders.layoutManager =
            LinearLayoutManager(this)

        recyclerViewReminders.adapter =
            reminderAdapter

        btnSelectTime.setOnClickListener {
            showTimePicker()
        }

        btnSaveReminder.setOnClickListener {
            saveReminder()
        }

        loadReminders()
    }

    override fun onResume() {
        super.onResume()

        loadReminders()
    }

    private fun loadReminders() {

        lifecycleScope.launch {

            val reminders =
                database.reminderDao()
                    .getAllReminders()

            reminderAdapter.updateReminders(
                reminders
            )
        }
    }

    private fun showTimePicker() {

        val calendar =
            Calendar.getInstance()

        val currentHour =
            calendar.get(Calendar.HOUR_OF_DAY)

        val currentMinute =
            calendar.get(Calendar.MINUTE)

        val timePickerDialog =
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->

                    selectedHour =
                        hourOfDay

                    selectedMinute =
                        minute

                    val formattedTime =
                        String.format(
                            "%02d:%02d",
                            selectedHour,
                            selectedMinute
                        )

                    tvSelectedTime.text =
                        "🕐 Selected Time: ${
                            formatTimeForDisplay(
                                formattedTime
                            )
                        }"
                },
                currentHour,
                currentMinute,
                false
            )

        timePickerDialog.show()
    }

    private fun saveReminder() {

        val message =
            etReminderMessage.text
                .toString()
                .trim()

        if (message.isEmpty()) {

            etReminderMessage.error =
                "Enter alarm message"

            return
        }

        if (
            selectedHour == -1 ||
            selectedMinute == -1
        ) {

            Toast.makeText(
                this,
                "Please select alarm time",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (!hasExactAlarmPermission()) {
            return
        }

        val time =
            String.format(
                "%02d:%02d",
                selectedHour,
                selectedMinute
            )

        val reminder =
            Reminder(
                time = time,
                message = message,
                isEnabled = true
            )

        lifecycleScope.launch {

            val reminderId =
                database.reminderDao()
                    .insertReminder(
                        reminder
                    )

            val savedReminder =
                reminder.copy(
                    id = reminderId.toInt()
                )

            ReminderScheduler.scheduleReminder(
                this@ReminderActivity,
                savedReminder
            )

            Toast.makeText(
                this@ReminderActivity,
                "Alarm set successfully ⏰",
                Toast.LENGTH_SHORT
            ).show()

            clearForm()

            loadReminders()
        }
    }

    private fun editReminder(
        reminder: Reminder
    ) {

        val editText =
            EditText(this)

        editText.setText(
            reminder.message
        )

        editText.setPadding(
            40,
            20,
            40,
            20
        )

        AlertDialog.Builder(this)

            .setTitle(
                "Edit Alarm Message"
            )

            .setView(
                editText
            )

            .setPositiveButton(
                "Next"
            ) { _, _ ->

                val newMessage =
                    editText.text
                        .toString()
                        .trim()

                if (
                    newMessage.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Message cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                showEditTimePicker(
                    reminder,
                    newMessage
                )
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }

    private fun showEditTimePicker(
        reminder: Reminder,
        newMessage: String
    ) {

        val parts =
            reminder.time.split(":")

        val oldHour =
            parts.getOrNull(0)
                ?.toIntOrNull()
                ?: 0

        val oldMinute =
            parts.getOrNull(1)
                ?.toIntOrNull()
                ?: 0

        val timePickerDialog =
            TimePickerDialog(
                this,
                { _, hour, minute ->

                    val newTime =
                        String.format(
                            "%02d:%02d",
                            hour,
                            minute
                        )

                    updateReminder(
                        reminder,
                        newMessage,
                        newTime
                    )
                },
                oldHour,
                oldMinute,
                false
            )

        timePickerDialog.show()
    }

    private fun updateReminder(
        reminder: Reminder,
        newMessage: String,
        newTime: String
    ) {

        if (!hasExactAlarmPermission()) {
            return
        }

        lifecycleScope.launch {

            // Old alarm cancel
            ReminderScheduler.cancelReminder(
                this@ReminderActivity,
                reminder
            )

            val updatedReminder =
                reminder.copy(
                    time = newTime,
                    message = newMessage,
                    isEnabled = true
                )

            database.reminderDao()
                .updateReminder(
                    updatedReminder
                )

            // New alarm schedule
            ReminderScheduler.scheduleReminder(
                this@ReminderActivity,
                updatedReminder
            )

            Toast.makeText(
                this@ReminderActivity,
                "Alarm updated ⏰",
                Toast.LENGTH_SHORT
            ).show()

            loadReminders()
        }
    }

    private fun deleteReminder(
        reminder: Reminder
    ) {

        AlertDialog.Builder(this)

            .setTitle(
                "Delete Alarm"
            )

            .setMessage(
                "Delete this alarm?\n\n" +
                        "${formatTimeForDisplay(reminder.time)}\n" +
                        reminder.message
            )

            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                lifecycleScope.launch {

                    // Cancel scheduled alarm
                    ReminderScheduler.cancelReminder(
                        this@ReminderActivity,
                        reminder
                    )

                    // Delete from database
                    database.reminderDao()
                        .deleteReminder(
                            reminder
                        )

                    Toast.makeText(
                        this@ReminderActivity,
                        "Alarm deleted 🗑️",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadReminders()
                }
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }

    private fun clearForm() {

        etReminderMessage.text.clear()

        selectedHour = -1
        selectedMinute = -1

        tvSelectedTime.text =
            "🕐 Selected Time: Not selected"
    }

    private fun hasExactAlarmPermission(): Boolean {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            val alarmManager =
                getSystemService(
                    ALARM_SERVICE
                ) as AlarmManager

            if (
                !alarmManager.canScheduleExactAlarms()
            ) {

                Toast.makeText(
                    this,
                    "Please allow Alarms & reminders permission",
                    Toast.LENGTH_LONG
                ).show()

                try {

                    val intent =
                        Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.parse(
                                "package:$packageName"
                            )
                        )

                    startActivity(intent)

                } catch (
                    e: Exception
                ) {

                    val intent =
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse(
                                "package:$packageName"
                            )
                        )

                    startActivity(intent)
                }

                return false
            }
        }

        return true
    }

    private fun formatTimeForDisplay(
        time: String
    ): String {

        val parts =
            time.split(":")

        if (parts.size != 2) {
            return time
        }

        val hour =
            parts[0].toIntOrNull()
                ?: return time

        val minute =
            parts[1].toIntOrNull()
                ?: return time

        val amPm =
            if (hour >= 12) {
                "PM"
            } else {
                "AM"
            }

        val displayHour =
            when {

                hour == 0 -> 12

                hour > 12 -> hour - 12

                else -> hour
            }

        return String.format(
            "%02d:%02d %s",
            displayHour,
            minute,
            amPm
        )
    }
}