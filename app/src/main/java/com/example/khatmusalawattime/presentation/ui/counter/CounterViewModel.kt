package com.example.khatmusalawattime.presentation.ui.counter

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.khatmusalawattime.domain.model.AzkarPreset
import com.example.khatmusalawattime.domain.model.CounterGoal
import com.example.khatmusalawattime.domain.model.CounterMode
import com.example.khatmusalawattime.domain.model.CounterModeType
import com.example.khatmusalawattime.domain.model.CounterState
import com.example.khatmusalawattime.domain.model.CounterStats
import com.example.khatmusalawattime.domain.model.GoalType
import com.example.khatmusalawattime.domain.model.WirdPreset
import com.example.khatmusalawattime.domain.model.ZikrItem
import com.example.khatmusalawattime.domain.usecase.counter.CounterUseCases
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CounterViewModel"
private const val PREFS_NAME = "counter_prefs"
private const val KEY_FREE_COUNT = "free_count"
private const val KEY_CUSTOM_ITEMS = "custom_items"

// Ключи для сохранения прогресса режимов
private const val KEY_AZKAR_INDEX = "azkar_current_index"
private const val KEY_AZKAR_COUNT = "azkar_current_count"
private const val KEY_WIRD_COUNT_PER_ZIKR = "wird_count_per_zikr"
private const val KEY_WIRD_INDEX = "wird_current_index"
private const val KEY_WIRD_COUNT = "wird_current_count"
private const val KEY_CUSTOM_INDEX = "custom_current_index"
private const val KEY_CUSTOM_COUNT = "custom_current_count"

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

    // Статистика счётчика (реактивная - обновляется автоматически)
    val stats: StateFlow<CounterStats> = counterUseCases.getStats.observeStats()
        .stateIn(viewModelScope, SharingStarted.Lazily, CounterStats())

    // Активные цели
    val activeGoals: StateFlow<List<CounterGoal>> = counterUseCases.getActiveGoals()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Индекс выбранной цели в списке
    private val _selectedGoalIndex = MutableStateFlow(0)
    val selectedGoalIndex: StateFlow<Int> = _selectedGoalIndex.asStateFlow()

    // Текущая выбранная цель (по индексу)
    private val _activeGoal = MutableStateFlow<CounterGoal?>(null)
    val activeGoal: StateFlow<CounterGoal?> = _activeGoal.asStateFlow()

    // Показывать ли диалог создания цели
    private val _showGoalDialog = MutableStateFlow(false)
    val showGoalDialog: StateFlow<Boolean> = _showGoalDialog.asStateFlow()

    // Показывать ли секцию статистики (свёрнута/развёрнута)
    private val _statsExpanded = MutableStateFlow(false)
    val statsExpanded: StateFlow<Boolean> = _statsExpanded.asStateFlow()

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
        loadStats()
        observeActiveGoals()
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

        // Загружаем сохранённое состояние для режима
        val (savedIndex, savedCount) = when (mode) {
            is CounterMode.Azkar -> loadAzkarState()
            is CounterMode.Wird -> {
                val (_, index, count) = loadWirdState()
                Pair(index, count)
            }
            is CounterMode.Custom -> loadCustomState()
            is CounterMode.Free -> Pair(0, 0)
        }

        // Проверяем, что индекс не выходит за пределы списка
        val validIndex = if (items.isNotEmpty()) savedIndex.coerceIn(0, items.size - 1) else 0
        // Проверяем, что счёт не превышает максимум текущего зикра
        val maxCount = items.getOrNull(validIndex)?.targetCount ?: Int.MAX_VALUE
        val validCount = savedCount.coerceIn(0, maxCount - 1)

        _counterState.value = CounterState(
            mode = mode,
            currentZikrIndex = validIndex,
            currentCount = validCount,
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

                    // Обновляем прогресс активной цели
                    updateActiveGoalProgress()

                    // Записываем в историю
                    counterUseCases.recordSession(1, CounterModeType.FREE)

                    if (newCount % 100 == 0) {
                        playMilestoneSound()
                        loadStats() // Обновляем статистику после каждых 100
                    }
                }

                else -> {
                    if (state.isCompleted) return@launch

                    val currentZikr = state.currentZikr ?: return@launch
                    val newCount = state.currentCount + 1

                    playClickSound()

                    // Записываем в историю (цель НЕ обновляем — только свободный счётчик влияет на цель)
                    counterUseCases.recordSession(1, state.mode.type)

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
                            saveCurrentModeState()
                            vibrateComplete()
                            loadStats() // Обновляем статистику
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
                            saveCurrentModeState()
                        }
                    } else {
                        // Просто увеличиваем счётчик
                        _counterState.update { it.copy(currentCount = newCount) }
                        saveCurrentModeState()
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
                        saveCurrentModeState()
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
                        saveCurrentModeState()
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
                    saveCurrentModeState()
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

    // === Mode State Persistence ===

    /**
     * Сохраняет состояние режима Azkar
     */
    private fun saveAzkarState(index: Int, count: Int) {
        sharedPrefs.edit {
            putInt(KEY_AZKAR_INDEX, index)
            putInt(KEY_AZKAR_COUNT, count)
        }
    }

    /**
     * Загружает состояние режима Azkar
     */
    private fun loadAzkarState(): Pair<Int, Int> {
        val index = sharedPrefs.getInt(KEY_AZKAR_INDEX, 0)
        val count = sharedPrefs.getInt(KEY_AZKAR_COUNT, 0)
        return Pair(index, count)
    }

    /**
     * Сохраняет состояние режима Wird
     */
    private fun saveWirdState(countPerZikr: Int, index: Int, count: Int) {
        sharedPrefs.edit {
            putInt(KEY_WIRD_COUNT_PER_ZIKR, countPerZikr)
            putInt(KEY_WIRD_INDEX, index)
            putInt(KEY_WIRD_COUNT, count)
        }
    }

    /**
     * Загружает состояние режима Wird
     * @return Triple(countPerZikr, index, count)
     */
    private fun loadWirdState(): Triple<Int, Int, Int> {
        val countPerZikr = sharedPrefs.getInt(KEY_WIRD_COUNT_PER_ZIKR, 100)
        val index = sharedPrefs.getInt(KEY_WIRD_INDEX, 0)
        val count = sharedPrefs.getInt(KEY_WIRD_COUNT, 0)
        return Triple(countPerZikr, index, count)
    }

    /**
     * Сохраняет состояние режима Custom
     */
    private fun saveCustomState(index: Int, count: Int) {
        sharedPrefs.edit {
            putInt(KEY_CUSTOM_INDEX, index)
            putInt(KEY_CUSTOM_COUNT, count)
        }
    }

    /**
     * Загружает состояние режима Custom
     */
    private fun loadCustomState(): Pair<Int, Int> {
        val index = sharedPrefs.getInt(KEY_CUSTOM_INDEX, 0)
        val count = sharedPrefs.getInt(KEY_CUSTOM_COUNT, 0)
        return Pair(index, count)
    }

    /**
     * Сохраняет текущее состояние в зависимости от режима
     */
    private fun saveCurrentModeState() {
        val state = _counterState.value
        when (state.mode) {
            is CounterMode.Azkar -> {
                saveAzkarState(state.currentZikrIndex, state.currentCount)
            }
            is CounterMode.Wird -> {
                saveWirdState(
                    (state.mode as CounterMode.Wird).countPerZikr,
                    state.currentZikrIndex,
                    state.currentCount
                )
            }
            is CounterMode.Custom -> {
                saveCustomState(state.currentZikrIndex, state.currentCount)
            }
            is CounterMode.Free -> {
                // Свободный режим сохраняется отдельно
            }
        }
    }

    // === Statistics & Goals ===

    private fun loadStats() {
        // Статистика обновляется реактивно через Flow - этот метод оставлен для совместимости
    }

    private fun observeActiveGoals() {
        viewModelScope.launch {
            // Комбинируем список целей и выбранный индекс
            kotlinx.coroutines.flow.combine(activeGoals, _selectedGoalIndex) { goals, index ->
                goals to index
            }.collect { (goals, index) ->
                // Корректируем индекс если он вышел за границы
                val validIndex = if (goals.isEmpty()) 0 else index.coerceIn(0, goals.size - 1)
                if (validIndex != index) {
                    _selectedGoalIndex.value = validIndex
                }
                _activeGoal.value = goals.getOrNull(validIndex)
            }
        }
    }

    /**
     * Выбирает цель по индексу (для свайпа)
     */
    fun selectGoal(index: Int) {
        val goals = activeGoals.value
        if (goals.isNotEmpty()) {
            _selectedGoalIndex.value = index.coerceIn(0, goals.size - 1)
        }
    }

    /**
     * Переключает на следующую незавершённую цель после завершения текущей
     */
    private fun switchToNextIncompleteGoal() {
        val goals = activeGoals.value
        val currentIndex = _selectedGoalIndex.value

        // Ищем следующую незавершённую цель начиная с текущей позиции
        val nextIndex = goals.indices
            .drop(currentIndex + 1)
            .firstOrNull { !goals[it].isCompleted }
            ?: goals.indices.firstOrNull { !goals[it].isCompleted }

        if (nextIndex != null && nextIndex != currentIndex) {
            _selectedGoalIndex.value = nextIndex
        }
    }

    private fun updateActiveGoalProgress() {
        viewModelScope.launch {
            // Берём цель напрямую по индексу, а не через _activeGoal (избегаем race condition)
            val goals = activeGoals.value
            val index = _selectedGoalIndex.value
            val goal = goals.getOrNull(index) ?: return@launch

            // Не обновляем прогресс завершённой цели
            if (goal.isCompleted) return@launch

            try {
                val completed = counterUseCases.updateGoalProgress(goal.id, 1)
                if (completed) {
                    vibrateComplete()
                    loadStats()
                    // Переключаемся на следующую незавершённую цель
                    delay(500) // Небольшая задержка чтобы пользователь увидел завершение
                    switchToNextIncompleteGoal()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка обновления прогресса цели", e)
            }
        }
    }

    /**
     * Создаёт новую цель счётчика
     */
    fun createGoal(
        title: String,
        targetCount: Int,
        goalType: GoalType
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Создание цели: title=$title, targetCount=$targetCount, goalType=$goalType")

                val goal = counterUseCases.createGoal(
                    title = title,
                    targetCount = targetCount,
                    goalType = goalType
                )

                Log.d(TAG, "Цель создана: id=${goal.id}")

                _showGoalDialog.value = false
                Toast.makeText(application, "Цель создана", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Log.e(TAG, "Ошибка создания цели", e)
                Toast.makeText(application, "Ошибка создания цели", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Удаляет цель счётчика
     */
    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            try {
                counterUseCases.deleteGoal(goalId)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка удаления цели", e)
            }
        }
    }

    /**
     * Сбрасывает прогресс цели (обнуляет счётчик, не удаляя цель)
     */
    fun resetGoalProgress(goalId: String) {
        viewModelScope.launch {
            try {
                counterUseCases.resetGoalProgress(goalId)
                Toast.makeText(application, "Прогресс сброшен", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка сброса прогресса цели", e)
            }
        }
    }

    /**
     * Показать/скрыть диалог создания цели
     */
    fun showGoalDialog(show: Boolean) {
        _showGoalDialog.value = show
    }

    /**
     * Развернуть/свернуть секцию статистики
     */
    fun toggleStatsExpanded() {
        _statsExpanded.value = !_statsExpanded.value
        if (_statsExpanded.value) {
            loadStats() // Обновляем статистику при открытии
        }
    }

    /**
     * Принудительное обновление статистики
     */
    fun refreshStats() {
        loadStats()
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
