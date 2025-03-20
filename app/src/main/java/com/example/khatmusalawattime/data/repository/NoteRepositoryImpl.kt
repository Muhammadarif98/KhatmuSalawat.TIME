package com.example.khatmusalawattime.data.repository

import com.example.khatmusalawattime.data.local.dao.NoteDao
import com.example.khatmusalawattime.data.local.entity.NoteEntity
import com.example.khatmusalawattime.data.local.entity.NoteListEntity
import com.example.khatmusalawattime.domain.model.Note
import com.example.khatmusalawattime.domain.model.NoteList
import com.example.khatmusalawattime.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    
    override suspend fun getAllNoteLists(): Flow<List<NoteList>> {
        return noteDao.getAllNoteLists().map { entities ->
            entities.map { it.toNoteList() }
        }
    }

    override suspend fun getNoteListById(id: String): NoteList? {
        return noteDao.getNoteListById(id)?.toNoteList()
    }

    override suspend fun insertNoteList(noteList: NoteList): String {
        val entity = NoteListEntity.fromNoteList(noteList)
        noteDao.insertNoteList(entity)
        return noteList.id
    }

    override suspend fun updateNoteList(noteList: NoteList) {
        val entity = NoteListEntity.fromNoteList(noteList)
        noteDao.updateNoteList(entity)
    }

    override suspend fun deleteNoteList(id: String) {
        noteDao.deleteNoteList(id)
    }

    override suspend fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toNote() }
        }
    }

    override suspend fun getNotesByListId(noteListId: String): Flow<List<Note>> {
        return noteDao.getNotesByListId(noteListId).map { entities ->
            entities.map { it.toNote() }
        }
    }

    override suspend fun getNoteById(id: String): Note? {
        return noteDao.getNoteById(id)?.toNote()
    }

    override suspend fun insertNote(note: Note): String {
        val entity = NoteEntity.fromNote(note)
        noteDao.insertNote(entity)
        return note.id
    }

    override suspend fun updateNote(note: Note) {
        val entity = NoteEntity.fromNote(note)
        noteDao.updateNote(entity)
    }

    override suspend fun deleteNote(id: String) {
        noteDao.deleteNote(id)
    }

    override suspend fun deleteNotesByListId(noteListId: String) {
        noteDao.deleteNotesByListId(noteListId)
    }
} 