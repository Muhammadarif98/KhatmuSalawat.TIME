package com.example.khatmusalawattime.domain.usecase.note

import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Use case for toggling the completion status of a note
 */
class ToggleNoteCompletionUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(taskId: String, goalList: GoalList): GoalList {
        return repository.toggleTaskCompletion(taskId, goalList)
    }
}