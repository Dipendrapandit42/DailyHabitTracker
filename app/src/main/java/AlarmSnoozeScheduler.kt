package com.example.dailyhabittracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object AlarmSnoozeScheduler {

    fun scheduleSnooze(
        context: Context,
        alarmId: Int,
        message: String
    ) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val intent = Intent(
            context,
            CustomReminderReceiver::class.java
        ).apply {

            putExtra(
                "REMINDER_ID",
                alarmId
            )

            putExtra(
                "REMINDER_MESSAGE",
                message
            )

            putExtra(
                "IS_SNOOZE",
                true
            )
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                alarmId + 50000,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val snoozeTime =
            System.currentTimeMillis() +
                    (5 * 60 * 1000)

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.S
        ) {

            if (
                !alarmManager.canScheduleExactAlarms()
            ) {
                return
            }
        }

        val alarmClockInfo =
            AlarmManager.AlarmClockInfo(
                snoozeTime,
                pendingIntent
            )

        alarmManager.setAlarmClock(
            alarmClockInfo,
            pendingIntent
        )
    }
}