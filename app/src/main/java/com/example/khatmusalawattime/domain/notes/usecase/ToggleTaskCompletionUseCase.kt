package com.example.khatmusalawattime.domain.notes.usecase

import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Use case для изменения статуса выполнения задачи.
 */
class ToggleTaskCompletionUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    /**
     * Изменяет статус выполнения задачи в указанном списке.
     * @param taskId ID задачи
     * @param goalList Список заметок, содержащий задачу
     * @return Обновленный список заметок
     */
    suspend operator fun invoke(taskId: String, goalList: GoalList): GoalList {
        val updatedTasks = goalList.tasks.map { task ->
            if (task.id == taskId) task.copy(isCompleted = !task.isCompleted)
            else task
        }
        
        val allTasksCompleted = updatedTasks.all { it.isCompleted }
        val updatedList = goalList.copy(
            tasks = updatedTasks,
            isCompleted = allTasksCompleted
        )
        
        repository.updateGoalList(updatedList)
        return updatedList
    }
} 