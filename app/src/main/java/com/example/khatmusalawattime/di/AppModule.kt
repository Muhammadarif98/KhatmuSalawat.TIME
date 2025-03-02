package com.example.khatmusalawattime.di

import android.content.Context
import androidx.room.Room
import com.example.khatmusalawattime.data.local.dao.CounterDao
import com.example.khatmusalawattime.data.local.database.AppDatabase
import com.example.khatmusalawattime.data.repository.ReminderRepositoryImpl
import com.example.khatmusalawattime.domain.repository.ReminderRepository
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
}