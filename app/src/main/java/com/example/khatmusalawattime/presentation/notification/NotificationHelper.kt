package com.example.khatmusalawattime.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.khatmusalawattime.domain.model.ReminderData
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object NotificationHelper {

    private const val CHANNEL_ID = "khatmu_salawat_channel"
    private const val CHANNEL_NAME = "Khatmu Salawat Notifications"
    private const val NOTIFICATION_ID = 1 // Уникальный ID для уведомления

    /**
     * Показывает уведомление с текстом в зависимости от дня недели.
     *
     * @param context Контекст приложения.
     * @param salawatMap Map с данными о времени Салавата.
     * @param hatmuMap Map с данными о времени Хатму.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification(
        context: Context,
        reminderData:ReminderData,
    ) {
        // Получаем NotificationManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений (если он еще не создан)
        createNotificationChannel(notificationManager)

        // Текущая дата в формате "день месяц"
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM"))

        // Определяем текст уведомления в зависимости от дня недели
        val notificationText = when (LocalDate.now().dayOfWeek) {
            DayOfWeek.THURSDAY -> "Салават: ${reminderData.datesKhunzakhSalawat[currentDate]}"
            DayOfWeek.FRIDAY -> "Сегодня Шазалийский Хатму"
            else -> "Хатму: ${reminderData.datesKhunzakhHatmu[currentDate]}"
        }

        // Логирование текста уведомления
        Log.d("NotificationHelper", "Текст уведомления: $notificationText")

        // Создаем уведомление
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Иконка уведомления
            .setContentTitle("Напоминание") // Заголовок
            .setContentText(notificationText) // Текст уведомления
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Приоритет уведомления
            .setAutoCancel(true) // Уведомление автоматически закрывается при нажатии
            .build()

        // Показываем уведомление
        notificationManager.notify(NOTIFICATION_ID, notification)

        // Логирование успешного показа уведомления
        Log.d("NotificationHelper", "Уведомление успешно показано!")
    }

    /**
     * Создает канал уведомлений (требуется для Android 8.0 и выше).
     *
     * @param notificationManager NotificationManager для создания канала.
     */
    fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Создаем канал с IMPORTANCE_DEFAULT (по умолчанию)
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Канал для уведомлений о времени Салавата и Хатму"
            }

            // Создаем канал в NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationHelper", "Канал уведомлений создан: $CHANNEL_ID")
        }
    }
}