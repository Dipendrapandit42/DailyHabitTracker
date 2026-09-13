package com.example.dailyhabittracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            val pendingResult =
                goAsync()

            CoroutineScope(Dispatchers.IO).launch {

                try {

                    val database =
                        HabitDatabase.getDatabase(
                            context
                        )

                    val reminders =
                        database.reminderDao()
                            .getAllReminders()

                    reminders
                        .filter {
                            it.isEnabled
                        }
                        .forEach { reminder ->

                            ReminderScheduler
                                .scheduleReminder(
                                    context,
                                    reminder
                                )
                        }

                } finally {

                    pendingResult.finish()
                }
            }
        }
    }
}