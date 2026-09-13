package com.example.dailyhabittracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        reminder: Reminder
    ) {

        val timeParts = reminder.time.split(":")

        if (timeParts.size != 2) {
            return
        }

        val hour = timeParts[0].toIntOrNull() ?: return
        val minute = timeParts[1].toIntOrNull() ?: return

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Agar aaj ka time nikal chuka hai,
        // alarm kal isi time par chalega.
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val alarmIntent = Intent(
            context,
            CustomReminderReceiver::class.java
        ).apply {
            putExtra("REMINDER_ID", reminder.id)
            putExtra("REMINDER_MESSAGE", reminder.message)
            putExtra("REMINDER_TIME", reminder.time)
        }

        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(
            context,
            ReminderActivity::class.java
        )

        val showPendingIntent = PendingIntent.getActivity(
            context,
            reminder.id + 100000,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        // Android 12+ exact alarm permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (!alarmManager.canScheduleExactAlarms()) {
                return
            }
        }

        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            calendar.timeInMillis,
            showPendingIntent
        )

        // Proper clock-style exact alarm
        alarmManager.setAlarmClock(
            alarmClockInfo,
            alarmPendingIntent
        )
    }

    fun cancelReminder(
        context: Context,
        reminder: Reminder
    ) {

        val intent = Intent(
            context,
            CustomReminderReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }
}