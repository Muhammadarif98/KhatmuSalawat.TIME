package com.example.khatmusalawattime.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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

    // Определяем, должна ли отображаться нижняя навигация
    val shouldShowBottomNav = when (currentRoute) {
        Route.Alarm.path, Route.Counter.path -> false
        else -> true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavigationHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )

        if (shouldShowBottomNav) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .align(Alignment.BottomCenter)
            ) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
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
        startDestination = Route.Home.path,
        modifier = modifier
    ) {
        composable(Route.Home.path) {
            HomeScreen(
                onNavigateToAlarm = { navController.navigate(Route.Alarm.path) },
                onNavigateToCounter = { navController.navigate(Route.Counter.path) }
            )
        }

        composable(Route.Notes.path) {
            NotesScreen()
        }

        composable(Route.Settings.path) {
            SettingsScreen()
        }

        composable(Route.Alarm.path) {
            AlarmScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(Route.Counter.path) {
            CounterScreen(onNavigateBack = { navController.navigateUp() })
        }
    }
} 