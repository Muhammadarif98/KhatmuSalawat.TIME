package com.example.khatmusalawattime.domain.notes.usecase

import com.example.khatmusalawattime.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Use case для обновления заголовка списка заметок.
 */
class UpdateGoalListTitleUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    /**
     * Обновляет заголовок списка заметок.
     * @param listId ID списка заметок
     * @param title Новый заголовок
     * @throws IllegalArgumentException если заголовок пустой
     */
    suspend operator fun invoke(listId: String, title: String) {
        if (title.isBlank()) {
            throw IllegalArgumentException("Заголовок не может быть пустым")
        }
        
        repository.updateGoalListTitle(listId, title)
    }
} 