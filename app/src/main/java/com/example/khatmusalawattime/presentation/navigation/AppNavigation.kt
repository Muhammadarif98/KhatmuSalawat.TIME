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
        composable("home") {
            HomeScreen(
                onNavigateToAlarm = { navController.navigate("alarm") },
                onNavigateToCounter = { navController.navigate("counter") }
            )
        }
        composable("alarm") { AlarmScreen() }
        composable("counter") { CounterScreen() }
    }
}