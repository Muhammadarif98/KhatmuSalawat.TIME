package com.example.khatmusalawattime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.khatmusalawattime.domain.model.CounterGoal
import com.example.khatmusalawattime.domain.model.GoalType
import java.util.Date

@Entity(tableName = "counter_goals")
data class CounterGoalEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val goalType: String, // DAILY or ZIKR_TARGET
    val targetCount: Int,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val deadline: Long? = null,
    val createdAt: Long
) {
    fun toCounterGoal(): CounterGoal {
        return CounterGoal(
            id = id,
            title = title,
            goalType = GoalType.valueOf(goalType),
            targetCount = targetCount,
            currentCount = currentCount,
            isCompleted = isCompleted,
            deadline = deadline?.let { Date(it) },
            createdAt = Date(createdAt)
        )
    }

    companion object {
        fun fromCounterGoal(goal: CounterGoal): CounterGoalEntity {
            return CounterGoalEntity(
                id = goal.id,
                title = goal.title,
                goalType = goal.goalType.name,
                targetCount = goal.targetCount,
                currentCount = goal.currentCount,
                isCompleted = goal.isCompleted,
                deadline = goal.deadline?.time,
                createdAt = goal.createdAt.time
            )
        }
    }
}
