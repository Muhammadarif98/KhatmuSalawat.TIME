package com.example.khatmusalawattime.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.khatmusalawattime.presentation.ui.alarm.AlarmScreen
import com.example.khatmusalawattime.presentation.ui.counter.CounterScreen
import com.example.khatmusalawattime.presentation.ui.home.HomeScreen
import com.example.khatmusalawattime.presentation.ui.notes.NotesScreen
import com.example.khatmusalawattime.presentation.ui.settings.SettingsScreen

/**
 * Главный экран приложения с навигацией
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    // Получаем текущий маршрут
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Используем Box вместо Scaffold для размещения контента и навигации
    Box(modifier = Modifier.fillMaxSize()) {
        // Основной контент занимает весь экран
        NavigationHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
        
        // Навигационная панель поверх контента внизу экрана, без отступов
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        // Главный экран
        composable("home") {
            HomeScreen(
                navController = navController,
                onNavigateToAlarm = { navController.navigate("alarm") },
                onNavigateToCounter = { navController.navigate("counter") }
            )
        }
        
        // Экран заметок
        composable("notes") {
            NotesScreen(navController = navController)
        }
        
        // Экран настроек
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        
        // Экран таймера (будильника)
        composable("alarm") {
            AlarmScreen(navController = navController)
        }
        
        // Экран счетчика
        composable("counter") {
            CounterScreen(navController = navController)
        }
    }
} 