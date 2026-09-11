package com.example.dailyhabittracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object NotificationScheduler {

    fun scheduleNotifications(context: Context) {

        // Morning reminder: 5:00 AM
        scheduleNotification(
            context,
            5,
            0,
            1001
        )

        // Evening reminder: 5:00 PM
        scheduleNotification(
            context,
            17,
            0,
            1002
        )
    }

    private fun scheduleNotification(
        context: Context,
        hour: Int,
        minute: Int,
        requestCode: Int
    ) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val intent =
            Intent(context, ReminderReceiver::class.java)

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // If today's time has already passed,
        // schedule it for tomorrow.
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}