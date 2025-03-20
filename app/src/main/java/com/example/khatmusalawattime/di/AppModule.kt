package com.example.khatmusalawattime.di

import android.content.Context
import androidx.room.Room
import com.example.khatmusalawattime.data.local.dao.CounterDao
import com.example.khatmusalawattime.data.local.dao.NoteDao
import com.example.khatmusalawattime.data.local.database.AppDatabase
import com.example.khatmusalawattime.data.repository.NoteRepositoryImpl
import com.example.khatmusalawattime.data.repository.ReminderRepositoryImpl
import com.example.khatmusalawattime.domain.repository.NoteRepository
import com.example.khatmusalawattime.domain.repository.ReminderRepository
import com.example.khatmusalawattime.domain.usecase.note.AddNoteListUseCase
import com.example.khatmusalawattime.domain.usecase.note.AddNoteUseCase
import com.example.khatmusalawattime.domain.usecase.note.DeleteNoteUseCase
import com.example.khatmusalawattime.domain.usecase.note.GetAllNoteListsUseCase
import com.example.khatmusalawattime.domain.usecase.note.GetNotesByListUseCase
import com.example.khatmusalawattime.domain.usecase.note.NoteUseCases
import com.example.khatmusalawattime.domain.usecase.note.ToggleNoteCompletionUseCase
import com.example.khatmusalawattime.domain.usecase.note.UpdateNoteUseCase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Room Database
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "khatmusalawat_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCounterDao(database: AppDatabase): CounterDao {
        return database.counterDao()
    }
    
    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    // Gson (для парсинга JSON)
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    // Репозиторий для работы с данными о времени Салавата и Хатму
    @Provides
    @Singleton
    fun provideReminderRepository(
        @ApplicationContext context: Context,
        gson: Gson
    ): ReminderRepository = ReminderRepositoryImpl(context, gson)
    
    // Репозиторий для работы с заметками
    @Provides
    @Singleton
    fun provideNoteRepository(
        noteDao: NoteDao
    ): NoteRepository = NoteRepositoryImpl(noteDao)
    
    // Use cases для заметок
    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getAllNoteLists = GetAllNoteListsUseCase(repository),
            getNotesByList = GetNotesByListUseCase(repository),
            addNoteList = AddNoteListUseCase(repository),
            addNote = AddNoteUseCase(repository),
            updateNote = UpdateNoteUseCase(repository),
            deleteNote = DeleteNoteUseCase(repository),
            toggleNoteCompletion = ToggleNoteCompletionUseCase(repository)
        )
    }
}