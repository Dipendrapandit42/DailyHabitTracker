package com.example.dailyhabittracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class CustomReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "exercise_alarm_fullscreen_v2"
        private const val CHANNEL_NAME = "Exercise Alarms"
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        // Acquire wake lock
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DailyHabitTracker:AlarmWakeLock")
        wakeLock.acquire(10 * 1000L)

        // -----------------------------------
        // 1. Get alarm data
        // -----------------------------------

        val reminderId =
            intent.getIntExtra(
                "REMINDER_ID",
                intent.getIntExtra(
                    "ALARM_ID",
                    System.currentTimeMillis().toInt()
                )
            )

        val message =
            intent.getStringExtra("REMINDER_MESSAGE")
                ?: intent.getStringExtra("ALARM_MESSAGE")
                ?: "Exercise ka time ho gaya 🏃"

        val reminderTime =
            intent.getStringExtra("REMINDER_TIME")

        // -----------------------------------
        // 2. Full-screen AlarmActivity Intent
        // -----------------------------------

        val alarmIntent =
            Intent(
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
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
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
        // 3. Alarm sound
        // -----------------------------------

        val resId =
            context.resources.getIdentifier(
                "alarm_ringtone",
                "raw",
                context.packageName
            )

        val alarmSoundUri: Uri =
            if (resId != 0) {
                Uri.parse(
                    "android.resource://${context.packageName}/$resId"
                )
            } else {
                RingtoneManager.getDefaultUri(
                    RingtoneManager.TYPE_ALARM
                )
            }

        val audioAttributes =
            AudioAttributes.Builder()
                .setUsage(
                    AudioAttributes.USAGE_ALARM
                )
                .setContentType(
                    AudioAttributes.CONTENT_TYPE_SONIFICATION
                )
                .build()

        // -----------------------------------
        // 4. Create HIGH importance channel
        // -----------------------------------

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {

                    description =
                        "Full-screen exercise and custom alarms"

                    setSound(
                        alarmSoundUri,
                        audioAttributes
                    )

                    enableVibration(true)

                    vibrationPattern =
                        longArrayOf(
                            0,
                            700,
                            400,
                            700
                        )

                    lockscreenVisibility =
                        NotificationCompat.VISIBILITY_PUBLIC
                }

            notificationManager.createNotificationChannel(
                channel
            )
        }

        // -----------------------------------
        // 5. Build full-screen notification
        // -----------------------------------

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
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
                .setVisibility(
                    NotificationCompat.VISIBILITY_PUBLIC
                )
                .setSound(
                    alarmSoundUri,
                    android.media.AudioManager.STREAM_ALARM
                )
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(
                    alarmPendingIntent
                )
                .setFullScreenIntent(
                    alarmPendingIntent,
                    true
                )
                .build()

        // -----------------------------------
        // 6. Show notification
        // -----------------------------------

        notificationManager.notify(
            reminderId,
            notification
        )

        // -----------------------------------
        // 7. Schedule tomorrow's alarm
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