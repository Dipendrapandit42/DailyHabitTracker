package com.example.dailyhabittracker

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class ReminderActivity : AppCompatActivity() {

    private lateinit var etReminderMessage: EditText
    private lateinit var tvSelectedTime: TextView

    private lateinit var btnSelectTime: View
    private lateinit var btnSaveReminder: View

    private lateinit var alarmListContainer: LinearLayout
    private lateinit var tvNoAlarms: TextView

    private lateinit var database: HabitDatabase

    private var selectedHour = -1
    private var selectedMinute = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reminder)

        etReminderMessage = findViewById(R.id.etReminderMessage)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)

        btnSelectTime = findViewById(R.id.btnSelectTime)
        btnSaveReminder = findViewById(R.id.btnSaveReminder)

        alarmListContainer = findViewById(R.id.alarmListContainer)
        tvNoAlarms = findViewById(R.id.tvNoAlarms)

        database = HabitDatabase.getDatabase(this)

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

    // =========================================================
    // LOAD REMINDERS
    // =========================================================

    private fun loadReminders() {

        lifecycleScope.launch {

            val reminders =
                database.reminderDao().getAllReminders()

            runOnUiThread {
                displayReminders(reminders)
            }
        }
    }

    // =========================================================
    // DISPLAY ALL REMINDERS
    // =========================================================

    private fun displayReminders(
        reminders: List<Reminder>
    ) {

        alarmListContainer.removeAllViews()

        if (reminders.isEmpty()) {

            tvNoAlarms.visibility = View.VISIBLE

            return
        }

        tvNoAlarms.visibility = View.GONE

        val inflater =
            LayoutInflater.from(this)

        reminders.forEach { reminder ->

            val itemView =
                inflater.inflate(
                    R.layout.item_reminder,
                    alarmListContainer,
                    false
                )

            val tvReminderTime =
                itemView.findViewById<TextView>(
                    R.id.tvReminderTime
                )

            val tvReminderMessage =
                itemView.findViewById<TextView>(
                    R.id.tvReminderMessage
                )

            val tvReminderStatus =
                itemView.findViewById<TextView>(
                    R.id.tvReminderStatus
                )

            val switchReminder =
                itemView.findViewById<Switch>(
                    R.id.switchReminder
                )

            val tvReminderMore =
                itemView.findViewById<TextView>(
                    R.id.tvReminderMore
                )

            // Time
            tvReminderTime.text =
                "⏰ ${formatTimeForDisplay(reminder.time)}"

            // Message
            tvReminderMessage.text =
                reminder.message

            // Status
            updateStatus(
                tvReminderStatus,
                reminder.isEnabled
            )

            // Switch
            switchReminder.setOnCheckedChangeListener(null)

            switchReminder.isChecked =
                reminder.isEnabled

            switchReminder.setOnCheckedChangeListener {
                    _, isChecked ->

                toggleReminder(
                    reminder,
                    isChecked
                )
            }

            // More menu
            tvReminderMore.setOnClickListener {

                showReminderMenu(
                    tvReminderMore,
                    reminder
                )
            }

            alarmListContainer.addView(itemView)
        }
    }

    // =========================================================
    // MORE MENU
    // =========================================================

    private fun showReminderMenu(
        anchorView: View,
        reminder: Reminder
    ) {

        val popupMenu =
            PopupMenu(
                this,
                anchorView
            )

        popupMenu.menu.add(
            "✏️ Edit"
        )

        popupMenu.menu.add(
            "🗑️ Delete"
        )

        popupMenu.setOnMenuItemClickListener { menuItem ->

            when (
                menuItem.title.toString()
            ) {

                "✏️ Edit" -> {

                    editReminder(
                        reminder
                    )

                    true
                }

                "🗑️ Delete" -> {

                    deleteReminder(
                        reminder
                    )

                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    // =========================================================
    // TIME PICKER
    // =========================================================

    private fun showTimePicker() {

        val calendar =
            Calendar.getInstance()

        TimePickerDialog(
            this,

            { _, hourOfDay, minute ->

                selectedHour = hourOfDay
                selectedMinute = minute

                val formattedTime =
                    String.format(
                        "%02d:%02d",
                        hourOfDay,
                        minute
                    )

                tvSelectedTime.text =
                    "🕐 Selected Time: ${
                        formatTimeForDisplay(
                            formattedTime
                        )
                    }"
            },

            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false

        ).show()
    }

    // =========================================================
    // SAVE NEW REMINDER
    // =========================================================

    private fun saveReminder() {

        val message =
            etReminderMessage.text
                .toString()
                .trim()

        if (message.isEmpty()) {

            etReminderMessage.error =
                "Enter alarm message"

            etReminderMessage.requestFocus()

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
                    .insertReminder(reminder)

            val savedReminder =
                reminder.copy(
                    id = reminderId.toInt()
                )

            ReminderScheduler.scheduleReminder(
                this@ReminderActivity,
                savedReminder
            )

            runOnUiThread {

                Toast.makeText(
                    this@ReminderActivity,
                    "Alarm set successfully ⏰",
                    Toast.LENGTH_SHORT
                ).show()

                clearForm()
            }

            loadReminders()
        }
    }

    // =========================================================
    // ON / OFF
    // =========================================================

    private fun toggleReminder(
        reminder: Reminder,
        isEnabled: Boolean
    ) {

        if (isEnabled) {

            if (!hasExactAlarmPermission()) {

                loadReminders()

                return
            }

            lifecycleScope.launch {

                val updatedReminder =
                    reminder.copy(
                        isEnabled = true
                    )

                database.reminderDao()
                    .updateReminder(
                        updatedReminder
                    )

                ReminderScheduler.scheduleReminder(
                    this@ReminderActivity,
                    updatedReminder
                )

                runOnUiThread {

                    Toast.makeText(
                        this@ReminderActivity,
                        "Alarm turned ON 🔔",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                loadReminders()
            }

        } else {

            lifecycleScope.launch {

                ReminderScheduler.cancelReminder(
                    this@ReminderActivity,
                    reminder
                )

                val updatedReminder =
                    reminder.copy(
                        isEnabled = false
                    )

                database.reminderDao()
                    .updateReminder(
                        updatedReminder
                    )

                runOnUiThread {

                    Toast.makeText(
                        this@ReminderActivity,
                        "Alarm turned OFF 🔕",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                loadReminders()
            }
        }
    }

    // =========================================================
    // EDIT REMINDER
    // =========================================================

    private fun editReminder(
        reminder: Reminder
    ) {

        val editText =
            EditText(this)

        editText.setText(
            reminder.message
        )

        editText.setSingleLine(true)

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

                if (newMessage.isEmpty()) {

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

    // =========================================================
    // EDIT TIME
    // =========================================================

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

        ).show()
    }

    // =========================================================
    // UPDATE REMINDER
    // =========================================================

    private fun updateReminder(
        reminder: Reminder,
        newMessage: String,
        newTime: String
    ) {

        if (!hasExactAlarmPermission()) {
            return
        }

        lifecycleScope.launch {

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

            ReminderScheduler.scheduleReminder(
                this@ReminderActivity,
                updatedReminder
            )

            runOnUiThread {

                Toast.makeText(
                    this@ReminderActivity,
                    "Alarm updated ⏰",
                    Toast.LENGTH_SHORT
                ).show()
            }

            loadReminders()
        }
    }

    // =========================================================
    // DELETE REMINDER
    // =========================================================

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

                    ReminderScheduler.cancelReminder(
                        this@ReminderActivity,
                        reminder
                    )

                    database.reminderDao()
                        .deleteReminder(
                            reminder
                        )

                    runOnUiThread {

                        Toast.makeText(
                            this@ReminderActivity,
                            "Alarm deleted 🗑️",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    loadReminders()
                }
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    // =========================================================
    // STATUS
    // =========================================================

    private fun updateStatus(
        textView: TextView,
        isEnabled: Boolean
    ) {

        if (isEnabled) {

            textView.text =
                "Daily • Alarm ON"

            textView.setTextColor(
                0xFF3FA76A.toInt()
            )

        } else {

            textView.text =
                "Daily • Alarm OFF"

            textView.setTextColor(
                0xFF999999.toInt()
            )
        }
    }

    // =========================================================
    // CLEAR FORM
    // =========================================================

    private fun clearForm() {

        etReminderMessage.text.clear()

        selectedHour = -1
        selectedMinute = -1

        tvSelectedTime.text =
            "🕐 Selected Time: Not selected"
    }

    // =========================================================
    // EXACT ALARM PERMISSION
    // =========================================================

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

                    startActivity(
                        Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.parse(
                                "package:$packageName"
                            )
                        )
                    )

                } catch (
                    e: Exception
                ) {

                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse(
                                "package:$packageName"
                            )
                        )
                    )
                }

                return false
            }
        }

        return true
    }

    // =========================================================
    // TIME FORMAT
    // =========================================================

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