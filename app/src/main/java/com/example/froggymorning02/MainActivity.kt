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
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale




class MainActivity : AppCompatActivity() {
    private val dayButtons: MutableList<Button> = mutableListOf()
    private val selectedDays: MutableList<Boolean> = MutableList(7) { false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(true)

        val currentTime = Calendar.getInstance()
        currentTime.add(Calendar.MINUTE, 30)
        timePicker.hour = currentTime.get(Calendar.HOUR_OF_DAY)
        timePicker.minute = currentTime.get(Calendar.MINUTE)

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
                button.setBackgroundColor(if (selectedDays[index]) Color.GRAY else Color.parseColor("#FFBB86FC"))
            }
        }


        setAlarmButton.setOnClickListener {
            requestExactAlarmPermission()
        }
    }

    private val EXACT_ALARM_PERMISSION_REQUEST_CODE = 100

    private fun selectedDaysToString(): String {
        val daysOfWeek = arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
        val selectedDaysText = StringBuilder()
        for (i in selectedDays.indices) {
            if (selectedDays[i]) {
                if (selectedDaysText.isNotEmpty()) {
                    selectedDaysText.append(", ")
                }
                selectedDaysText.append(daysOfWeek[i])
            }
        }
        return selectedDaysText.toString()
    }

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

    private fun setAlert(alarmManager: AlarmManager, alertTime: Long, index: Int, isEarlyAlert: Boolean) {
        if (alertTime > System.currentTimeMillis()) {
            val pendingIntent = createAlarmPendingIntent(index, isEarlyAlert)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent)
        }
    }


    private fun setAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        val alarmInfoTextView = findViewById<TextView>(R.id.alarmInfoTextView)

        val selectedDaysText = selectedDaysToString()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val numberOfAlerts = 8
        val EA = 30 * 60 * 1000 // 30 минут в миллисекундах

        for (j in 0 until 7) {
            if (selectedDays[j]) {
                val dayOfWeek = (j + 1) % 7
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)

                for (i in 0 until numberOfAlerts) {
                    val timeToSubtract = EA * (1 - 0.5.pow(i)).toLong()
                    val alertTime = calendar.timeInMillis - timeToSubtract

                    setAlert(alarmManager, alertTime, i * numberOfAlerts + j, true) // Устанавливаем ранние оповещения
                }

                setAlert(alarmManager, calendar.timeInMillis, numberOfAlerts * 7 + j, false) // Устанавливаем основной будильник
            }
        }


        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val firstAlertTime = calendar.timeInMillis - EA
        val firstAlertTimeText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(firstAlertTime.toLong()))
        val alarmTimeText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(calendar.timeInMillis))

        // Сбрасываем выбор дней недели и цвет кнопок
        for (i in selectedDays.indices) {
            selectedDays[i] = false
            dayButtons[i].setBackgroundColor(Color.parseColor("#FFBB86FC"))
        }


        alarmInfoTextView.text = "Будильник установлен на $alarmTimeText для дней: $selectedDaysText\nПервое оповещение в $firstAlertTimeText"
    }



    private fun createAlarmPendingIntent(index: Int, isEarlyAlert: Boolean): PendingIntent {
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("ALARM_INDEX", index)
        intent.putExtra("IS_EARLY_ALERT", isEarlyAlert)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val uniqueRequestCode = (if (isEarlyAlert) 0 else 1) * 1000 + index
        return PendingIntent.getBroadcast(this, uniqueRequestCode, intent, flags)
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
