package com.example.khatmusalawattime.domain.repository

import com.example.khatmusalawattime.domain.model.GoalList
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for note-related operations
 */
interface NoteRepository {
    fun getAllGoalLists(): Flow<List<GoalList>>
    suspend fun getGoalListById(id: String): GoalList?
    suspend fun addGoalList(goalList: GoalList): String
    suspend fun updateGoalList(goalList: GoalList)
    suspend fun deleteGoalList(id: String)
    suspend fun updateGoalListTitle(id: String, title: String)
    suspend fun addTask(title: String, goalList: GoalList): GoalList
    suspend fun updateTaskTitle(taskId: String, title: String, goalList: GoalList): GoalList
    suspend fun toggleTaskCompletion(taskId: String, goalList: GoalList): GoalList
    suspend fun deleteTask(taskId: String, goalList: GoalList): GoalList
    suspend fun saveGoalLists(goalLists: List<GoalList>)
}