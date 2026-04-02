package com.example.khatmusalawattime.data.repository

import com.example.khatmusalawattime.data.local.dao.CounterHistoryDao
import com.example.khatmusalawattime.data.local.entity.CounterHistoryEntity
import com.example.khatmusalawattime.domain.model.CounterHistory
import com.example.khatmusalawattime.domain.model.CounterModeType
import com.example.khatmusalawattime.domain.model.CounterStats
import com.example.khatmusalawattime.domain.repository.CounterHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class CounterHistoryRepositoryImpl @Inject constructor(
    private val historyDao: CounterHistoryDao,
    private val gson: Gson
) : CounterHistoryRepository {

    override fun getAllHistory(): Flow<List<CounterHistory>> {
        return historyDao.getAllHistory().map { entities ->
            entities.map { it.toCounterHistory(parseModes(it.modes)) }
        }
    }

    override fun observeStats(): Flow<CounterStats> {
        return historyDao.getAllHistory().map { _ ->
            // Пересчитываем статистику при любом изменении истории
            getStats()
        }
    }

    override suspend fun getHistoryByDate(date: Date): CounterHistory? {
        val dayStart = getDayStart(date)
        return historyDao.getHistoryByDate(dayStart)?.let {
            it.toCounterHistory(parseModes(it.modes))
        }
    }

    override suspend fun getRecentHistory(days: Int): List<CounterHistory> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val fromDate = getDayStart(calendar.time)
        return historyDao.getHistoryFromDate(fromDate).map {
            it.toCounterHistory(parseModes(it.modes))
        }
    }

    override suspend fun recordCount(count: Int, mode: CounterModeType) {
        val today = getDayStart(Date())
        val existing = historyDao.getHistoryByDate(today)

        if (existing != null) {
            val existingModes = parseModes(existing.modes).toMutableList()
            if (!existingModes.contains(mode)) {
                existingModes.add(mode)
            }
            val updated = existing.copy(
                totalCount = existing.totalCount + count,
                sessionsCount = existing.sessionsCount + 1,
                modes = gson.toJson(existingModes.map { it.name })
            )
            historyDao.update(updated)
        } else {
            val newHistory = CounterHistoryEntity(
                id = UUID.randomUUID().toString(),
                date = today,
                totalCount = count,
                sessionsCount = 1,
                modes = gson.toJson(listOf(mode.name)),
                createdAt = System.currentTimeMillis()
            )
            historyDao.insert(newHistory)
        }
    }

    override suspend fun getStats(): CounterStats {
        val totalAllTime = historyDao.getTotalAllTime() ?: 0
        val activeDays = historyDao.getActiveDaysCount()
        val averagePerDay = if (activeDays > 0) totalAllTime.toFloat() / activeDays else 0f
        val bestDayEntity = historyDao.getBestDay()
        val bestDay = bestDayEntity?.toCounterHistory(parseModes(bestDayEntity.modes))
        val last7Days = getRecentHistory(7)
        val currentStreak = calculateStreak(last7Days)
        val bestStreak = calculateBestStreak()

        return CounterStats(
            totalAllTime = totalAllTime,
            averagePerDay = averagePerDay,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            bestDay = bestDay,
            last7Days = last7Days,
            totalActiveDays = activeDays
        )
    }

    override suspend fun deleteAll() {
        historyDao.deleteAll()
    }

    private fun getDayStart(date: Date): Long {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun parseModes(modesJson: String): List<CounterModeType> {
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            val modeNames: List<String> = gson.fromJson(modesJson, type)
            modeNames.mapNotNull { name ->
                try {
                    CounterModeType.valueOf(name)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun calculateStreak(recentHistory: List<CounterHistory>): Int {
        if (recentHistory.isEmpty()) return 0

        val calendar = Calendar.getInstance()
        var streak = 0
        var currentDate = getDayStart(Date())

        // Проверяем есть ли запись за сегодня
        val todayHistory = recentHistory.find { getDayStart(it.date) == currentDate }
        if (todayHistory == null) {
            // Проверяем вчера
            calendar.timeInMillis = currentDate
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            currentDate = calendar.timeInMillis
        }

        val sortedHistory = recentHistory.sortedByDescending { it.date }
        for (history in sortedHistory) {
            val historyDate = getDayStart(history.date)
            if (historyDate == currentDate) {
                streak++
                calendar.timeInMillis = currentDate
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                currentDate = calendar.timeInMillis
            } else if (historyDate < currentDate) {
                break
            }
        }

        return streak
    }

    private suspend fun calculateBestStreak(): Int {
        val allHistory = historyDao.getRecentHistory(365)
        if (allHistory.isEmpty()) return 0

        val sortedHistory = allHistory.sortedBy { it.date }
        var bestStreak = 1
        var currentStreak = 1
        var previousDate = getDayStart(Date(sortedHistory.first().date))

        for (i in 1 until sortedHistory.size) {
            val currentDate = getDayStart(Date(sortedHistory[i].date))
            val expectedDate = previousDate + 24 * 60 * 60 * 1000 // +1 день

            if (currentDate == expectedDate) {
                currentStreak++
                bestStreak = maxOf(bestStreak, currentStreak)
            } else {
                currentStreak = 1
            }
            previousDate = currentDate
        }

        return bestStreak
    }
}
