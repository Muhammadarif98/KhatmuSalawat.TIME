package com.example.khatmusalawattime.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.khatmusalawattime.domain.model.ReminderData
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Получаем данные из Intent
        val reminderData = intent.getSerializableExtra("REMINDER_DATA") as ReminderData
        val currentDate = intent.getStringExtra("CURRENT_DATE") ?: ""

        // Генерируем текст уведомления
        val displayText = when {
            isThursday() -> "Салават: ${reminderData.datesKhunzakhSalawat[currentDate]}"
            isFriday() -> "Сегодня Шазалийский Хатму"
            else -> "Хатму: ${reminderData.datesKhunzakhHatmu[currentDate]}"
        }

        // Показываем уведомление
        showNotification(context, displayText)
    }

    private fun showNotification(context: Context, text: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_notification_channel"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Напоминание")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
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