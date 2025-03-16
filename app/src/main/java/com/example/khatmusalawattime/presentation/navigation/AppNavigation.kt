package com.example.khatmusalawattime.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.khatmusalawattime.presentation.ui.alarm.AlarmScreen
import com.example.khatmusalawattime.presentation.ui.counter.CounterScreen
import com.example.khatmusalawattime.presentation.ui.home.HomeScreen
import com.example.khatmusalawattime.presentation.ui.notes.NotesScreen
import com.example.khatmusalawattime.presentation.ui.settings.SettingsScreen

/**
 * Навигация приложения.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Главный экран
        composable("home") {
            HomeScreen(
                navController = navController,
                onNavigateToAlarm = { navController.navigate("alarm") },
                onNavigateToCounter = { navController.navigate("counter") }
            )
        }
        
        // Экран таймера (будильника)
        composable("alarm") { 
            AlarmScreen(navController = navController) 
        }
        
        // Экран счетчика
        composable("counter") { 
            CounterScreen(navController = navController) 
        }
        
        // Экран заметок
        composable("notes") {
            NotesScreen(navController = navController)
        }
        
        // Экран настроек
        composable("settings") {
            SettingsScreen(navController = navController)
        }
    }
}