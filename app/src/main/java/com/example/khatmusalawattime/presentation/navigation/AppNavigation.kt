package com.example.khatmusalawattime.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

/**
 * Навигация приложения.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    // Используем MainScreen, который содержит SmoothBottomNavigation
    MainScreen()
}