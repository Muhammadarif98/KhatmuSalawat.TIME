package com.example.khatmusalawattime.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.domain.model.ReminderData
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationReceiver", "onReceive вызван: ${intent.action}")
        
        // Если устройство перезагружено, перепланируем уведомления
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("NotificationReceiver", "Устройство перезагружено, перепланирую уведомления")
            val reminderData = loadReminderDataFromJson(context)
            val currentDate = SimpleDateFormat("d MMMM", Locale.getDefault()).format(Date())
            scheduleDailyNotification(context, reminderData, currentDate)
            return
        }
        
        try {
            // Сначала пробуем получить данные из Intent
            var reminderData = intent.getSerializableExtra("REMINDER_DATA") as? ReminderData
            
            // Если данные не доступны в Intent, загружаем из JSON
            if (reminderData == null || reminderData.datesKhunzakhSalawat.isEmpty()) {
                Log.d("NotificationReceiver", "Данные в Intent пусты, загружаю из JSON")
                reminderData = loadReminderDataFromJson(context)
            }
            
            // Получаем текущую дату в формате "d MMMM"
            val currentDate = SimpleDateFormat("d MMMM", Locale.getDefault()).format(Date())
            
            Log.d("NotificationReceiver", "Получено уведомление для даты: $currentDate")
            
            // Проверяем, что данные не пусты
            if (reminderData != null && (reminderData.datesKhunzakhSalawat.isNotEmpty() || reminderData.datesKhunzakhHatmu.isNotEmpty())) {
                // Генерируем текст уведомления
                val displayText = when {
                    isThursday() -> "Салават: ${reminderData.datesKhunzakhSalawat[currentDate] ?: "Загрузка..."}"
                    isFriday() -> "Сегодня Шазалийский Хатму"
                    else -> "Хатму: ${reminderData.datesKhunzakhHatmu[currentDate] ?: "Загрузка..."}"
                }
                
                Log.d("NotificationReceiver", "Текст уведомления: $displayText")
                
                // Показываем уведомление
                showNotification(context, displayText)
                
                // Перепланируем на следующий день
                scheduleTomorrowNotification(context, reminderData)
            } else {
                Log.e("NotificationReceiver", "Данные пусты или не загружены")
                showNotification(context, "Не удалось загрузить данные о времени молитвы")
            }
        } catch (e: Exception) {
            Log.e("NotificationReceiver", "Ошибка при обработке уведомления: ${e.message}", e)
            showNotification(context, "Произошла ошибка при загрузке данных о времени молитвы")
        }
    }

    private fun loadReminderDataFromJson(context: Context): ReminderData {
        try {
            val inputStream = context.resources.openRawResource(R.raw.reminder_data)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            return Gson().fromJson(jsonString, ReminderData::class.java)
        } catch (e: Exception) {
            Log.e("NotificationReceiver", "Ошибка при загрузке данных из JSON: ${e.message}", e)
            return ReminderData(emptyMap(), emptyMap())
        }
    }
    
    private fun scheduleTomorrowNotification(context: Context, reminderData: ReminderData) {
        try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrowDate = SimpleDateFormat("d MMMM", Locale.getDefault()).format(calendar.time)
            
            scheduleDailyNotification(context, reminderData, tomorrowDate)
        } catch (e: Exception) {
            Log.e("NotificationReceiver", "Ошибка при планировании уведомления на завтра: ${e.message}", e)
        }
    }

    private fun showNotification(context: Context, text: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_notification_channel"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Ежедневные уведомления о времени молитв"
            channel.enableVibration(true)
            channel.enableLights(true)
            notificationManager.createNotificationChannel(channel)
        }

        // Используем иконку из ресурсов приложения (если она есть)
        val iconResId = try {
            val resources = context.resources
            val packageName = context.packageName
            resources.getIdentifier("ic_mosque", "drawable", packageName)
        } catch (e: Exception) {
            android.R.drawable.ic_dialog_info
        }
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Хатму и Салават")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setSmallIcon(if (iconResId != 0) iconResId else android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun isThursday(): Boolean {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY
    }

    private fun isFriday(): Boolean {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
    }
}