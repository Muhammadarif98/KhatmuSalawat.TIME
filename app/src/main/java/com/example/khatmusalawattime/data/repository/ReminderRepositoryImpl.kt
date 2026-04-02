package com.example.khatmusalawattime.data.repository

import android.content.Context
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.domain.model.Location
import com.example.khatmusalawattime.domain.model.ReminderData
import com.example.khatmusalawattime.domain.repository.ReminderRepository
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
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

    /**
     * Модель для парсинга JSON Чиркей.
     */
    private data class ChirkeiReminderData(
        @SerializedName("datesChirkeySalawat")
        val salawat: Map<String, String>,
        @SerializedName("datesChirkeyHatmu")
        val hatmu: Map<String, String>
    )

    override suspend fun getReminderData(location: Location): ReminderData = withContext(Dispatchers.IO) {
        when (location) {
            Location.KHUNZAKH -> loadKhunzakhData()
            Location.CHIRKEI -> loadChirkeiData()
        }
    }

    private fun loadKhunzakhData(): ReminderData {
        val jsonString = context.resources.openRawResource(R.raw.reminder_data)
            .bufferedReader()
            .use { it.readText() }
        return gson.fromJson(jsonString, ReminderData::class.java)
    }

    private fun loadChirkeiData(): ReminderData {
        val jsonString = context.resources.openRawResource(R.raw.reminder_data_chirkei)
            .bufferedReader()
            .use { it.readText() }
        val chirkeiData = gson.fromJson(jsonString, ChirkeiReminderData::class.java)
        // Конвертируем в ReminderData для совместимости с существующим кодом
        return ReminderData(
            datesKhunzakhSalawat = chirkeiData.salawat,
            datesKhunzakhHatmu = chirkeiData.hatmu
        )
    }
}
