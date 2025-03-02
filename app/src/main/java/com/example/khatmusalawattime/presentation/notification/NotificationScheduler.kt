package com.example.khatmusalawattime.presentation.notification

import NotificationWorker
import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.khatmusalawattime.domain.model.ReminderData
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleDailyNotification(reminderData: ReminderData) {
        // Преобразуем reminderData в Data
        val inputData = Data.Builder()
            .putString("datesKhunzakhSalawat", reminderData.datesKhunzakhSalawat.toString())
            .putString("datesKhunzakhHatmu", reminderData.datesKhunzakhHatmu.toString())
            .build()

        // Создаем запрос на ежедневное выполнение в 15:00
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .setInputData(inputData) // Передаем данные
            .build()

        // Планируем задачу
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyNotificationWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        // Вычисляем задержку до 15:00
        val currentTime = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 15)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)

        if (calendar.timeInMillis <= currentTime) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1) // Если 15:00 уже прошло, планируем на завтра
        }

        return calendar.timeInMillis - currentTime
    }
}