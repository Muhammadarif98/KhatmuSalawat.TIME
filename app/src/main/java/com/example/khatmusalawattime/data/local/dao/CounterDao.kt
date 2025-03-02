package com.example.khatmusalawattime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.khatmusalawattime.data.local.entity.CounterEntity

@Dao
interface CounterDao {
    @Query("SELECT * FROM counter WHERE id = 0")
    suspend fun getCounter(): CounterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCounter(counter: CounterEntity)
}