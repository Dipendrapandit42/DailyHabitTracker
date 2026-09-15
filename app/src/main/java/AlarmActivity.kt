package com.example.dailyhabittracker

import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity : AppCompatActivity() {

    private lateinit var ivAlarmImage: ImageView
    private lateinit var tvAlarmIcon: TextView
    private lateinit var tvAlarmTitle: TextView
    private lateinit var tvAlarmMessage: TextView

    // New XML uses MaterialCardView
    private lateinit var btnStop: View
    private lateinit var btnSnooze: View

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var alarmId: Int = 0

    // Alarm images
    private val alarmImages = intArrayOf(
        R.drawable.alarm1,
        R.drawable.alarm2,
        R.drawable.alarm3,
        R.drawable.alarm4,
        R.drawable.alarm5,
        R.drawable.alarm6,
        R.drawable.alarm7,
        R.drawable.alarm8,
        R.drawable.alarm9,
        R.drawable.alarm10
    )

    private var currentImageIndex = 0

    private val imageHandler =
        Handler(Looper.getMainLooper())

    private val imageRunnable =
        object : Runnable {

            override fun run() {

                currentImageIndex++

                if (currentImageIndex >= alarmImages.size) {
                    currentImageIndex = 0
                }

                ivAlarmImage.setImageResource(
                    alarmImages[currentImageIndex]
                )

                imageHandler.postDelayed(
                    this,
                    1000
                )
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        // ==========================================
        // SHOW OVER LOCK SCREEN
        // ==========================================

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O_MR1
        ) {

            setShowWhenLocked(true)
            setTurnScreenOn(true)

            val keyguardManager =
                getSystemService(
                    KEYGUARD_SERVICE
                ) as android.app.KeyguardManager

            try {
                keyguardManager.requestDismissKeyguard(
                    this,
                    null
                )
            } catch (_: Exception) {
            }

        } else {

            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        // ==========================================
        // LAYOUT
        // ==========================================

        setContentView(
            R.layout.activity_alarm
        )

        // ==========================================
        // FIND VIEWS
        // ==========================================

        ivAlarmImage =
            findViewById(
                R.id.ivAlarmImage
            )

        tvAlarmIcon =
            findViewById(
                R.id.tvAlarmIcon
            )

        tvAlarmTitle =
            findViewById(
                R.id.tvAlarmTitle
            )

        tvAlarmMessage =
            findViewById(
                R.id.tvAlarmMessage
            )

        btnStop =
            findViewById(
                R.id.btnStopAlarm
            )

        btnSnooze =
            findViewById(
                R.id.btnSnoozeAlarm
            )

        // ==========================================
        // ALARM DATA
        // ==========================================

        alarmId =
            intent.getIntExtra(
                "ALARM_ID",
                0
            )

        val message =
            intent.getStringExtra(
                "ALARM_MESSAGE"
            ) ?: "Exercise ka time ho gaya 🏃"

        // ==========================================
        // DISPLAY DATA
        // ==========================================

        tvAlarmIcon.text = "⏰"

        tvAlarmTitle.text = "ALARM"

        tvAlarmMessage.text = message

        // ==========================================
        // START IMAGE SLIDESHOW
        // ==========================================

        ivAlarmImage.setImageResource(
            alarmImages[0]
        )

        imageHandler.postDelayed(
            imageRunnable,
            1000
        )

        // ==========================================
        // START SOUND
        // ==========================================

        startCustomAlarmSound()

        // ==========================================
        // START VIBRATION
        // ==========================================

        startVibration()

        // ==========================================
        // STOP BUTTON
        // ==========================================

        btnStop.setOnClickListener {

            stopAlarm()
        }

        // ==========================================
        // SNOOZE BUTTON
        // ==========================================

        btnSnooze.setOnClickListener {

            snoozeAlarm()
        }
    }

    // ==========================================
    // CUSTOM ALARM SOUND
    // ==========================================

    private fun startCustomAlarmSound() {

        try {

            val resId =
                resources.getIdentifier(
                    "alarm_ringtone",
                    "raw",
                    packageName
                )

            val alarmUri =
                if (resId != 0) {

                    Uri.parse(
                        "android.resource://$packageName/$resId"
                    )

                } else {

                    RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_ALARM
                    )
                }

            mediaPlayer =
                MediaPlayer().apply {

                    setDataSource(
                        this@AlarmActivity,
                        alarmUri
                    )

                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(
                                AudioAttributes.USAGE_ALARM
                            )
                            .setContentType(
                                AudioAttributes.CONTENT_TYPE_SONIFICATION
                            )
                            .build()
                    )

                    isLooping = true

                    setVolume(
                        1.0f,
                        1.0f
                    )

                    prepare()

                    start()
                }

            // ==========================================
            // SET ALARM VOLUME TO MAX
            // ==========================================

            val audioManager =
                getSystemService(
                    AUDIO_SERVICE
                ) as android.media.AudioManager

            audioManager.setStreamVolume(
                android.media.AudioManager.STREAM_ALARM,
                audioManager.getStreamMaxVolume(
                    android.media.AudioManager.STREAM_ALARM
                ),
                0
            )

        } catch (e: Exception) {

            e.printStackTrace()

            playFallbackSound()
        }
    }

    // ==========================================
    // FALLBACK SOUND
    // ==========================================

    private fun playFallbackSound() {

        try {

            val alertUri =
                RingtoneManager.getDefaultUri(
                    RingtoneManager.TYPE_ALARM
                )

            mediaPlayer =
                MediaPlayer().apply {

                    setDataSource(
                        this@AlarmActivity,
                        alertUri
                    )

                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(
                                AudioAttributes.USAGE_ALARM
                            )
                            .setContentType(
                                AudioAttributes.CONTENT_TYPE_SONIFICATION
                            )
                            .build()
                    )

                    isLooping = true

                    prepare()

                    start()
                }

        } catch (_: Exception) {
        }
    }

    // ==========================================
    // VIBRATION
    // ==========================================

    private fun startVibration() {

        vibrator =
            getSystemService(
                Context.VIBRATOR_SERVICE
            ) as Vibrator

        try {

            vibrator?.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(
                        0,
                        500,
                        500
                    ),
                    0
                )
            )

        } catch (_: Exception) {
        }
    }

    // ==========================================
    // STOP ALARM
    // ==========================================

    private fun stopAlarm() {

        stopAlarmSound()

        stopVibration()

        imageHandler.removeCallbacks(
            imageRunnable
        )

        val notificationManager =
            getSystemService(
                NOTIFICATION_SERVICE
            ) as NotificationManager

        notificationManager.cancel(
            alarmId
        )

        finish()
    }

    // ==========================================
    // SNOOZE ALARM
    // ==========================================

    private fun snoozeAlarm() {

        stopAlarmSound()

        stopVibration()

        imageHandler.removeCallbacks(
            imageRunnable
        )

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

    // ==========================================
    // STOP SOUND
    // ==========================================

    private fun stopAlarmSound() {

        mediaPlayer?.let {

            try {

                if (it.isPlaying) {
                    it.stop()
                }

            } catch (_: Exception) {
            }

            try {
                it.release()
            } catch (_: Exception) {
            }
        }

        mediaPlayer = null
    }

    // ==========================================
    // STOP VIBRATION
    // ==========================================

    private fun stopVibration() {

        try {
            vibrator?.cancel()
        } catch (_: Exception) {
        }

        vibrator = null
    }

    // ==========================================
    // ACTIVITY DESTROY
    // ==========================================

    override fun onDestroy() {

        imageHandler.removeCallbacks(
            imageRunnable
        )

        stopAlarmSound()

        stopVibration()

        super.onDestroy()
    }
}