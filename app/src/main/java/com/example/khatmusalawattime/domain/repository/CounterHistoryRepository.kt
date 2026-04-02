package com.example.khatmusalawattime.domain.repository

import com.example.khatmusalawattime.domain.model.CounterHistory
import com.example.khatmusalawattime.domain.model.CounterModeType
import com.example.khatmusalawattime.domain.model.CounterStats
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface CounterHistoryRepository {
    fun getAllHistory(): Flow<List<CounterHistory>>
    fun observeStats(): Flow<CounterStats>
    suspend fun getHistoryByDate(date: Date): CounterHistory?
    suspend fun getRecentHistory(days: Int): List<CounterHistory>
    suspend fun recordCount(count: Int, mode: CounterModeType)
    suspend fun getStats(): CounterStats
    suspend fun deleteAll()
}
