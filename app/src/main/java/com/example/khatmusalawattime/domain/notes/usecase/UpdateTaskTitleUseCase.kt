package com.example.khatmusalawattime.domain.notes.usecase

import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Use case для обновления заголовка задачи.
 */
class UpdateTaskTitleUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    /**
     * Обновляет заголовок задачи в указанном списке.
     * @param taskId ID задачи
     * @param title Новый заголовок
     * @param goalList Список заметок, содержащий задачу
     * @return Обновленный список заметок
     * @throws IllegalArgumentException если заголовок пустой
     */
    suspend operator fun invoke(taskId: String, title: String, goalList: GoalList): GoalList {
        if (title.isBlank()) {
            throw IllegalArgumentException("Заголовок задачи не может быть пустым")
        }
        
        val updatedTasks = goalList.tasks.map { task ->
            if (task.id == taskId) task.copy(title = title)
            else task
        }
        
        val updatedList = goalList.copy(tasks = updatedTasks)
        repository.updateGoalList(updatedList)
        
        return updatedList
    }
} 