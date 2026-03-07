package com.example.khatmusalawattime.presentation.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Notes : Route("notes")
    data object Settings : Route("settings")
    data object Alarm : Route("alarm")
    data object Counter : Route("counter")
}
