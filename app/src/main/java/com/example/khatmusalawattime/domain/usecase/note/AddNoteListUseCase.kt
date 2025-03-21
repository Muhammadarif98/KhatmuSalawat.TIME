package com.example.khatmusalawattime.domain.usecase.note

import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.repository.NoteRepository
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for adding a new note list
 */
class AddNoteListUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(title: String): String {
        if (title.isBlank()) {
            throw IllegalArgumentException("Note list title cannot be blank")
        }

        val goalList = GoalList(
            id = UUID.randomUUID().toString(),
            title = title,
            isCompleted = false,
            tasks = emptyList(),
            createdAt = Date(),
            updatedAt = Date()
        )
        
        return repository.addGoalList(goalList)
    }
} 