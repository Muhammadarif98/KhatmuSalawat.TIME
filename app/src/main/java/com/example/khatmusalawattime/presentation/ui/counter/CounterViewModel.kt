package com.example.khatmusalawattime.presentation.ui.counter

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CounterViewModel"

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val application: Application
) : AndroidViewModel(application) {
    
    // Значение счетчика
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()
    
    // MediaPlayer для звуков
    private var clickPlayer: MediaPlayer? = null
    private var milestone100Player: MediaPlayer? = null
    
    init {
        loadCount()
        prepareMediaPlayers()
    }
    
    // Загрузка счетчика из SharedPreferences
    private fun loadCount() {
        try {
            val sharedPrefs = application.getSharedPreferences("counter_prefs", Context.MODE_PRIVATE)
            val savedCount = sharedPrefs.getInt("count_value", 0)
            _count.value = savedCount
            Log.d(TAG, "Загружено значение счетчика: $savedCount")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке значения счетчика", e)
        }
    }
    
    // Сохранение значения счетчика
    private fun saveCount() {
        try {
            val sharedPrefs = application.getSharedPreferences("counter_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit {
                putInt("count_value", _count.value)
            }
            Log.d(TAG, "Сохранено значение счетчика: ${_count.value}")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при сохранении значения счетчика", e)
        }
    }
    
    // Подготовка MediaPlayer для звуков
    private fun prepareMediaPlayers() {
        try {
            // Звук клика (бип)
            // Имя файла: counter_click.mp3
            // Звук при достижении 100
            // Имя файла: counter_milestone.mp3
            
            // Пока не создаем, так как файлов еще нет, только проверяем наличие
            val clickSoundExists = application.resources.getIdentifier(
                "counter_click", "raw", application.packageName
            ) != 0
            
            val milestoneSoundExists = application.resources.getIdentifier(
                "counter_milestone", "raw", application.packageName
            ) != 0
            
            Log.d(TAG, "Звук клика существует: $clickSoundExists")
            Log.d(TAG, "Звук достижения существует: $milestoneSoundExists")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при подготовке звуков", e)
        }
    }
    
    // Увеличение счетчика на 1
    fun incrementCount() {
        _count.value += 1
        saveCount()
        playClickSound()
        
        // Проверяем, достигнут ли рубеж кратный 100
        if (_count.value % 100 == 0) {
            playMilestoneSound()
        }
    }
    
    // Уменьшение счетчика на 1
    fun decrementCount() {
        if (_count.value > 0) {
            _count.value -= 1
            saveCount()
            playClickSound()
        }
    }
    
    // Сброс счетчика
    fun resetCount() {
        _count.value = 0
        saveCount()
        playClickSound()
    }
    
    // Воспроизведение звука клика
    private fun playClickSound() {
        viewModelScope.launch {
            try {
                // Проверяем наличие звукового файла
                val soundId = application.resources.getIdentifier(
                    "counter_click", "raw", application.packageName
                )
                
                if (soundId != 0) {
                    // Освобождаем предыдущий плеер, если он был
                    clickPlayer?.release()
                    
                    // Создаем и воспроизводим звук
                    clickPlayer = MediaPlayer.create(application, soundId)
                    clickPlayer?.setVolume(1.0f, 1.0f)
                    clickPlayer?.setOnCompletionListener { it.release() }
                    clickPlayer?.start()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при воспроизведении звука клика", e)
            }
        }
    }
    
    // Воспроизведение звука достижения рубежа
    private fun playMilestoneSound() {
        viewModelScope.launch {
            try {
                // Проверяем наличие звукового файла
                val soundId = application.resources.getIdentifier(
                    "counter_milestone", "raw", application.packageName
                )
                
                if (soundId != 0) {
                    // Освобождаем предыдущий плеер, если он был
                    milestone100Player?.release()
                    
                    // Создаем и воспроизводим звук
                    milestone100Player = MediaPlayer.create(application, soundId)
                    milestone100Player?.setVolume(1.0f, 1.0f)
                    milestone100Player?.setOnCompletionListener { it.release() }
                    milestone100Player?.start()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при воспроизведении звука достижения", e)
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        
        // Освобождаем ресурсы MediaPlayer
        try {
            clickPlayer?.release()
            milestone100Player?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при освобождении MediaPlayer", e)
        }
    }
} 