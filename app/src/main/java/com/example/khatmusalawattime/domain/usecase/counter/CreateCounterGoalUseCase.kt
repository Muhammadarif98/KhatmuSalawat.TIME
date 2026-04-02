package com.example.khatmusalawattime.domain.usecase.counter

import com.example.khatmusalawattime.domain.model.CounterGoal
import com.example.khatmusalawattime.domain.model.GoalType
import com.example.khatmusalawattime.domain.repository.CounterGoalRepository
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class CreateCounterGoalUseCase @Inject constructor(
    private val goalRepository: CounterGoalRepository
) {
    /**
     * Создаёт цель счётчика.
     * @param title название цели
     * @param targetCount целевое количество
     * @param goalType тип цели (DAILY или ZIKR_TARGET)
     * @return созданная цель
     */
    suspend operator fun invoke(
        title: String,
        targetCount: Int,
        goalType: GoalType
    ): CounterGoal {
        val goal = CounterGoal(
            id = UUID.randomUUID().toString(),
            title = title,
            goalType = goalType,
            targetCount = targetCount,
            currentCount = 0,
            isCompleted = false,
            createdAt = Date()
        )

        goalRepository.createGoal(goal)
        return goal
    }
}
