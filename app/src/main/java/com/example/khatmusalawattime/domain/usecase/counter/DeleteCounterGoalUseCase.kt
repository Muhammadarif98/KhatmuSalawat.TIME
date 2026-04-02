package com.example.khatmusalawattime.domain.usecase.counter

import com.example.khatmusalawattime.domain.repository.CounterGoalRepository
import javax.inject.Inject

class DeleteCounterGoalUseCase @Inject constructor(
    private val goalRepository: CounterGoalRepository
) {
    /**
     * Удаляет цель счётчика.
     */
    suspend operator fun invoke(goalId: String) {
        goalRepository.deleteGoal(goalId)
    }
}
