package com.example.khatmusalawattime.data.repository

import com.example.khatmusalawattime.data.local.dao.NoteDao
import com.example.khatmusalawattime.data.local.entity.NoteEntity
import com.example.khatmusalawattime.data.local.entity.NoteListEntity
import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.model.Task
import com.example.khatmusalawattime.domain.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NotesRepository {
    
    override fun getAllGoalLists(): Flow<List<GoalList>> {
        // Combine обоих Flow для реактивности на изменения в обеих таблицах
        return combine(
            noteDao.getAllNoteLists(),
            noteDao.getAllNotes()
        ) { listEntities, allNotes ->
            listEntities.map { entity ->
                // Фильтруем заметки для этого списка
                val tasks = allNotes
                    .filter { it.noteListId == entity.id }
                    .map { noteEntity -> noteEntity.toTask() }

                // Преобразуем сущность списка в модель
                entity.toGoalList(tasks)
            }
        }.flowOn(Dispatchers.IO) // Обработка данных в фоновом потоке
    }

    override suspend fun getGoalListById(id: String): GoalList? {
        val entity = noteDao.getNoteListById(id) ?: return null
        
        // Получаем заметки для этого списка
        val notes = noteDao.getNotesByListId(id).first()
        
        // Преобразуем заметки в задачи
        val tasks = notes.map { noteEntity -> noteEntity.toTask() }
        
        // Возвращаем преобразованную модель
        return entity.toGoalList(tasks)
    }

    override suspend fun addGoalList(goalList: GoalList): String {
        // Создаем сущность списка заметок с текущей датой
        val entity = NoteListEntity.fromGoalList(
            goalList.copy(
                createdAt = Date(),
                updatedAt = Date()
            )
        )
        
        noteDao.insertNoteList(entity)
        
        // Сохраняем каждую задачу как заметку
        goalList.tasks.forEach { task ->
            val noteEntity = NoteEntity.fromTask(
                task.copy(
                    createdAt = Date(),
                    updatedAt = Date()
                ), 
                goalList.id
            )
            
            noteDao.insertNote(noteEntity)
        }
        
        return goalList.id
    }

    override suspend fun updateGoalList(goalList: GoalList) {
        // Получаем текущий список из базы
        val currentList = noteDao.getNoteListById(goalList.id)
        
        if (currentList != null) {
            // Обновляем список с сохранением даты создания
            val entity = NoteListEntity.fromGoalList(
                goalList.copy(
                    createdAt = Date(currentList.createdAt),
                    updatedAt = Date()
                )
            )
            
            noteDao.updateNoteList(entity)
            
            // Получаем текущие задачи
            val currentNotes = noteDao.getNotesByListId(goalList.id).first()
            val currentTaskIds = currentNotes.map { it.id }
            
            // Определяем, какие задачи нужно удалить
            val taskIdsToKeep = goalList.tasks.map { it.id }
            val taskIdsToDelete = currentTaskIds.filter { it !in taskIdsToKeep }
            
            // Удаляем ненужные задачи
            taskIdsToDelete.forEach { id ->
                noteDao.deleteNote(id)
            }
            
            // Обновляем или добавляем задачи
            goalList.tasks.forEach { task ->
                val currentNote = currentNotes.find { it.id == task.id }
                
                if (currentNote != null) {
                    // Обновляем существующую заметку
                    val updatedNote = NoteEntity.fromTask(
                        task.copy(
                            createdAt = Date(currentNote.createdAt),
                            updatedAt = Date()
                        ),
                        goalList.id
                    )
                    
                    noteDao.updateNote(updatedNote)
                } else {
                    // Добавляем новую заметку
                    val newNote = NoteEntity.fromTask(
                        task.copy(
                            createdAt = Date(),
                            updatedAt = Date()
                        ),
                        goalList.id
                    )
                    
                    noteDao.insertNote(newNote)
                }
            }
        }
    }

    override suspend fun deleteGoalList(id: String) {
        noteDao.deleteNoteList(id)
        // Заметки удалятся автоматически благодаря CASCADE
    }

    override suspend fun updateGoalListTitle(id: String, title: String) {
        val entity = noteDao.getNoteListById(id) ?: return
        
        val updatedEntity = entity.copy(
            title = title,
            updatedAt = System.currentTimeMillis()
        )
        
        noteDao.updateNoteList(updatedEntity)
    }

    override suspend fun addTask(title: String, goalList: GoalList): GoalList {
        val taskId = UUID.randomUUID().toString()
        
        // Создаем новую задачу
        val task = Task(
            id = taskId,
            title = title,
            isCompleted = false,
            createdAt = Date(),
            updatedAt = Date()
        )
        
        // Создаем новую заметку для задачи
        val noteEntity = NoteEntity.fromTask(task, goalList.id)
        
        noteDao.insertNote(noteEntity)
        
        // Возвращаем обновленный список задач
        return goalList.copy(
            tasks = goalList.tasks + task,
            updatedAt = Date()
        )
    }

    override suspend fun updateTaskTitle(taskId: String, title: String, goalList: GoalList): GoalList {
        // Находим заметку, соответствующую задаче
        val note = noteDao.getNoteById(taskId) ?: return goalList
        
        // Обновляем заголовок заметки
        val updatedNote = note.copy(
            title = title,
            updatedAt = System.currentTimeMillis()
        )
        
        noteDao.updateNote(updatedNote)
        
        // Находим и обновляем задачу в списке
        val updatedTasks = goalList.tasks.map { task ->
            if (task.id == taskId) {
                task.copy(
                    title = title,
                    updatedAt = Date()
                )
            } else {
                task
            }
        }
        
        // Возвращаем обновленный список задач
        return goalList.copy(
            tasks = updatedTasks,
            updatedAt = Date()
        )
    }

    override suspend fun toggleTaskCompletion(taskId: String, goalList: GoalList): GoalList {
        // Находим заметку, соответствующую задаче
        val note = noteDao.getNoteById(taskId) ?: return goalList
        
        // Переключаем состояние выполнения заметки
        val updatedNote = note.copy(
            isCompleted = !note.isCompleted,
            updatedAt = System.currentTimeMillis()
        )
        
        noteDao.updateNote(updatedNote)
        
        // Обновляем задачу в списке
        val updatedTasks = goalList.tasks.map { task ->
            if (task.id == taskId) {
                task.copy(
                    isCompleted = !task.isCompleted,
                    updatedAt = Date()
                )
            } else {
                task
            }
        }
        
        // Проверяем состояние всех задач, чтобы определить состояние списка
        val allTasksCompleted = updatedTasks.all { it.isCompleted }
        val anyTasksCompleted = updatedTasks.any { it.isCompleted }
        
        // Определяем новое состояние списка
        val newListState = if (updatedTasks.isEmpty()) false else allTasksCompleted
        
        // Если состояние списка изменилось, обновляем его
        if (newListState != goalList.isCompleted) {
            val listEntity = noteDao.getNoteListById(goalList.id)
            if (listEntity != null) {
                val updatedList = listEntity.copy(
                    isCompleted = newListState,
                    updatedAt = System.currentTimeMillis()
                )
                noteDao.updateNoteList(updatedList)
            }
        }
        
        // Возвращаем обновленный список задач
        return goalList.copy(
            tasks = updatedTasks,
            isCompleted = newListState,
            updatedAt = Date()
        )
    }

    override suspend fun deleteTask(taskId: String, goalList: GoalList): GoalList {
        // Удаляем заметку
        noteDao.deleteNote(taskId)
        
        // Удаляем задачу из списка
        val updatedTasks = goalList.tasks.filter { it.id != taskId }
        
        // Используем anyTasksCompleted для определения статуса списка
        val anyTasksCompleted = updatedTasks.any { it.isCompleted }
        val allTasksCompleted = updatedTasks.all { it.isCompleted }
        
        val updatedGoalList = goalList.copy(
            tasks = updatedTasks,
            // Если нет задач или все задачи выполнены
            isCompleted = updatedTasks.isNotEmpty() && allTasksCompleted,
            updatedAt = Date()
        )
        
        // Если состояние списка изменилось, обновляем его
        if (updatedGoalList.isCompleted != goalList.isCompleted) {
            val listEntity = noteDao.getNoteListById(goalList.id)
            if (listEntity != null) {
                val updatedList = listEntity.copy(
                    isCompleted = updatedGoalList.isCompleted,
                    updatedAt = System.currentTimeMillis()
                )
                noteDao.updateNoteList(updatedList)
            }
        }
        
        // Возвращаем обновленный список задач
        return updatedGoalList
    }

    override suspend fun saveGoalLists(goalLists: List<GoalList>) {
        // Удаляем все существующие списки и их заметки
        noteDao.deleteAllNotesByQuery()
        noteDao.deleteAllNoteListsByQuery()
        
        // Сохраняем новые списки
        goalLists.forEach { goalList ->
            addGoalList(goalList)
        }
    }
} 