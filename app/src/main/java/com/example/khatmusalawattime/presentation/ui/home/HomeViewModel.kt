package com.example.khatmusalawattime.presentation.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khatmusalawattime.domain.model.Location
import com.example.khatmusalawattime.domain.model.ReminderData
import com.example.khatmusalawattime.domain.usecase.GetReminderTimeUseCase
import com.example.khatmusalawattime.presentation.ui.components.settings.SettingsPreferences
import com.example.khatmusalawattime.presentation.widget.TimeWidgetProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для главного экрана.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getReminderTimeUseCase: GetReminderTimeUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Состояние загрузки данных
    private val _reminderData = MutableStateFlow<ReminderData?>(null)
    val reminderData: StateFlow<ReminderData?> = _reminderData

    // Текущая локация
    private val _currentLocation = MutableStateFlow(Location.KHUNZAKH)
    val currentLocation: StateFlow<Location> = _currentLocation

    // Состояние ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        // Загружаем сохраненную локацию при создании ViewModel
        _currentLocation.value = SettingsPreferences.loadLocation(context)
    }

    /**
     * Загрузить данные о времени Салавата и Хатму.
     */
    fun loadReminderData() {
        viewModelScope.launch {
            try {
                val data = getReminderTimeUseCase(_currentLocation.value)
                _reminderData.value = data
                Log.d("HomeViewModel", "Данные загружены для ${_currentLocation.value}: $data")
            } catch (e: Exception) {
                _error.value = e.message ?: "Произошла ошибка при загрузке данных"
                Log.e("HomeViewModel", "Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Изменить локацию и перезагрузить данные.
     */
    fun setLocation(location: Location) {
        if (_currentLocation.value != location) {
            _currentLocation.value = location
            SettingsPreferences.saveLocation(context, location)
            loadReminderData()
            // Обновляем виджеты
            TimeWidgetProvider.updateAllWidgets(context)
        }
    }
}
