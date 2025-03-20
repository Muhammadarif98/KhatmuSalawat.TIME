package com.example.khatmusalawattime.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.khatmusalawattime.domain.model.Note
import com.example.khatmusalawattime.domain.model.NoteList
import java.util.Date

@Entity(tableName = "note_lists")
data class NoteListEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val completed: Boolean
) {
    fun toNoteList(): NoteList {
        return NoteList(
            id = id,
            title = title,
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt),
            completed = completed
        )
    }
    
    companion object {
        fun fromNoteList(noteList: NoteList): NoteListEntity {
            return NoteListEntity(
                id = noteList.id,
                title = noteList.title,
                createdAt = noteList.createdAt.time,
                updatedAt = noteList.updatedAt.time,
                completed = noteList.completed
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
    val createdAt: Long,
    val updatedAt: Long,
    val completed: Boolean
) {
    fun toNote(): Note {
        return Note(
            id = id,
            noteListId = noteListId,
            title = title,
            content = content,
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt),
            completed = completed
        )
    }
    
    companion object {
        fun fromNote(note: Note): NoteEntity {
            return NoteEntity(
                id = note.id,
                noteListId = note.noteListId,
                title = note.title,
                content = note.content,
                createdAt = note.createdAt.time,
                updatedAt = note.updatedAt.time,
                completed = note.completed
            )
        }
    }
} 