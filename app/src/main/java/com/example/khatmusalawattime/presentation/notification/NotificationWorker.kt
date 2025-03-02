import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        // Получаем данные из inputData
        val datesKhunzakhSalawat = inputData.getString("datesKhunzakhSalawat")
        val datesKhunzakhHatmu = inputData.getString("datesKhunzakhHatmu")

        // Преобразуем строки обратно в Map (если нужно)
        val salawatMap = parseStringToMap(datesKhunzakhSalawat)
        val hatmuMap = parseStringToMap(datesKhunzakhHatmu)

        // Логика для создания уведомления
        showNotification(salawatMap, hatmuMap)
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(salawatMap: Map<String, String>?, hatmuMap: Map<String, String>?) {
        val context = applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений (для Android 8.0 и выше)
        val channelId = "khatmu_salawat_channel"
        val channelName = "Khatmu Salawat Notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance)
        notificationManager.createNotificationChannel(channel)

        // Текущая дата
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM"))

        // Определяем текст уведомления в зависимости от дня недели
        val notificationText = when (LocalDate.now().dayOfWeek) {
            DayOfWeek.THURSDAY -> "Салават: ${salawatMap?.get(currentDate)}"
            DayOfWeek.FRIDAY -> "Сегодня Шазалийский Хатму"
            else -> "Хатму: ${hatmuMap?.get(currentDate)}"
        }

        // Создаем уведомление
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Иконка уведомления
            .setContentTitle("Напоминание") // Заголовок
            .setContentText(notificationText) // Текст уведомления
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Показываем уведомление
        notificationManager.notify(1, notification)
    }

    private fun parseStringToMap(data: String?): Map<String, String>? {
        // Преобразуем строку обратно в Map (если нужно)
        return data?.let {
            it.removePrefix("{").removeSuffix("}")
                .split(",")
                .associate { entry ->
                    val (key, value) = entry.split("=")
                    key.trim() to value.trim()
                }
        }
    }
}