package com.example.khatmusalawattime.domain.model

import java.util.Date

data class Task(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val linkedGoalId: String? = null
) {
    val isLinkedToGoal: Boolean
        get() = linkedGoalId != null
}