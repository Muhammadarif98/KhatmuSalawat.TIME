package com.example.khatmusalawattime.domain.usecase.counter

import com.example.khatmusalawattime.domain.model.CounterModeType
import com.example.khatmusalawattime.domain.repository.CounterHistoryRepository
import javax.inject.Inject

class RecordCounterSessionUseCase @Inject constructor(
    private val historyRepository: CounterHistoryRepository
) {
    suspend operator fun invoke(count: Int, mode: CounterModeType) {
        if (count > 0) {
            historyRepository.recordCount(count, mode)
        }
    }
}
