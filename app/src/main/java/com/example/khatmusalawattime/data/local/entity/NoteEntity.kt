package com.example.khatmusalawattime.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.model.Task
import java.util.Date

@Entity(tableName = "note_lists")
data class NoteListEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val tasks: List<NoteEntity>,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toGoalList(tasks: List<Task>): GoalList {
        return GoalList(
            id = id,
            title = title,
            isCompleted = isCompleted,
            tasks = tasks,
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt)
        )
    }
    
    companion object {
        fun fromGoalList(goalList: GoalList): NoteListEntity {
            return NoteListEntity(
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
                        updatedAt = task.updatedAt.time,
                        linkedGoalId = task.linkedGoalId
                    )
                },
                createdAt = goalList.createdAt.time,
                updatedAt = goalList.updatedAt.time
            )
        }
    }
}

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = NoteListEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteListId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("noteListId")]
)
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val noteListId: String,
    val title: String,
    val content: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val linkedGoalId: String? = null
) {
    fun toTask(): Task {
        return Task(
            id = id,
            title = title,
            isCompleted = isCompleted,
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt),
            linkedGoalId = linkedGoalId
        )
    }

    companion object {
        fun fromTask(task: Task, noteListId: String): NoteEntity {
            return NoteEntity(
                id = task.id,
                noteListId = noteListId,
                title = task.title,
                content = "",
                isCompleted = task.isCompleted,
                createdAt = task.createdAt.time,
                updatedAt = task.updatedAt.time,
                linkedGoalId = task.linkedGoalId
            )
        }
    }
}