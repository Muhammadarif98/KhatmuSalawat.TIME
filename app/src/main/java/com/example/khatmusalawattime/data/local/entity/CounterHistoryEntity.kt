package com.example.khatmusalawattime.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.khatmusalawattime.domain.model.CounterHistory
import com.example.khatmusalawattime.domain.model.CounterModeType
import java.util.Date

@Entity(
    tableName = "counter_history",
    indices = [Index("date")]
)
data class CounterHistoryEntity(
    @PrimaryKey
    val id: String,
    val date: Long,
    val totalCount: Int,
    val sessionsCount: Int = 1,
    val modes: String = "[]", // JSON array of CounterModeType
    val createdAt: Long
) {
    fun toCounterHistory(modesList: List<CounterModeType>): CounterHistory {
        return CounterHistory(
            id = id,
            date = Date(date),
            totalCount = totalCount,
            sessionsCount = sessionsCount,
            modes = modesList,
            createdAt = Date(createdAt)
        )
    }

    companion object {
        fun fromCounterHistory(history: CounterHistory, modesJson: String): CounterHistoryEntity {
            return CounterHistoryEntity(
                id = history.id,
                date = history.date.time,
                totalCount = history.totalCount,
                sessionsCount = history.sessionsCount,
                modes = modesJson,
                createdAt = history.createdAt.time
            )
        }
    }
}
