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

    /**
     * Добавляет новую задачу в список
     * @param title Заголовок новой задачи
     * @param goalList Список задач, в который добавляется новая задача
     * @return Обновленный список задач
     */
    suspend fun addTask(title: String, goalList: GoalList): GoalList

    /**
     * Обновляет заголовок задачи
     * @param taskId ID задачи
     * @param title Новый заголовок
     * @param goalList Список задач, содержащий обновляемую задачу
     * @return Обновленный список задач
     */
    suspend fun updateTaskTitle(taskId: String, title: String, goalList: GoalList): GoalList

    /**
     * Переключает состояние выполнения задачи
     * @param taskId ID задачи
     * @param goalList Список задач, содержащий переключаемую задачу
     * @return Обновленный список задач
     */
    suspend fun toggleTaskCompletion(taskId: String, goalList: GoalList): GoalList

    /**
     * Удаляет задачу из списка
     * @param taskId ID задачи
     * @param goalList Список задач, из которого удаляется задача
     * @return Обновленный список задач
     */
    suspend fun deleteTask(taskId: String, goalList: GoalList): GoalList
} 