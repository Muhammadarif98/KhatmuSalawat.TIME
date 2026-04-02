package com.example.khatmusalawattime.domain.usecase.counter

import com.example.khatmusalawattime.domain.model.CounterGoal
import com.example.khatmusalawattime.domain.repository.CounterGoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveGoalsUseCase @Inject constructor(
    private val goalRepository: CounterGoalRepository
) {
    /**
     * Возвращает все цели (включая завершённые) для отображения в карусели
     */
    operator fun invoke(): Flow<List<CounterGoal>> {
        return goalRepository.getAllGoals()
    }
}
