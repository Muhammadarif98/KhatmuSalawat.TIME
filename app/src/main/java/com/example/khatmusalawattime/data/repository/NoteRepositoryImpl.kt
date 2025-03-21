package com.example.khatmusalawattime.data.repository

import com.example.khatmusalawattime.data.local.dao.NoteDao
import com.example.khatmusalawattime.data.local.entity.NoteEntity
import com.example.khatmusalawattime.data.local.entity.NoteListEntity
import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.model.Task
import com.example.khatmusalawattime.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getAllGoalLists(): Flow<List<GoalList>> {
        return noteDao.getAllNoteLists().map { entities ->
            entities.map { entity -> 
                GoalList(
                    id = entity.id,
                    title = entity.title,
                    isCompleted = entity.isCompleted,
                    tasks = entity.tasks.map { taskEntity ->
                        Task(
                            id = taskEntity.id,
                            title = taskEntity.title,
                            isCompleted = taskEntity.isCompleted,
                            createdAt = Date(taskEntity.createdAt),
                            updatedAt = Date(taskEntity.updatedAt)
                        )
                    },
                    createdAt = Date(entity.createdAt),
                    updatedAt = Date(entity.updatedAt)
                )
            }
        }
    }

    override suspend fun getGoalListById(id: String): GoalList? {
        val entity = noteDao.getNoteListById(id) ?: return null
        return GoalList(
            id = entity.id,
            title = entity.title,
            isCompleted = entity.isCompleted,
            tasks = entity.tasks.map { taskEntity ->
                Task(
                    id = taskEntity.id,
                    title = taskEntity.title,
                    isCompleted = taskEntity.isCompleted,
                    createdAt = Date(taskEntity.createdAt),
                    updatedAt = Date(taskEntity.updatedAt)
                )
            },
            createdAt = Date(entity.createdAt),
            updatedAt = Date(entity.updatedAt)
        )
    }

    override suspend fun addGoalList(goalList: GoalList): String {
        val entity = NoteListEntity(
            id = goalList.id,
            title = goalList.title,
            isCompleted = goalList.isCompleted,
            tasks = goalList.tasks.map { task ->
                NoteEntity(
                    id = task.id,
                    noteListId = goalList.id,
                    title = task.title,
                    content = "",
                    isCompleted = task.isCompleted,
                    createdAt = task.createdAt.time,
                    updatedAt = task.updatedAt.time
                )
            },
            createdAt = goalList.createdAt.time,
            updatedAt = goalList.updatedAt.time
        )
        noteDao.insertNoteList(entity)
        return goalList.id
    }

    override suspend fun updateGoalList(goalList: GoalList) {
        val entity = NoteListEntity(
            id = goalList.id,
            title = goalList.title,
            isCompleted = goalList.isCompleted,
            tasks = goalList.tasks.map { task ->
                NoteEntity(
                    id = task.id,
                    noteListId = goalList.id,
                    title = task.title,
                    content = "",
                    isCompleted = task.isCompleted,
                    createdAt = task.createdAt.time,
                    updatedAt = task.updatedAt.time
                )
            },
            createdAt = goalList.createdAt.time,
            updatedAt = goalList.updatedAt.time
        )
        noteDao.updateNoteList(entity)
    }

    override suspend fun deleteGoalList(id: String) {
        noteDao.deleteNoteList(id)
    }

    override suspend fun updateGoalListTitle(id: String, title: String) {
        val goalList = getGoalListById(id) ?: return
        val updatedGoalList = goalList.copy(
            title = title,
            updatedAt = Date()
        )
        updateGoalList(updatedGoalList)
    }

    override suspend fun addTask(title: String, goalList: GoalList): GoalList {
        val task = Task(
            id = java.util.UUID.randomUUID().toString(),
            title = title,
            isCompleted = false,
            createdAt = Date(),
            updatedAt = Date()
        )
        val updatedGoalList = goalList.copy(
            tasks = goalList.tasks + task,
            updatedAt = Date()
        )
        updateGoalList(updatedGoalList)
        return updatedGoalList
    }

    override suspend fun updateTaskTitle(taskId: String, title: String, goalList: GoalList): GoalList {
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
        val updatedGoalList = goalList.copy(
            tasks = updatedTasks,
            updatedAt = Date()
        )
        updateGoalList(updatedGoalList)
        return updatedGoalList
    }

    override suspend fun toggleTaskCompletion(taskId: String, goalList: GoalList): GoalList {
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
        val updatedGoalList = goalList.copy(
            tasks = updatedTasks,
            isCompleted = updatedTasks.all { it.isCompleted },
            updatedAt = Date()
        )
        updateGoalList(updatedGoalList)
        return updatedGoalList
    }

    override suspend fun deleteTask(taskId: String, goalList: GoalList): GoalList {
        val updatedTasks = goalList.tasks.filter { it.id != taskId }
        val updatedGoalList = goalList.copy(
            tasks = updatedTasks,
            isCompleted = updatedTasks.all { it.isCompleted },
            updatedAt = Date()
        )
        updateGoalList(updatedGoalList)
        return updatedGoalList
    }

    override suspend fun saveGoalLists(goalLists: List<GoalList>) {
        goalLists.forEach { goalList ->
            addGoalList(goalList)
        }
    }
} 