package com.example.khatmusalawattime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.khatmusalawattime.data.local.entity.CounterEntity
import com.example.khatmusalawattime.data.local.dao.CounterDao

@Database(entities = [CounterEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
}