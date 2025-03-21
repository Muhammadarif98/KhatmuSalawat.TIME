package com.example.khatmusalawattime.domain.usecase.note

import com.example.khatmusalawattime.domain.model.Task
import com.example.khatmusalawattime.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Use case for getting notes by list ID
 */
class GetNotesByListUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(listId: String): List<Task> {
        val goalList = repository.getGoalListById(listId)
        return goalList?.tasks ?: emptyList()
    }
}