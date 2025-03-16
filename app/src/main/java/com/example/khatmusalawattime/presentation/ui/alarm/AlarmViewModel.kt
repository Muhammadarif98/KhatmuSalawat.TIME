package com.example.khatmusalawattime.presentation.ui.alarm

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.khatmusalawattime.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

private const val TAG = "AlarmViewModel"

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val application: Application
) : AndroidViewModel(application) {
    
    // Отображаемое время в минутах
    private val _selectedTime = MutableStateFlow(5L)
    val selectedTime: StateFlow<Long> = _selectedTime.asStateFlow()
    
    // Наблюдаем за состоянием таймера из сервиса
    val timerState = TimerService.timerState
    
    // Наблюдаем за оставшимся временем из сервиса
    val timeLeft = TimerService.timeLeft
    
    // Наблюдаем за статусом таймера из сервиса
    val isActive = TimerService.isActive
    
    // Состояние для форматированного отображения времени
    private val _formattedTime = MutableStateFlow("05:00")
    val formattedTime: StateFlow<String> = _formattedTime.asStateFlow()
    
    // Новые StateFlow для хранения URI пользовательского изображения
    private val _userImageUri = MutableStateFlow<Uri?>(null)
    val userImageUri: StateFlow<Uri?> = _userImageUri.asStateFlow()
    
    // Состояние для режима звука (true = звук, false = вибрация)
    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()
    
    init {
        Log.d(TAG, "Инициализация AlarmViewModel")
        // Инициализируем форматированное время при создании ViewModel
        updateFormattedTime(_selectedTime.value * 60)
        
        // Загружаем сохраненное изображение пользователя при инициализации
        loadUserImage()
        
        // Загружаем настройку звука
        loadSoundSettings()
        
        // Настраиваем наблюдение за изменениями времени
        viewModelScope.launch {
            Log.d(TAG, "Настройка наблюдения за оставшимся временем")
            timeLeft.collectLatest { secondsLeft ->
                Log.d(TAG, "Time left update: $secondsLeft seconds")
                if (TimerService.isActive.value) {
                    updateFormattedTime(secondsLeft)
                }
            }
        }
        
        // Наблюдаем за состоянием таймера для дополнительной синхронизации
        viewModelScope.launch {
            Log.d(TAG, "Настройка наблюдения за состоянием таймера")
            timerState.collectLatest { state ->
                Log.d(TAG, "Timer state update: $state")
                if (state is TimerState.Idle) {
                    // Если таймер в режиме ожидания, обновляем отображаемое время на основе выбранного времени
                    updateFormattedTime(_selectedTime.value * 60)
                }
            }
        }
    }
    
    // Выбор времени таймера
    fun selectTime(minutes: Long) {
        Log.d(TAG, "Выбрано время: $minutes минут")
        _selectedTime.value = minutes
        
        // Обновляем отображаемое время только если таймер не активен
        if (timerState.value !is TimerState.Running && timerState.value !is TimerState.Paused) {
            updateFormattedTime(minutes * 60)
        }
    }
    
    // Запуск таймера
    fun startTimer() {
        Log.d(TAG, "Запуск таймера с ${_selectedTime.value} минутами")
        val serviceIntent = Intent(application, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_TIME, _selectedTime.value)
            putExtra(TimerService.EXTRA_SOUND_ENABLED, _soundEnabled.value)
        }
        try {
            application.startService(serviceIntent)
            Log.d(TAG, "Сервис таймера успешно запущен")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при запуске сервиса таймера", e)
        }
    }
    
    // Пауза таймера
    fun pauseTimer() {
        Log.d(TAG, "Пауза таймера")
        val serviceIntent = Intent(application, TimerService::class.java).apply {
            action = TimerService.ACTION_PAUSE
        }
        try {
            application.startService(serviceIntent)
            Log.d(TAG, "Команда паузы отправлена в сервис")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при отправке команды паузы", e)
        }
    }
    
    // Возобновление таймера
    fun resumeTimer() {
        Log.d(TAG, "Возобновление таймера")
        val serviceIntent = Intent(application, TimerService::class.java).apply {
            action = TimerService.ACTION_RESUME
        }
        try {
            application.startService(serviceIntent)
            Log.d(TAG, "Команда возобновления отправлена в сервис")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при отправке команды возобновления", e)
        }
    }
    
    // Сброс таймера
    fun resetTimer() {
        Log.d(TAG, "Сброс таймера")
        val serviceIntent = Intent(application, TimerService::class.java).apply {
            action = TimerService.ACTION_RESET
        }
        try {
            application.startService(serviceIntent)
            Log.d(TAG, "Команда сброса отправлена в сервис")
            
            // Обновляем отображаемое время на основе выбранного времени
            updateFormattedTime(_selectedTime.value * 60)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при отправке команды сброса", e)
        }
    }
    
    // Обработка нажатия на кнопку play/pause
    fun toggleTimerState() {
        Log.d(TAG, "Переключение состояния таймера: ${timerState.value}")
        when (timerState.value) {
            is TimerState.Running -> pauseTimer()
            is TimerState.Paused -> resumeTimer()
            is TimerState.Idle, is TimerState.Finished -> startTimer()
        }
    }
    
    // Обновление форматированного времени для отображения
    @SuppressLint("DefaultLocale")
    private fun updateFormattedTime(seconds: Long) {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val newFormattedTime = String.format("%02d:%02d", minutes, remainingSeconds)
        Log.d(TAG, "Обновление отображаемого времени: $newFormattedTime")
        _formattedTime.value = newFormattedTime
    }
    
    // Проверка воспроизведения звука
    fun testSound() {
        Log.d(TAG, "Проверка звука таймера")
        var mediaPlayer: MediaPlayer? = null
        
        try {
            // Пробуем создать MediaPlayer и воспроизвести звук
            mediaPlayer = MediaPlayer.create(application, R.raw.timer_finish)
            
            if (mediaPlayer == null) {
                Log.e(TAG, "Не удалось создать MediaPlayer для тестового звука. Файл может отсутствовать или быть поврежденным")
                return
            }
            
            // Настройки воспроизведения
            mediaPlayer.setVolume(1.0f, 1.0f)
            
            // Добавляем обработчик завершения
            mediaPlayer.setOnCompletionListener {
                Log.d(TAG, "Тестовый звук завершен")
                it.release()
            }
            
            // Добавляем обработчик ошибок
            mediaPlayer.setOnErrorListener { mp, what, extra ->
                Log.e(TAG, "Ошибка при воспроизведении тестового звука: код $what, $extra")
                mp.release()
                false
            }
            
            // Начинаем воспроизведение
            mediaPlayer.start()
            Log.d(TAG, "Тестовый звук запущен")
            
            // Остановка звука через 3 секунды
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                    }
                    mediaPlayer.release()
                    Log.d(TAG, "Тестовый звук остановлен")
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при остановке тестового звука: ${e.message}")
                }
            }, 3000)
            
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при воспроизведении тестового звука: ${e.message}")
            e.printStackTrace()
            
            // Освобождаем ресурсы в случае ошибки
            mediaPlayer?.release()
        }
    }
    
    // Сохранение выбранного изображения
    fun saveUserImage(uri: Uri?) {
        try {
            if (uri == null) {
                // Если uri null, просто очищаем сохраненное изображение
                _userImageUri.value = null
                
                // Удаляем запись в SharedPreferences
                val sharedPrefs = application.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
                sharedPrefs.edit {
                    remove("user_image_path")
                    Log.d(TAG, "Удалено пользовательское изображение")
                }
                
                // Удаляем сохраненный файл, если он есть
                val savedImageDir = File(application.filesDir, "user_images")
                val savedImageFile = File(savedImageDir, "timer_background.jpg")
                if (savedImageFile.exists()) {
                    savedImageFile.delete()
                }
                
                return
            }
            
            // Создаем директорию для хранения изображений, если ее нет
            val imageDir = File(application.filesDir, "user_images")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }
            
            // Путь для сохраненного изображения
            val imageFile = File(imageDir, "timer_background.jpg")
            
            // Загружаем и масштабируем изображение для экономии места
            application.contentResolver.openInputStream(uri)?.use { input ->
                // Декодируем размеры изображения без загрузки в память
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(input, null, options)
                input.close()
                
                // Повторно открываем поток
                application.contentResolver.openInputStream(uri)?.use { newInput ->
                    // Рассчитываем масштаб для уменьшения (максимальная сторона 1080px)
                    val maxDimension = 1080
                    val scale = Math.max(1, Math.min(
                        options.outWidth / maxDimension,
                        options.outHeight / maxDimension
                    ))
                    
                    // Настраиваем опции для масштабирования
                    val scaleOptions = BitmapFactory.Options().apply {
                        inSampleSize = scale
                    }
                    
                    // Загружаем изображение с масштабированием
                    val bitmap = BitmapFactory.decodeStream(newInput, null, scaleOptions)
                    
                    // Сохраняем сжатое изображение
                    FileOutputStream(imageFile).use { output ->
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 85, output)
                        bitmap?.recycle()
                    }
                }
            }
            
            // Создаем URI из файла для отображения
            val savedUri = Uri.fromFile(imageFile)
            _userImageUri.value = savedUri
            
            // Сохраняем путь к файлу в SharedPreferences
            val sharedPrefs = application.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit {
                putString("user_image_path", imageFile.absolutePath)
                Log.d(TAG, "Сохранено пользовательское изображение: ${imageFile.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при сохранении изображения", e)
        }
    }
    
    // Загрузка сохраненного URI изображения из SharedPreferences
    private fun loadUserImage() {
        try {
            val sharedPrefs = application.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            val savedImagePath = sharedPrefs.getString("user_image_path", null)
            
            savedImagePath?.let { path ->
                try {
                    val imageFile = File(path)
                    if (imageFile.exists()) {
                        val fileUri = Uri.fromFile(imageFile)
                        _userImageUri.value = fileUri
                        Log.d(TAG, "Загружено пользовательское изображение: $path")
                    } else {
                        Log.w(TAG, "Файл изображения не найден: $path")
                        // Если файл не существует, очищаем настройку
                        sharedPrefs.edit {
                            remove("user_image_path")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при загрузке файла изображения", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке изображения", e)
        }
    }
    
    // Сброс изображения к изображению по умолчанию
    fun resetUserImage() {
        saveUserImage(null)
    }
    
    // Загрузка настроек звука из SharedPreferences
    private fun loadSoundSettings() {
        try {
            val sharedPrefs = application.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            val soundEnabled = sharedPrefs.getBoolean("sound_enabled", true)
            _soundEnabled.value = soundEnabled
            Log.d(TAG, "Загружена настройка звука: $soundEnabled")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке настроек звука", e)
        }
    }
    
    // Переключение режима звука/вибрации
    fun toggleSoundMode() {
        val newValue = !_soundEnabled.value
        _soundEnabled.value = newValue
        
        // Сохраняем настройку в SharedPreferences
        val sharedPrefs = application.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit {
            putBoolean("sound_enabled", newValue)
        }
        
        Log.d(TAG, "Режим звука изменен на: ${if (newValue) "Звук" else "Вибрация"}")
    }
    
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared - ViewModel уничтожена")
        // При уничтожении ViewModel не сбрасываем таймер автоматически,
        // так как он должен продолжать работать в фоне
    }
} 