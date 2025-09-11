package com.example.khatmusalawattime.presentation.ui.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.khatmusalawattime.MainActivity
import com.example.khatmusalawattime.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "TimerService"

class TimerService : Service() {

    companion object {
        const val CHANNEL_ID = "timer_notification_channel"
        const val NOTIFICATION_ID = 100

        const val ACTION_START = "com.example.khatmusalawattime.ACTION_START"
        const val ACTION_PAUSE = "com.example.khatmusalawattime.ACTION_PAUSE"
        const val ACTION_RESUME = "com.example.khatmusalawattime.ACTION_RESUME"
        const val ACTION_RESET = "com.example.khatmusalawattime.ACTION_RESET"
        const val EXTRA_TIME = "com.example.khatmusalawattime.EXTRA_TIME"
        const val EXTRA_SOUND_ENABLED = "com.example.khatmusalawattime.EXTRA_SOUND_ENABLED"

        // StateFlow для наблюдения за состоянием таймера
        private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
        val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

        // StateFlow для наблюдения за оставшимся временем
        private val _timeLeft = MutableStateFlow<Long>(0)
        val timeLeft: StateFlow<Long> = _timeLeft.asStateFlow()

        // StateFlow для наблюдения за статусом таймера (активен или нет)
        private val _isActive = MutableStateFlow(false)
        val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

        // Метод для сброса состояния при закрытии приложения (чтобы избежать проблем с новым запуском)
        fun resetState() {
            Log.d(TAG, "Сброс состояния Companion объекта")
            _timerState.value = TimerState.Idle
            _timeLeft.value = 0
            _isActive.value = false
        }
    }

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var totalTimeMillis: Long = 0
    private var isTimerRunning = false
    private var mediaPlayer: MediaPlayer? = null
    private var soundEnabled = true
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate - Сервис создан")
        createNotificationChannel()

        // Инициализация вибратора
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand - Получена команда: ${intent?.action}")

        // Получаем режим звука, если он передан
        intent?.getBooleanExtra(EXTRA_SOUND_ENABLED, true)?.let {
            soundEnabled = it
            Log.d(TAG, "Установлен режим звука: ${if (soundEnabled) "Звук включен" else "Вибрация"}")
        }

        when (intent?.action) {
            ACTION_START -> {
                val timeInMinutes = intent.getLongExtra(EXTRA_TIME, 5)
                Log.d(TAG, "Запуск таймера на $timeInMinutes минут")
                startTimer(timeInMinutes * 60 * 1000)
            }
            ACTION_PAUSE -> {
                Log.d(TAG, "Пауза таймера")
                pauseTimer()
            }
            ACTION_RESUME -> {
                Log.d(TAG, "Возобновление таймера")
                resumeTimer()
            }
            ACTION_RESET -> {
                Log.d(TAG, "Сброс таймера")
                resetTimer()
            }
            else -> {
                Log.w(TAG, "Неизвестное действие: ${intent?.action}")
            }
        }

