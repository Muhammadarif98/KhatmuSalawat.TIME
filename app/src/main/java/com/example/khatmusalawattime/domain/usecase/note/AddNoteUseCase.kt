package com.example.khatmusalawattime.domain.usecase.note

import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Use case for adding a new note
 */
class AddNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(title: String, goalList: GoalList): GoalList {
        return repository.addTask(title, goalList)
    }
}