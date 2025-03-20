package com.example.khatmusalawattime.di

import android.content.Context
import com.example.khatmusalawattime.data.notes.datasource.local.SharedPreferencesNotesDataSource
import com.example.khatmusalawattime.data.notes.repository.NotesRepositoryImpl
import com.example.khatmusalawattime.domain.repository.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotesModule {
    
    @Provides
    @Singleton
    fun provideSharedPreferencesNotesDataSource(
        @ApplicationContext context: Context
    ): SharedPreferencesNotesDataSource {
        return SharedPreferencesNotesDataSource(context)
    }
    
    @Provides
    @Singleton
    fun provideNotesRepository(
        localDataSource: SharedPreferencesNotesDataSource
    ): NotesRepository {
        return NotesRepositoryImpl(localDataSource)
    }
} 