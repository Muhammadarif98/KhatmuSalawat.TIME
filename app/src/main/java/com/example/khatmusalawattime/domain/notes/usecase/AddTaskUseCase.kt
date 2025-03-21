package com.example.khatmusalawattime.domain.notes.usecase

import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.model.Task
import com.example.khatmusalawattime.domain.repository.NotesRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Use case для добавления новой задачи в список заметок.
 */
class AddTaskUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    /**
     * Добавляет новую задачу в указанный список заметок.
     * @param title Заголовок задачи
     * @param goalList Список заметок, в который добавляется задача
     * @return Обновленный список заметок с новой задачей
     * @throws IllegalArgumentException если заголовок пустой
     */
    suspend operator fun invoke(title: String, goalList: GoalList): GoalList {
        if (title.isBlank()) {
            throw IllegalArgumentException("Заголовок задачи не может быть пустым")
        }
        
        val newTask = Task(
            id = UUID.randomUUID().toString(),
            title = title
        )
        
        val updatedList = goalList.copy(
            tasks = goalList.tasks + newTask
        )
        
        repository.updateGoalList(updatedList)
        return updatedList
    }
} 