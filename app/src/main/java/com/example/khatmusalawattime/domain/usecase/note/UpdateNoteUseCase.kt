package com.example.khatmusalawattime.domain.usecase.note

import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Use case for updating a note
 */
class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(taskId: String, title: String, goalList: GoalList): GoalList {
        return repository.updateTaskTitle(taskId, title, goalList)
    }
}