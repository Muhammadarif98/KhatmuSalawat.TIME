package com.example.khatmusalawattime.domain.repository

import com.example.khatmusalawattime.domain.model.Location
import com.example.khatmusalawattime.domain.model.ReminderData


/**
 * Интерфейс репозитория для работы с данными о времени Салавата и Хатму.
 */
interface ReminderRepository {

    /**
     * Получить данные о времени Салавата и Хатму для указанной локации.
     * @param location Локация (Хунзах или Чиркей)
     * @return Объект ReminderData с данными из JSON.
     */
    suspend fun getReminderData(location: Location = Location.KHUNZAKH): ReminderData
}
