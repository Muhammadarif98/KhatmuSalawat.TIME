package com.example.khatmusalawattime.domain.usecase.counter

import com.example.khatmusalawattime.domain.model.CounterStats
import com.example.khatmusalawattime.domain.repository.CounterHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCounterStatsUseCase @Inject constructor(
    private val historyRepository: CounterHistoryRepository
) {
    suspend operator fun invoke(): CounterStats {
        return historyRepository.getStats()
    }

    fun observeStats(): Flow<CounterStats> {
        return historyRepository.observeStats()
    }
}
