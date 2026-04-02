package com.example.khatmusalawattime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.khatmusalawattime.data.local.entity.CounterGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterGoalDao {

    @Query("SELECT * FROM counter_goals WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveGoals(): Flow<List<CounterGoalEntity>>

    @Query("SELECT * FROM counter_goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<CounterGoalEntity>>

    @Query("SELECT * FROM counter_goals WHERE id = :id")
    suspend fun getGoalById(id: String): CounterGoalEntity?

    @Query("SELECT * FROM counter_goals WHERE goalType = :type AND isCompleted = 0 LIMIT 1")
    suspend fun getActiveGoalByType(type: String): CounterGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: CounterGoalEntity)

    @Update
    suspend fun update(goal: CounterGoalEntity)

    @Query("UPDATE counter_goals SET currentCount = :count WHERE id = :id")
    suspend fun updateProgress(id: String, count: Int)

    @Query("UPDATE counter_goals SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompleted(id: String, completed: Boolean)

    @Query("DELETE FROM counter_goals WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM counter_goals")
    suspend fun deleteAll()

    @Query("UPDATE counter_goals SET currentCount = 0, isCompleted = 0 WHERE goalType = 'DAILY'")
    suspend fun resetDailyGoals()

    @Query("UPDATE counter_goals SET currentCount = 0, isCompleted = 0 WHERE id = :id")
    suspend fun resetProgress(id: String)
}
