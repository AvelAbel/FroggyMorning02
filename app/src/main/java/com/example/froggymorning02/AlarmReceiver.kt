package com.example.froggymorning02

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmType = intent.getStringExtra("ALARM_TYPE")

        val alarmSound = Uri.parse("android.resource://${context.packageName}/${R.raw.notfffy}")
        val mediaPlayer = MediaPlayer.create(context, alarmSound)

        when (alarmType) {
            "ALARM" -> {
                // Обработка основного будильника
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
            }
            "FIRST_ALERT" -> {
                // Обработка раннего оповещения
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
            }
        }
    }
}
