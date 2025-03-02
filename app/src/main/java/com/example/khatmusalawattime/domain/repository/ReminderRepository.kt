package com.example.khatmusalawattime.domain.repository

import com.example.khatmusalawattime.domain.model.ReminderData


/**
 * Интерфейс репозитория для работы с данными о времени Салавата и Хатму.
 */
interface ReminderRepository {

    /**
     * Получить данные о времени Салавата и Хатму.
     * @return Объект ReminderData с данными из JSON.
     */
    suspend fun getReminderData(): ReminderData
}