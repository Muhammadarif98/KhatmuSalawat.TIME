package com.example.khatmusalawattime.domain.model

import java.util.Date
import java.util.UUID

/**
 * Запись истории подсчёта за день
 */
data class CounterHistory(
    val id: String = UUID.randomUUID().toString(),
    val date: Date,
    val totalCount: Int,
    val sessionsCount: Int = 1,
    val modes: List<CounterModeType> = emptyList(),
    val createdAt: Date = Date()
)

/**
 * Агрегированная статистика счётчика
 */
data class CounterStats(
    /** Общее количество за всё время */
    val totalAllTime: Int = 0,
    /** Среднее количество в день */
    val averagePerDay: Float = 0f,
    /** Текущий streak (дней подряд) */
    val currentStreak: Int = 0,
    /** Лучший streak */
    val bestStreak: Int = 0,
    /** Лучший день */
    val bestDay: CounterHistory? = null,
    /** История за последние 7 дней */
    val last7Days: List<CounterHistory> = emptyList(),
    /** Общее количество дней с активностью */
    val totalActiveDays: Int = 0
)