        return START_STICKY
    }

    private fun startTimer(milliseconds: Long) {
        if (countDownTimer != null) {
            Log.d(TAG, "Отмена существующего таймера перед запуском нового")
            countDownTimer?.cancel()
        }

        timeLeftInMillis = milliseconds
        totalTimeMillis = milliseconds
        _timeLeft.value = timeLeftInMillis / 1000
        Log.d(TAG, "Начальное время: ${_timeLeft.value} секунд")

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                _timeLeft.value = millisUntilFinished / 1000
                Log.v(TAG, "Тик: осталось ${_timeLeft.value} секунд")
                // Обновляем уведомление с новым временем
                updateNotification()
            }

            override fun onFinish() {
                handleTimerFinish()
            }
        }.start()

        isTimerRunning = true
        _isActive.value = true
        _timerState.value = TimerState.Running
        Log.d(TAG, "Таймер успешно запущен и работает")

        // Создаем уведомление один раз при старте
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun pauseTimer() {
        Log.d(TAG, "Пауза таймера, осталось: ${timeLeftInMillis / 1000} секунд")
        countDownTimer?.cancel()
        isTimerRunning = false
        _timerState.value = TimerState.Paused
        // Обновляем уведомление при паузе
        updateNotification()
    }

    private fun resumeTimer() {
        if (timeLeftInMillis > 0) {
            Log.d(TAG, "Возобновление таймера, осталось: ${timeLeftInMillis / 1000} секунд")
            startTimer(timeLeftInMillis)
        } else {
            Log.w(TAG, "Попытка возобновить таймер с нулевым временем")
        }
    }

    private fun resetTimer() {
        Log.d(TAG, "Сброс таймера")
        countDownTimer?.cancel()
        timeLeftInMillis = 0
        _timeLeft.value = 0
        isTimerRunning = false
        _isActive.value = false
        _timerState.value = TimerState.Idle
        stopAlarmSound()
        vibrator?.cancel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }

        stopSelf()
    }

    private fun handleTimerFinish() {
        Log.d(TAG, "Таймер завершен")
        timeLeftInMillis = 0
        _timeLeft.value = 0
        isTimerRunning = false
        _isActive.value = false
        _timerState.value = TimerState.Finished

        try {
            // Останавливаем foreground service и показываем обычное уведомление о завершении
            stopForeground(true)
            showTimerFinishedNotification()

            // Читаем актуальные настройки звука из SharedPreferences
            val sharedPrefs = getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            val currentSoundEnabled = sharedPrefs.getBoolean("sound_enabled", true)

            Log.d(TAG, "Текущая настройка звука: ${if (currentSoundEnabled) "Включен" else "Выключен"}")

            // Воспроизводим звук или вибрацию в зависимости от актуальных настроек
            if (currentSoundEnabled) {
                playAlarmSound()
            } else {
                vibrateDevice()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при завершении таймера: ${e.message}")
        }
    }

    private fun createFinishedNotification(): android.app.Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val resetAction = PendingIntent.getService(
            this, 3,
            Intent(this, TimerService::class.java).setAction(ACTION_RESET),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Таймер завершен!")
            .setContentText("Время истекло")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Сбросить", resetAction)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOngoing(false)
            .build()
    }

    private fun showTimerFinishedNotification() {
        val notification = createFinishedNotification()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Если таймер завершен, показываем специальное уведомление
        if (_timerState.value is TimerState.Finished) {
            showTimerFinishedNotification()
        } else {
            // Используем notify с тем же ID для обновления существующего уведомления
            notificationManager.notify(NOTIFICATION_ID, createNotification())
        }
    }

    private fun createNotification(): android.app.Notification {
        Log.d(TAG, "Создание/обновление уведомления")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Форматируем время для отображения
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)

        // Создаем действия для уведомления
        val pauseResumeAction = if (isTimerRunning) {
            PendingIntent.getService(
                this, 1,
                Intent(this, TimerService::class.java).setAction(ACTION_PAUSE),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getService(
                this, 2,
                Intent(this, TimerService::class.java).setAction(ACTION_RESUME),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val resetAction = PendingIntent.getService(
            this, 3,
            Intent(this, TimerService::class.java).setAction(ACTION_RESET),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(if (isTimerRunning) "Таймер работает" else "Таймер на паузе")
            .setContentText("Осталось: $timeLeftFormatted")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_media_pause,
                if (isTimerRunning) "Пауза" else "Возобновить",
                pauseResumeAction
            )
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Сброс", resetAction)
            .setOngoing(true)
            .setOnlyAlertOnce(true) // Не показывать уведомление повторно при обновлении
            .setShowWhen(false) // Не показывать время создания уведомления
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotificationChannel() {
        Log.d(TAG, "Создание канала уведомлений")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Таймер"
            val description = "Канал для уведомлений таймера"
            val importance = NotificationManager.IMPORTANCE_LOW // Низкая важность - нет звука/вибрации
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
                setShowBadge(true)
                // Не устанавливаем звук и вибрацию для обычных уведомлений
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Канал уведомлений успешно создан")
        } else {
            Log.d(TAG, "Не требуется создание канала (Android < O)")
        }
    }

    private fun playAlarmSound() {
        try {
            Log.d(TAG, "Попытка воспроизведения звука завершения таймера")
            stopAlarmSound() // Сначала останавливаем предыдущее воспроизведение, если оно есть

            // Повторно инициализируем MediaPlayer
            mediaPlayer = MediaPlayer()

            try {
                // Пробуем создать MediaPlayer из ресурса
                mediaPlayer = MediaPlayer.create(this, R.raw.timer_finish)

                if (mediaPlayer == null) {
                    Log.e(TAG, "MediaPlayer не создан. Звуковой файл может отсутствовать или быть поврежденным")
                    return
                }

                // Настраиваем и воспроизводим звук
                mediaPlayer?.setVolume(1.0f, 1.0f)
                mediaPlayer?.isLooping = false

                mediaPlayer?.setOnCompletionListener {
                    Log.d(TAG, "Воспроизведение звука завершено")
                    stopAlarmSound()
                }

                mediaPlayer?.setOnErrorListener { mp, what, extra ->
                    Log.e(TAG, "Ошибка воспроизведения: код $what, $extra")
                    stopAlarmSound()
                    false
                }

                mediaPlayer?.start()
                Log.d(TAG, "Звук успешно воспроизводится")

            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при настройке или воспроизведении звука: ${e.message}")
                e.printStackTrace()
                stopAlarmSound()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Общая ошибка при воспроизведении звука: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun stopAlarmSound() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer?.isPlaying == true) {
                    Log.d(TAG, "Остановка воспроизведения звука")
                    mediaPlayer?.stop()
                }
                Log.d(TAG, "Освобождение ресурсов MediaPlayer")
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при остановке звука: ${e.message}")
        }
    }

    // Новый метод для вибрации устройства
    private fun vibrateDevice() {
        try {
            Log.d(TAG, "Запуск вибрации (3 раза)")
            if (vibrator?.hasVibrator() == true) {
                // Шаблон вибрации: 3 вибрации по 500мс с паузами 500мс
                val pattern = longArrayOf(0, 500, 500, 500, 500, 500)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Для Android 8.0 и выше используем VibrationEffect
                    val vibrationEffect = VibrationEffect.createWaveform(pattern, -1) // -1 означает не повторять
                    vibrator?.vibrate(vibrationEffect)
                } else {
                    // Для более старых версий Android
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(pattern, -1) // -1 означает не повторять
                }

                Log.d(TAG, "Вибрация активирована на 3 раза")
            } else {
                Log.w(TAG, "Устройство не поддерживает вибрацию")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при запуске вибрации: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy - Сервис уничтожен")
        countDownTimer?.cancel()
        stopAlarmSound()

        // Остановка вибрации при уничтожении сервиса
        vibrator?.cancel()
    }
}

// Состояние таймера для отображения UI
sealed class TimerState {
    object Idle : TimerState()
    object Running : TimerState()
    object Paused : TimerState()
    object Finished : TimerState()

    override fun toString(): String {
        return when (this) {
            is Idle -> "Idle"
            is Running -> "Running"
            is Paused -> "Paused"
            is Finished -> "Finished"
        }
    }
}