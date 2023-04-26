package com.example.froggymorning02

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.froggymorning02.AlarmReceiver
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val dayButtons: MutableList<Button> = mutableListOf()
    private val selectedDays: MutableList<Boolean> = MutableList(7) { false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(true)
        val setAlarmButton = findViewById<Button>(R.id.setAlarmButton)

        // Получение ссылок на кнопки дней недели
        dayButtons.apply {
            add(findViewById(R.id.sundayButton))
            add(findViewById(R.id.mondayButton))
            add(findViewById(R.id.tuesdayButton))
            add(findViewById(R.id.wednesdayButton))
            add(findViewById(R.id.thursdayButton))
            add(findViewById(R.id.fridayButton))
            add(findViewById(R.id.saturdayButton))
        }

        // Обработка нажатий на кнопки дней недели
        dayButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                selectedDays[index] = !selectedDays[index]
                button.setBackgroundColor(if (selectedDays[index]) Color.GRAY else Color.TRANSPARENT)
            }
        }

        setAlarmButton.setOnClickListener {
            requestExactAlarmPermission()
        }
    }
    private val EXACT_ALARM_PERMISSION_REQUEST_CODE = 100

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasExactAlarmPermission()) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                    EXACT_ALARM_PERMISSION_REQUEST_CODE
                )
            } else {
                setAlarm()
            }
        } else {
            setAlarm()
        }
    }

    private fun hasExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun setAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java)

        // Остальной код для установки будильника

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == EXACT_ALARM_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, устанавливаем будильник
                setAlarm()
            } else {
                // Разрешение не получено, показываем сообщение пользователю
            }
        }
    }
}


