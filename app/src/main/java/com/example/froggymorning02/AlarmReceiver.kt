package com.example.froggymorning02

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmIndex = intent.getIntExtra("ALARM_INDEX", -1)

        val alarmSound = Uri.parse("android.resource://${context.packageName}/${R.raw.notfffy}")
        val mediaPlayer = MediaPlayer.create(context, alarmSound)

        if (alarmIndex != -1) {
            // Обработка основного будильника и ранних оповещений
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
        }
    }
}
