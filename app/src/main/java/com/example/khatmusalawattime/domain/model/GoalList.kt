package com.example.khatmusalawattime.domain.model

data class GoalList(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val tasks: List<Task> = emptyList()
)

data class Task(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
) 