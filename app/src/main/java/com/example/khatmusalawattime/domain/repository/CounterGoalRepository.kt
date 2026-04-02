package com.example.khatmusalawattime.domain.repository

import com.example.khatmusalawattime.domain.model.CounterGoal
import com.example.khatmusalawattime.domain.model.GoalType
import kotlinx.coroutines.flow.Flow

interface CounterGoalRepository {
    fun getActiveGoals(): Flow<List<CounterGoal>>
    fun getAllGoals(): Flow<List<CounterGoal>>
    suspend fun getGoalById(id: String): CounterGoal?
    suspend fun getActiveGoalByType(type: GoalType): CounterGoal?
    suspend fun createGoal(goal: CounterGoal)
    suspend fun updateProgress(goalId: String, count: Int)
    suspend fun completeGoal(goalId: String)
    suspend fun deleteGoal(goalId: String)
    suspend fun resetDailyGoals()
    suspend fun resetProgress(goalId: String)
}
