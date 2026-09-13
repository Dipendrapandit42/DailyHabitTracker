package com.example.dailyhabittracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class CustomReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val reminderId =
            intent.getIntExtra(
                "REMINDER_ID",
                System.currentTimeMillis().toInt()
            )

        val message =
            intent.getStringExtra(
                "REMINDER_MESSAGE"
            ) ?: "Exercise ka time ho gaya 🏃"

        val reminderTime =
            intent.getStringExtra(
                "REMINDER_TIME"
            )

        // -----------------------------------
        // 1. Open full-screen AlarmActivity
        // -----------------------------------

        val alarmIntent = Intent(
            context,
            AlarmActivity::class.java
        ).apply {

            putExtra(
                "ALARM_ID",
                reminderId
            )

            putExtra(
                "ALARM_MESSAGE",
                message
            )

            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        }

        val alarmPendingIntent =
            PendingIntent.getActivity(
                context,
                reminderId + 200000,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        // -----------------------------------
        // 2. Notification Channel
        // -----------------------------------

        val channelId =
            "alarm_reminders"

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    channelId,
                    "Exercise Alarm",
                    NotificationManager.IMPORTANCE_HIGH
                )

            channel.description =
                "Exercise and custom alarm notifications"

            notificationManager.createNotificationChannel(
                channel
            )
        }

        // -----------------------------------
        // 3. Alarm notification
        // -----------------------------------

        val notification =
            NotificationCompat.Builder(
                context,
                channelId
            )
                .setSmallIcon(
                    android.R.drawable.ic_lock_idle_alarm
                )
                .setContentTitle(
                    "⏰ Exercise Alarm"
                )
                .setContentText(
                    message
                )
                .setPriority(
                    NotificationCompat.PRIORITY_MAX
                )
                .setCategory(
                    NotificationCompat.CATEGORY_ALARM
                )
                .setAutoCancel(false)
                .setOngoing(true)
                .setFullScreenIntent(
                    alarmPendingIntent,
                    true
                )
                .build()

        notificationManager.notify(
            reminderId,
            notification
        )

        // -----------------------------------
        // 4. Schedule same alarm for tomorrow
        // -----------------------------------

        if (
            reminderTime != null &&
            reminderTime.contains(":")
        ) {

            val nextReminder =
                Reminder(
                    id = reminderId,
                    time = reminderTime,
                    message = message,
                    isEnabled = true
                )

            ReminderScheduler.scheduleReminder(
                context,
                nextReminder
            )
        }
    }
}