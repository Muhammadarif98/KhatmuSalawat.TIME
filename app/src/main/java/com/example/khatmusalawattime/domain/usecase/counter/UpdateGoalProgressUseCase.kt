package com.example.khatmusalawattime.domain.usecase.counter

import com.example.khatmusalawattime.domain.repository.CounterGoalRepository
import javax.inject.Inject

class UpdateGoalProgressUseCase @Inject constructor(
    private val goalRepository: CounterGoalRepository
) {
    /**
     * Обновляет прогресс цели.
     * @param goalId ID цели
     * @param incrementBy на сколько увеличить счётчик
     * @return true если цель была завершена этим обновлением
     */
    suspend operator fun invoke(goalId: String, incrementBy: Int = 1): Boolean {
        val goal = goalRepository.getGoalById(goalId) ?: return false

        if (goal.isCompleted) return false

        val newCount = goal.currentCount + incrementBy
        goalRepository.updateProgress(goalId, incrementBy)

        // Проверяем достижение цели
        if (newCount >= goal.targetCount) {
            goalRepository.completeGoal(goal.id)
            return true
        }

        return false
    }
}
