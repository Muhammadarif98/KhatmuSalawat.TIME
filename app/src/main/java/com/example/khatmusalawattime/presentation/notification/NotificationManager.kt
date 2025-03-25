package com.example.khatmusalawattime.presentation.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.khatmusalawattime.domain.model.ReminderData
import com.example.khatmusalawattime.presentation.ui.components.settings.SettingsPreferences
import java.util.Calendar

/**
 * Планирует ежедневное уведомление на указанное время.
 * @param context Контекст приложения
 * @param reminderData Данные для отображения в уведомлении
 * @param currentDate Текущая дата в формате "d MMMM"
 */
fun scheduleDailyNotification(context: Context, reminderData: ReminderData, currentDate: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    // Получаем сохраненное время для уведомления
    val reminderTime = SettingsPreferences.loadReminderTime(context)
    
    // Создаем Intent для BroadcastReceiver
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("REMINDER_DATA", reminderData)
        putExtra("CURRENT_DATE", currentDate)
        // Добавляем действие для явного интента, чтобы система могла его найти
        action = "com.example.khatmusalawattime.NOTIFICATION"
    }
    
    // Создаем PendingIntent с FLAG_IMMUTABLE для Android 12+
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        flags
    )
    
    // Отменяем предыдущее уведомление
    alarmManager.cancel(pendingIntent)
    
    // Парсим время уведомления
    val timeParts = reminderTime.split(":")
    if (timeParts.size < 2) return
    
    val hour = timeParts[0].toIntOrNull() ?: 16
    val minute = timeParts[1].toIntOrNull() ?: 0
    
    // Устанавливаем время для ежедневного уведомления
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        
        // Если указанное время уже прошло сегодня, планируем на завтра
        if (timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    
    Log.d("NotificationManager", "Планирую уведомление на ${calendar.time}, время: $reminderTime")
    
    // Планируем уведомление в зависимости от версии Android
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12+ требует разрешение на использование точных будильников
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            // Если нет разрешения, используем неточный будильник
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.w("NotificationManager", "Нет разрешения на точные будильники, использую неточный будильник")
        }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Android 6-11 позволяет использовать setExactAndAllowWhileIdle
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    } else {
        // Для более старых версий Android
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
    
    // Добавляем дублирующий неточный будильник для подстраховки
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
    
    // Логируем информацию о запланированном уведомлении
    Log.d("NotificationManager", "Уведомление запланировано на ${reminderTime}, дата: ${calendar.time}")
}

/**
 * Отменяет запланированное уведомление.
 * @param context Контекст приложения
 */
fun cancelScheduledNotification(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        action = "com.example.khatmusalawattime.NOTIFICATION"
    }
    
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    
    val pendingIntent = PendingIntent.getBroadcast(
        context, 
        0, 
        intent, 
        flags
    )
    
    alarmManager.cancel(pendingIntent)
    Log.d("NotificationManager", "Запланированное уведомление отменено")
} 