package com.example.khatmusalawattime.domain.model

import java.io.Serializable

/**
 * Модель данных для парсинга JSON.
 */
data class ReminderData(
    val datesKhunzakhSalawat: Map<String, String>,
    val datesKhunzakhHatmu: Map<String, String>
): Serializable