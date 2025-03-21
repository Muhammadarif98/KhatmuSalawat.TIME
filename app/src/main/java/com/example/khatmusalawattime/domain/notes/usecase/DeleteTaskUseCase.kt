package com.example.khatmusalawattime.domain.notes.usecase

import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Use case для удаления задачи из списка заметок.
 */
class DeleteTaskUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    /**
     * Удаляет задачу из указанного списка заметок.
     * @param taskId ID задачи для удаления
     * @param goalList Список заметок, содержащий задачу
     * @return Обновленный список заметок без удаленной задачи
     */
    suspend operator fun invoke(taskId: String, goalList: GoalList): GoalList {
        val updatedTasks = goalList.tasks.filter { it.id != taskId }
        val updatedList = goalList.copy(tasks = updatedTasks)
        
        repository.updateGoalList(updatedList)
        return updatedList
    }
} 