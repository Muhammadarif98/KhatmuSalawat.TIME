package com.example.khatmusalawattime.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khatmusalawattime.domain.model.ReminderData
import com.example.khatmusalawattime.domain.usecase.GetReminderTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для главного экрана.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getReminderTimeUseCase: GetReminderTimeUseCase
) : ViewModel() {

    // Состояние загрузки данных
    private val _reminderData = MutableStateFlow<ReminderData?>(null)
    val reminderData: StateFlow<ReminderData?> = _reminderData

    // Состояние ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Загрузить данные о времени Салавата и Хатму.
     */
    fun loadReminderData() {
        viewModelScope.launch {
            try {
                // Получаем данные из UseCase
                val data = getReminderTimeUseCase()
                _reminderData.value = data
            } catch (e: Exception) {
                // Обрабатываем ошибку
                _error.value = e.message ?: "Произошла ошибка при загрузке данных"
            }
        }
    }
}