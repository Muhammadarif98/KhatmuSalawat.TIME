package com.example.khatmusalawattime.presentation.notification


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.khatmusalawattime.domain.model.ReminderData
import java.util.Calendar

@SuppressLint("ScheduleExactAlarm")
fun scheduleDailyNotification(context: Context, reminderData: ReminderData, currentDate: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        // Передаем данные в Intent
        putExtra("REMINDER_DATA", reminderData)
        putExtra("CURRENT_DATE", currentDate)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        // add(Calendar.MINUTE, 2)
        set(Calendar.HOUR_OF_DAY, 16) // Установите время, например, 8:00 утра
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    // Если выбранное время уже прошло сегодня, установите его на завтра
    if (Calendar.getInstance().after(calendar)) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    // Установите повторяющийся будильник
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}