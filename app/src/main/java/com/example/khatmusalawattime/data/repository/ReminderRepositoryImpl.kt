package com.example.khatmusalawattime.data.repository

import android.content.Context
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.domain.model.ReminderData
import com.example.khatmusalawattime.domain.repository.ReminderRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Реализация репозитория для работы с данными о времени Салавата и Хатму.
 */
class ReminderRepositoryImpl @Inject constructor(
    private val context: Context,
    private val gson: Gson
) : ReminderRepository {

    override suspend fun getReminderData(): ReminderData = withContext(Dispatchers.IO) {
        // Чтение JSON-файла из папки res/raw
        val jsonString = context.resources.openRawResource(R.raw.reminder_data)
            .bufferedReader()
            .use { it.readText() }

        // Парсинг JSON в объект ReminderData
        gson.fromJson(jsonString, ReminderData::class.java)
    }
}