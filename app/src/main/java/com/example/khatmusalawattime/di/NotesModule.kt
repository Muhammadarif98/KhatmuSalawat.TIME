package com.example.khatmusalawattime.di

import com.example.khatmusalawattime.data.local.dao.NoteDao
import com.example.khatmusalawattime.data.repository.NotesRepositoryImpl
import com.example.khatmusalawattime.domain.repository.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotesModule {

    @Provides
    @Singleton
    fun provideNotesRepository(
        noteDao: NoteDao
    ): NotesRepository {
        return NotesRepositoryImpl(noteDao)
    }
} 