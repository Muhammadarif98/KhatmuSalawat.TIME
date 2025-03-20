package com.example.khatmusalawattime.domain.repository

import com.example.khatmusalawattime.domain.model.GoalList
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с заметками.
 * Определяет контракт для операций с заметками.
 */
interface NotesRepository {
    
    /**
     * Получает все списки заметок
     * @return [Flow] со списком всех списков заметок [GoalList]
     */
    fun getAllGoalLists(): Flow<List<GoalList>>
    
    /**
     * Получает конкретный список заметок по ID
     * @param id ID списка заметок
     * @return Список заметок [GoalList] с указанным ID или null, если не найден
     */
    suspend fun getGoalListById(id: String): GoalList?
    
    /**
     * Добавляет новый список заметок
     * @param goalList Список заметок для добавления
     * @return ID добавленного списка
     */
    suspend fun addGoalList(goalList: GoalList): String
    
    /**
     * Обновляет существующий список заметок
     * @param goalList Обновленный список заметок
     */
    suspend fun updateGoalList(goalList: GoalList)
    
    /**
     * Удаляет список заметок по ID
     * @param id ID списка заметок для удаления
     */
    suspend fun deleteGoalList(id: String)
    
    /**
     * Обновляет заголовок списка заметок
     * @param id ID списка заметок
     * @param title Новый заголовок
     */
    suspend fun updateGoalListTitle(id: String, title: String)
    
    /**
     * Сохраняет все списки заметок
     * @param goalLists Списки заметок для сохранения
     */
    suspend fun saveGoalLists(goalLists: List<GoalList>)
} 