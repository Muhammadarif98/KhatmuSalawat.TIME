package com.example.khatmusalawattime.data.repository

import com.example.khatmusalawattime.data.local.dao.CounterGoalDao
import com.example.khatmusalawattime.data.local.entity.CounterGoalEntity
import com.example.khatmusalawattime.domain.model.CounterGoal
import com.example.khatmusalawattime.domain.model.GoalType
import com.example.khatmusalawattime.domain.repository.CounterGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CounterGoalRepositoryImpl @Inject constructor(
    private val goalDao: CounterGoalDao
) : CounterGoalRepository {

    override fun getActiveGoals(): Flow<List<CounterGoal>> {
        return goalDao.getActiveGoals().map { entities ->
            entities.map { it.toCounterGoal() }
        }
    }

    override fun getAllGoals(): Flow<List<CounterGoal>> {
        return goalDao.getAllGoals().map { entities ->
            entities.map { it.toCounterGoal() }
        }
    }

    override suspend fun getGoalById(id: String): CounterGoal? {
        return goalDao.getGoalById(id)?.toCounterGoal()
    }

    override suspend fun getActiveGoalByType(type: GoalType): CounterGoal? {
        return goalDao.getActiveGoalByType(type.name)?.toCounterGoal()
    }

    override suspend fun createGoal(goal: CounterGoal) {
        goalDao.insert(CounterGoalEntity.fromCounterGoal(goal))
    }

    override suspend fun updateProgress(goalId: String, count: Int) {
        val goal = goalDao.getGoalById(goalId) ?: return
        val newCount = goal.currentCount + count
        goalDao.updateProgress(goalId, newCount)

        // Автоматически завершаем цель если достигнута
        if (newCount >= goal.targetCount) {
            goalDao.updateCompleted(goalId, true)
        }
    }

    override suspend fun completeGoal(goalId: String) {
        goalDao.updateCompleted(goalId, true)
    }

    override suspend fun deleteGoal(goalId: String) {
        goalDao.delete(goalId)
    }

    override suspend fun resetDailyGoals() {
        goalDao.resetDailyGoals()
    }

    override suspend fun resetProgress(goalId: String) {
        goalDao.resetProgress(goalId)
    }
}
