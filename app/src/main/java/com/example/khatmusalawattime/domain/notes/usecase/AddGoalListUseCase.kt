package com.example.khatmusalawattime.domain.notes.usecase

import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.repository.NotesRepository
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * Use case для добавления нового списка заметок.
 */
class AddGoalListUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    /**
     * Добавляет новый список заметок с указанным заголовком.
     * @param title Заголовок нового списка заметок
     * @return ID созданного списка заметок
     * @throws IllegalArgumentException если заголовок пустой
     */
    suspend operator fun invoke(title: String): String {
        if (title.isBlank()) {
            throw IllegalArgumentException("Title cannot be blank")
        }
        
        val now = Date()
        val goalList = GoalList(
            id = UUID.randomUUID().toString(),
            title = title,
            isCompleted = false,
            tasks = emptyList(),
            createdAt = now,
            updatedAt = now
        )
        
        return repository.addGoalList(goalList)
    }
} 