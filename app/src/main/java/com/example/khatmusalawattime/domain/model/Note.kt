package com.example.khatmusalawattime.domain.model

import java.util.Date

/**
 * Domain model representing a note list
 */
data class NoteList(
    val id: String,
    val title: String,
    val createdAt: Date,
    val updatedAt: Date,
    val completed: Boolean = false
)

/**
 * Domain model representing a note
 */
data class Note(
    val id: String,
    val noteListId: String,
    val title: String,
    val content: String = "",
    val createdAt: Date,
    val updatedAt: Date, 
    val completed: Boolean = false
) 