package com.example.khatmusalawattime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.khatmusalawattime.data.local.entity.CounterHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterHistoryDao {

    @Query("SELECT * FROM counter_history ORDER BY date DESC")
    fun getAllHistory(): Flow<List<CounterHistoryEntity>>

    @Query("SELECT * FROM counter_history WHERE date = :date LIMIT 1")
    suspend fun getHistoryByDate(date: Long): CounterHistoryEntity?

    @Query("SELECT * FROM counter_history WHERE date >= :fromDate ORDER BY date DESC")
    suspend fun getHistoryFromDate(fromDate: Long): List<CounterHistoryEntity>

    @Query("SELECT * FROM counter_history ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentHistory(limit: Int): List<CounterHistoryEntity>

    @Query("SELECT * FROM counter_history ORDER BY totalCount DESC LIMIT 1")
    suspend fun getBestDay(): CounterHistoryEntity?

    @Query("SELECT SUM(totalCount) FROM counter_history")
    suspend fun getTotalAllTime(): Int?

    @Query("SELECT COUNT(*) FROM counter_history WHERE totalCount > 0")
    suspend fun getActiveDaysCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: CounterHistoryEntity)

    @Update
    suspend fun update(history: CounterHistoryEntity)

    @Query("DELETE FROM counter_history WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM counter_history")
    suspend fun deleteAll()
}
