package com.example.khatmusalawattime.domain.notes.usecase

import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case для получения всех списков заметок.
 */
class GetAllGoalListsUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    /**
     * Получает все списки заметок.
     * @return [Flow] со списком всех списков заметок [GoalList]
     */
    operator fun invoke(): Flow<List<GoalList>> {
        return repository.getAllGoalLists()
    }
} 