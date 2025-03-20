package com.example.khatmusalawattime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.khatmusalawattime.data.local.entity.NoteEntity
import com.example.khatmusalawattime.data.local.entity.NoteListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    // NoteList operations
    @Query("SELECT * FROM note_lists ORDER BY updatedAt DESC")
    fun getAllNoteLists(): Flow<List<NoteListEntity>>
    
    @Query("SELECT * FROM note_lists WHERE id = :id")
    suspend fun getNoteListById(id: String): NoteListEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteList(noteList: NoteListEntity): Long
    
    @Update
    suspend fun updateNoteList(noteList: NoteListEntity)
    
    @Query("DELETE FROM note_lists WHERE id = :id")
    suspend fun deleteNoteList(id: String)
    
    // Note operations
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE noteListId = :noteListId ORDER BY updatedAt DESC")
    fun getNotesByListId(noteListId: String): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long
    
    @Update
    suspend fun updateNote(note: NoteEntity)
    
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: String)
    
    @Query("DELETE FROM notes WHERE noteListId = :noteListId")
    suspend fun deleteNotesByListId(noteListId: String)
} 