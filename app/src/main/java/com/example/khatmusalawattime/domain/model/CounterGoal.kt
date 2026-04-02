package com.example.khatmusalawattime.domain.model

import java.util.Date
import java.util.UUID

/**
 * Тип цели счётчика
 */
enum class GoalType {
    /** Дневная цель — сбрасывается каждый день */
    DAILY,
    /** Цель на зикр — постоянная, пока не выполнена */
    ZIKR_TARGET
}

/**
 * Цель счётчика
 */
data class CounterGoal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val goalType: GoalType,
    val targetCount: Int,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val deadline: Date? = null,
    val createdAt: Date = Date()
) {
    val progress: Float
        get() = if (targetCount > 0) currentCount.toFloat() / targetCount else 0f

    val remainingCount: Int
        get() = (targetCount - currentCount).coerceAtLeast(0)
}
