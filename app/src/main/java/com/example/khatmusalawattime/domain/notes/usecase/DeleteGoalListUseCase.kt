package com.example.khatmusalawattime.domain.notes.usecase

import com.example.khatmusalawattime.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Use case для удаления списка заметок.
 */
class DeleteGoalListUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    /**
     * Удаляет список заметок по ID.
     * @param listId ID списка заметок для удаления
     */
    suspend operator fun invoke(listId: String) {
        repository.deleteGoalList(listId)
    }
} 