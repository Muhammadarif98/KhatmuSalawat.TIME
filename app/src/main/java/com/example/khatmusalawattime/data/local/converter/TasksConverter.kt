package com.example.khatmusalawattime.data.local.converter

import androidx.room.TypeConverter
import com.example.khatmusalawattime.data.local.entity.NoteEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TasksConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromNoteEntityList(value: List<NoteEntity>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toNoteEntityList(value: String): List<NoteEntity> {
        val listType: Type = object : TypeToken<List<NoteEntity>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}

