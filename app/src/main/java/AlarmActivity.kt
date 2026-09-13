package com.example.dailyhabittracker

import android.app.NotificationManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity : AppCompatActivity() {

    private lateinit var tvAlarmTitle: TextView
    private lateinit var tvAlarmMessage: TextView
    private lateinit var btnStop: Button
    private lateinit var btnSnooze: Button

    private var ringtone: Ringtone? = null
    private var alarmId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Screen ko lock screen ke upar dikhane ke liye
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        setContentView(R.layout.activity_alarm)

        tvAlarmTitle = findViewById(R.id.tvAlarmTitle)
        tvAlarmMessage = findViewById(R.id.tvAlarmMessage)
        btnStop = findViewById(R.id.btnStopAlarm)
        btnSnooze = findViewById(R.id.btnSnoozeAlarm)

        alarmId = intent.getIntExtra(
            "ALARM_ID",
            0
        )

        val message =
            intent.getStringExtra(
                "ALARM_MESSAGE"
            ) ?: "Exercise ka time ho gaya 🏃"

        tvAlarmTitle.text = "⏰ ALARM"
        tvAlarmMessage.text = message

        startAlarmSound()

        btnStop.setOnClickListener {
            stopAlarm()
        }

        btnSnooze.setOnClickListener {
            snoozeAlarm()
        }
    }

    private fun startAlarmSound() {

        val alarmUri =
            RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_ALARM
            )

        ringtone =
            RingtoneManager.getRingtone(
                this,
                alarmUri
            )

        ringtone?.let {

            if (!it.isPlaying) {
                it.play()
            }
        }
    }

    private fun stopAlarm() {

        // Sound stop
        ringtone?.stop()

        // Notification remove
        val notificationManager =
            getSystemService(
                NOTIFICATION_SERVICE
            ) as NotificationManager

        notificationManager.cancel(
            alarmId
        )

        finish()
    }

    private fun snoozeAlarm() {

        // Current sound stop
        ringtone?.stop()

        // Notification remove
        val notificationManager =
            getSystemService(
                NOTIFICATION_SERVICE
            ) as NotificationManager

        notificationManager.cancel(
            alarmId
        )

        val message =
            intent.getStringExtra(
                "ALARM_MESSAGE"
            ) ?: "Exercise ka time ho gaya 🏃"

        AlarmSnoozeScheduler.scheduleSnooze(
            this,
            alarmId,
            message
        )

        finish()
    }

    override fun onDestroy() {

        ringtone?.stop()

        super.onDestroy()
    }
}