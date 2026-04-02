package com.example.khatmusalawattime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.khatmusalawattime.data.local.converter.TasksConverter
import com.example.khatmusalawattime.data.local.dao.CounterDao
import com.example.khatmusalawattime.data.local.dao.CounterGoalDao
import com.example.khatmusalawattime.data.local.dao.CounterHistoryDao
import com.example.khatmusalawattime.data.local.dao.NoteDao
import com.example.khatmusalawattime.data.local.entity.CounterEntity
import com.example.khatmusalawattime.data.local.entity.CounterGoalEntity
import com.example.khatmusalawattime.data.local.entity.CounterHistoryEntity
import com.example.khatmusalawattime.data.local.entity.NoteEntity
import com.example.khatmusalawattime.data.local.entity.NoteListEntity
import com.example.khatmusalawattime.data.local.util.DateConverter

@Database(
    entities = [
        CounterEntity::class,
        NoteListEntity::class,
        NoteEntity::class,
        CounterHistoryEntity::class,
        CounterGoalEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(DateConverter::class, TasksConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
    abstract fun noteDao(): NoteDao
    abstract fun counterHistoryDao(): CounterHistoryDao
    abstract fun counterGoalDao(): CounterGoalDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Удаляем колонки linkedTaskId и linkedListId из counter_goals
                // SQLite не поддерживает DROP COLUMN напрямую, пересоздаём таблицу
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS counter_goals_new (
                        id TEXT PRIMARY KEY NOT NULL,
                        title TEXT NOT NULL,
                        goalType TEXT NOT NULL,
                        targetCount INTEGER NOT NULL,
                        currentCount INTEGER NOT NULL,
                        isCompleted INTEGER NOT NULL,
                        deadline INTEGER,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO counter_goals_new (id, title, goalType, targetCount, currentCount, isCompleted, deadline, createdAt)
                    SELECT id, title, goalType, targetCount, currentCount, isCompleted, deadline, createdAt FROM counter_goals
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE counter_goals")
                db.execSQL("ALTER TABLE counter_goals_new RENAME TO counter_goals")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Создаём таблицу истории счётчика
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS counter_history (
                        id TEXT PRIMARY KEY NOT NULL,
                        date INTEGER NOT NULL,
                        totalCount INTEGER NOT NULL,
                        sessionsCount INTEGER NOT NULL,
                        modes TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_counter_history_date ON counter_history(date)")

                // Создаём таблицу целей счётчика
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS counter_goals (
                        id TEXT PRIMARY KEY NOT NULL,
                        title TEXT NOT NULL,
                        goalType TEXT NOT NULL,
                        targetCount INTEGER NOT NULL,
                        currentCount INTEGER NOT NULL,
                        linkedTaskId TEXT,
                        linkedListId TEXT,
                        isCompleted INTEGER NOT NULL,
                        deadline INTEGER,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                // Добавляем колонку linkedGoalId в таблицу notes
                db.execSQL("ALTER TABLE notes ADD COLUMN linkedGoalId TEXT")
            }
        }
    }
}