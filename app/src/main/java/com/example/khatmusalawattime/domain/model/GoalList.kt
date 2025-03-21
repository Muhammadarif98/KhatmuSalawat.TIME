package com.example.khatmusalawattime.domain.model

import java.util.Date

data class GoalList(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val tasks: List<Task>,
    val createdAt: Date,
    val updatedAt: Date
)