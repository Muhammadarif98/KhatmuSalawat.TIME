package com.example.khatmusalawattime.domain.usecase.counter

import com.example.khatmusalawattime.domain.repository.CounterGoalRepository
import javax.inject.Inject

class ResetGoalProgressUseCase @Inject constructor(
    private val goalRepository: CounterGoalRepository
) {
    suspend operator fun invoke(goalId: String) {
        goalRepository.resetProgress(goalId)
    }
}
