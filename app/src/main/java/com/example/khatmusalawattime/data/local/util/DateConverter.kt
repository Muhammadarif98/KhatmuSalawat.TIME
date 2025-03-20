package com.example.khatmusalawattime.data.local.util

import androidx.room.TypeConverter
import java.util.Date

/**
 * Date converter for Room database
 */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
} 