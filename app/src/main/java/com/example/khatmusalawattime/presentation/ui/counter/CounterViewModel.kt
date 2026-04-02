package com.example.khatmusalawattime.presentation.ui.counter

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.khatmusalawattime.domain.model.AzkarPreset
import com.example.khatmusalawattime.domain.model.CounterMode
import com.example.khatmusalawattime.domain.model.CounterState
import com.example.khatmusalawattime.domain.model.WirdPreset
import com.example.khatmusalawattime.domain.model.ZikrItem
import com.example.khatmusalawattime.domain.usecase.counter.CounterUseCases
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CounterViewModel"
private const val PREFS_NAME = "counter_prefs"
private const val KEY_FREE_COUNT = "free_count"
private const val KEY_CUSTOM_ITEMS = "custom_items"

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val application: Application,
    private val counterUseCases: CounterUseCases,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val gson = Gson()
    private val sharedPrefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Состояние счётчика
    private val _counterState = MutableStateFlow(CounterState())
    val counterState: StateFlow<CounterState> = _counterState.asStateFlow()

    // Для совместимости со старым кодом
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    // MediaPlayer для звуков
    private var clickPlayer: MediaPlayer? = null
    private var milestone100Player: MediaPlayer? = null

    // Vibrator
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    init {
        loadFreeCounter()
        prepareMediaPlayers()
    }

    /**
     * Устанавливает режим счётчика
     */
    fun setMode(mode: CounterMode) {
        val items = when (mode) {
            is CounterMode.Free -> emptyList()
            is CounterMode.Azkar -> AzkarPreset.items
            is CounterMode.Wird -> WirdPreset.getItems(mode.countPerZikr)
            is CounterMode.Custom -> mode.items
        }

        _counterState.value = CounterState(
            mode = mode,
            currentZikrIndex = 0,
            currentCount = 0,
            isCompleted = false,
            zikrItems = items
        )

        // Для свободного режима загружаем сохранённое значение
        if (mode is CounterMode.Free) {
            loadFreeCounter()
        }

        // Сохраняем кастомные элементы
        if (mode is CounterMode.Custom) {
            saveCustomItems(mode.items)
        }
    }

    /**
     * Увеличивает счётчик
     */
    fun increment() {
        viewModelScope.launch {
            val state = _counterState.value

            when (state.mode) {
                is CounterMode.Free -> {
                    val newCount = _count.value + 1
                    _count.value = newCount
                    _counterState.update { it.copy(currentCount = newCount) }
                    counterUseCases.updateCounter(newCount)
                    saveFreeCounter(newCount)
                    playClickSound()

                    if (newCount % 100 == 0) {
                        playMilestoneSound()
                    }
                }

                else -> {
                    if (state.isCompleted) return@launch

                    val currentZikr = state.currentZikr ?: return@launch
                    val newCount = state.currentCount + 1

                    playClickSound()

                    if (newCount >= currentZikr.targetCount) {
                        // Завершили текущий зикр
                        val nextIndex = state.currentZikrIndex + 1

                        if (nextIndex >= state.zikrItems.size) {
                            // Завершили все зикры
                            _counterState.update {
                                it.copy(
                                    currentCount = currentZikr.targetCount,
                                    isCompleted = true
                                )
                            }
                            vibrateComplete()
                        } else {
                            // Переходим к следующему зикру
                            vibrateTransition()
                            delay(300) // Небольшая задержка для визуального эффекта
                            _counterState.update {
                                it.copy(
                                    currentZikrIndex = nextIndex,
                                    currentCount = 0
                                )
                            }
                        }
                    } else {
                        // Просто увеличиваем счётчик
                        _counterState.update { it.copy(currentCount = newCount) }
                    }
                }
            }
        }
    }

    /**
     * Уменьшает счётчик
     */
    fun decrement() {
        viewModelScope.launch {
            val state = _counterState.value

            when (state.mode) {
                is CounterMode.Free -> {
                    val newCount = (_count.value - 1).coerceAtLeast(0)
                    _count.value = newCount
                    _counterState.update { it.copy(currentCount = newCount) }
                    counterUseCases.updateCounter(newCount)
                    saveFreeCounter(newCount)
                    playClickSound()
                }

                else -> {
                    if (state.currentCount > 0) {
                        _counterState.update {
                            it.copy(
                                currentCount = it.currentCount - 1,
                                isCompleted = false
                            )
                        }
                        playClickSound()
                    } else if (state.currentZikrIndex > 0) {
                        // Возвращаемся к предыдущему зикру
                        val prevIndex = state.currentZikrIndex - 1
                        val prevZikr = state.zikrItems[prevIndex]
                        _counterState.update {
                            it.copy(
                                currentZikrIndex = prevIndex,
                                currentCount = prevZikr.targetCount - 1,
                                isCompleted = false
                            )
                        }
                        playClickSound()
                    }
                }
            }
        }
    }

    /**
     * Сбрасывает счётчик
     */
    fun reset() {
        viewModelScope.launch {
            val state = _counterState.value

            when (state.mode) {
                is CounterMode.Free -> {
                    _count.value = 0
                    _counterState.update { it.copy(currentCount = 0) }
                    counterUseCases.updateCounter(0)
                    saveFreeCounter(0)
                    playClickSound()
                }

                else -> {
                    _counterState.update {
                        it.copy(
                            currentZikrIndex = 0,
                            currentCount = 0,
                            isCompleted = false
                        )
                    }
                    playClickSound()
                }
            }
        }
    }

    /**
     * Вибрация при переходе к следующему зикру (одиночная)
     */
    private fun vibrateTransition() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(150)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка вибрации", e)
        }
    }

    /**
     * Тройная вибрация при полном завершении
     */
    private fun vibrateComplete() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 200, 100, 200, 100, 200)
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                val pattern = longArrayOf(0, 200, 100, 200, 100, 200)
                vibrator.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка вибрации", e)
        }
    }

    // === Persistence ===

    private fun loadFreeCounter() {
        viewModelScope.launch {
            try {
                val counter = counterUseCases.getCounter()
                _count.value = counter.count
                _counterState.update { it.copy(currentCount = counter.count) }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка загрузки счётчика", e)
            }
        }
    }

    private fun saveFreeCounter(count: Int) {
        try {
            sharedPrefs.edit {
                putInt(KEY_FREE_COUNT, count)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка сохранения счётчика", e)
        }
    }

    fun loadCustomItems(): List<ZikrItem> {
        return try {
            val json = sharedPrefs.getString(KEY_CUSTOM_ITEMS, null) ?: return emptyList()
            val type = object : TypeToken<List<ZikrItem>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки кастомных элементов", e)
            emptyList()
        }
    }

    private fun saveCustomItems(items: List<ZikrItem>) {
        try {
            val json = gson.toJson(items)
            sharedPrefs.edit {
                putString(KEY_CUSTOM_ITEMS, json)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка сохранения кастомных элементов", e)
        }
    }

    // === Sound ===

    private fun prepareMediaPlayers() {
        try {
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

    private fun playClickSound() {
        viewModelScope.launch {
            try {
                val soundId = application.resources.getIdentifier(
                    "counter_click", "raw", application.packageName
                )

                if (soundId != 0) {
                    clickPlayer?.release()
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

    private fun playMilestoneSound() {
        viewModelScope.launch {
            try {
                val soundId = application.resources.getIdentifier(
                    "counter_milestone", "raw", application.packageName
                )

                if (soundId != 0) {
                    milestone100Player?.release()
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
        try {
            clickPlayer?.release()
            milestone100Player?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при освобождении MediaPlayer", e)
        }
    }
}
