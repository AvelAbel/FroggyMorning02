package com.example.froggymorning02

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.media.MediaPlayer


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmSound = Uri.parse("android.resource://${context.packageName}/${R.raw.notfffy}")

        val mediaPlayer = MediaPlayer.create(context, alarmSound)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
    }
}
