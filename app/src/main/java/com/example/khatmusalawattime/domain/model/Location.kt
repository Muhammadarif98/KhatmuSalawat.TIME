package com.example.khatmusalawattime.domain.model

/**
 * Доступные локации для расписания Хатму/Салават.
 */
enum class Location(val displayName: String) {
    KHUNZAKH("Хунзах"),
    CHIRKEI("Чиркей");

    companion object {
        fun fromName(name: String): Location {
            return entries.find { it.name == name } ?: KHUNZAKH
        }
    }
}
