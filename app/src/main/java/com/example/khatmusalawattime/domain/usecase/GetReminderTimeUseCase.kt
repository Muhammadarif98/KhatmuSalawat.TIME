package com.example.khatmusalawattime.domain.usecase

import com.example.khatmusalawattime.domain.model.ReminderData
import com.example.khatmusalawattime.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * UseCase для получения данных о времени Салавата и Хатму.
 */
class GetReminderTimeUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {

    /**
     * Получить данные о времени Салавата и Хатму.
     * @return Объект ReminderData с данными из JSON.
     */
    suspend operator fun invoke(): ReminderData {
        return reminderRepository.getReminderData()
    }
}