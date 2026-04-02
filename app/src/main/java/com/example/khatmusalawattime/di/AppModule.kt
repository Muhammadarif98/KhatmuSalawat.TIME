package com.example.khatmusalawattime.di

import android.content.Context
import androidx.room.Room
import com.example.khatmusalawattime.data.backup.BackupManager
import com.example.khatmusalawattime.data.local.dao.CounterDao
import com.example.khatmusalawattime.data.local.dao.CounterGoalDao
import com.example.khatmusalawattime.data.local.dao.CounterHistoryDao
import com.example.khatmusalawattime.data.local.dao.NoteDao
import com.example.khatmusalawattime.data.local.database.AppDatabase
import com.example.khatmusalawattime.data.repository.CounterGoalRepositoryImpl
import com.example.khatmusalawattime.data.repository.CounterHistoryRepositoryImpl
import com.example.khatmusalawattime.data.repository.CounterRepositoryImpl
import com.example.khatmusalawattime.data.repository.ReminderRepositoryImpl
import com.example.khatmusalawattime.domain.repository.CounterGoalRepository
import com.example.khatmusalawattime.domain.repository.CounterHistoryRepository
import com.example.khatmusalawattime.domain.repository.CounterRepository
import com.example.khatmusalawattime.domain.repository.ReminderRepository
import com.example.khatmusalawattime.domain.usecase.counter.CounterUseCases
import com.example.khatmusalawattime.domain.usecase.counter.CreateCounterGoalUseCase
import com.example.khatmusalawattime.domain.usecase.counter.DeleteCounterGoalUseCase
import com.example.khatmusalawattime.domain.usecase.counter.GetActiveGoalsUseCase
import com.example.khatmusalawattime.domain.usecase.counter.GetCounterStatsUseCase
import com.example.khatmusalawattime.domain.usecase.counter.GetCounterUseCase
import com.example.khatmusalawattime.domain.usecase.counter.RecordCounterSessionUseCase
import com.example.khatmusalawattime.domain.usecase.counter.ResetGoalProgressUseCase
import com.example.khatmusalawattime.domain.usecase.counter.UpdateCounterUseCase
import com.example.khatmusalawattime.domain.usecase.counter.UpdateGoalProgressUseCase
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
        )
        .addMigrations(AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4)
        .fallbackToDestructiveMigration()
        .build()
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

    @Provides
    @Singleton
    fun provideCounterHistoryDao(database: AppDatabase): CounterHistoryDao {
        return database.counterHistoryDao()
    }

    @Provides
    @Singleton
    fun provideCounterGoalDao(database: AppDatabase): CounterGoalDao {
        return database.counterGoalDao()
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
    
    // Репозиторий для счетчика
    @Provides
    @Singleton
    fun provideCounterRepository(counterDao: CounterDao): CounterRepository {
        return CounterRepositoryImpl(counterDao)
    }

    // Репозиторий для истории счётчика
    @Provides
    @Singleton
    fun provideCounterHistoryRepository(
        historyDao: CounterHistoryDao,
        gson: Gson
    ): CounterHistoryRepository {
        return CounterHistoryRepositoryImpl(historyDao, gson)
    }

    // Репозиторий для целей счётчика
    @Provides
    @Singleton
    fun provideCounterGoalRepository(goalDao: CounterGoalDao): CounterGoalRepository {
        return CounterGoalRepositoryImpl(goalDao)
    }

    // Use cases для счетчика
    @Provides
    @Singleton
    fun provideCounterUseCases(
        counterRepository: CounterRepository,
        historyRepository: CounterHistoryRepository,
        goalRepository: CounterGoalRepository
    ): CounterUseCases {
        return CounterUseCases(
            getCounter = GetCounterUseCase(counterRepository),
            updateCounter = UpdateCounterUseCase(counterRepository),
            recordSession = RecordCounterSessionUseCase(historyRepository),
            getStats = GetCounterStatsUseCase(historyRepository),
            createGoal = CreateCounterGoalUseCase(goalRepository),
            updateGoalProgress = UpdateGoalProgressUseCase(goalRepository),
            getActiveGoals = GetActiveGoalsUseCase(goalRepository),
            deleteGoal = DeleteCounterGoalUseCase(goalRepository),
            resetGoalProgress = ResetGoalProgressUseCase(goalRepository)
        )
    }

    @Provides
    @Singleton
    fun provideBackupManager(
        @ApplicationContext context: Context,
        noteDao: NoteDao,
        counterDao: CounterDao,
        counterHistoryDao: CounterHistoryDao,
        counterGoalDao: CounterGoalDao,
        gson: Gson
    ): BackupManager {
        return BackupManager(context, noteDao, counterDao, counterHistoryDao, counterGoalDao, gson)
    }
}