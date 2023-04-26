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
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
        val alarmInfoTextView = findViewById<TextView>(R.id.alarmInfoTextView)

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

        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        val alarmHour = timePicker.hour
        val alarmMinute = timePicker.minute
        val EA = 30
        val n = 15

        val firstAlertTime = alarmHour * 60 + alarmMinute - (EA / 60.0) / Math.pow(2.0, (n - 1).toDouble())
        val selectedDayNames = mutableListOf<String>()

        selectedDays.forEachIndexed { index, isSelected ->
            if (isSelected) {
                selectedDayNames.add(getDayName(index + 1))

                for (i in 0 until n) {
                    val earlyAlertTime = alarmHour * 60 + alarmMinute - (EA / 60.0) / Math.pow(2.0, i.toDouble())

                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, alarmHour)
                        set(Calendar.MINUTE, alarmMinute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        timeInMillis = timeInMillis - earlyAlertTime.toInt() * 60 * 1000
                        set(Calendar.DAY_OF_WEEK, index + 1)
                        if (timeInMillis <= System.currentTimeMillis()) {
                            add(Calendar.DAY_OF_YEAR, 7)
                        }
                    }

                    // ... Создание и установка PendingIntent
                }
            }
        }

        val alarmInfo = "Будильник установлен на ${String.format("%02d:%02d", alarmHour, alarmMinute)} " +
                "для дней: ${selectedDayNames.joinToString(", ")}\nПервое оповещение в ${String.format("%02d:%02d", (firstAlertTime / 60).toInt(), (firstAlertTime % 60).toInt())}"

        findViewById<TextView>(R.id.alarmInfoTextView).text = alarmInfo
    }



    private fun getDayName(dayIndex: Int): String {
        return when (dayIndex) {
            Calendar.SUNDAY -> "Воскресенье"
            Calendar.MONDAY -> "Понедельник"
            Calendar.TUESDAY -> "Вторник"
            Calendar.WEDNESDAY -> "Среда"
            Calendar.THURSDAY -> "Четверг"
            Calendar.FRIDAY -> "Пятница"
            Calendar.SATURDAY -> "Суббота"
            else -> ""
        }
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
