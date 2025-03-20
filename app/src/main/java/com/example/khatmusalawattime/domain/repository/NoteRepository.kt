package com.example.khatmusalawattime.domain.repository

import com.example.khatmusalawattime.domain.model.Note
import com.example.khatmusalawattime.domain.model.NoteList
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for note-related operations
 */
interface NoteRepository {
    // NoteList operations
    suspend fun getAllNoteLists(): Flow<List<NoteList>>
    suspend fun getNoteListById(id: String): NoteList?
    suspend fun insertNoteList(noteList: NoteList): String
    suspend fun updateNoteList(noteList: NoteList)
    suspend fun deleteNoteList(id: String)
    
    // Note operations
    suspend fun getAllNotes(): Flow<List<Note>>
    suspend fun getNotesByListId(noteListId: String): Flow<List<Note>>
    suspend fun getNoteById(id: String): Note?
    suspend fun insertNote(note: Note): String
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: String)
    suspend fun deleteNotesByListId(noteListId: String)
} 