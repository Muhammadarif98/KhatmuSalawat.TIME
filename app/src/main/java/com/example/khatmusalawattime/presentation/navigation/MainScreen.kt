package com.example.khatmusalawattime.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.khatmusalawattime.presentation.ui.alarm.AlarmScreen
import com.example.khatmusalawattime.presentation.ui.components.settings.SettingsPreferences
import com.example.khatmusalawattime.presentation.ui.counter.CounterModeScreen
import com.example.khatmusalawattime.presentation.ui.counter.CounterScreen
import com.example.khatmusalawattime.presentation.ui.counter.CounterViewModel
import com.example.khatmusalawattime.presentation.ui.counter.CustomCounterSetupScreen
import com.example.khatmusalawattime.presentation.ui.home.HomeScreen
import com.example.khatmusalawattime.presentation.ui.notes.NotesScreen
import com.example.khatmusalawattime.presentation.ui.onboarding.OnboardingScreen
import com.example.khatmusalawattime.presentation.ui.settings.SettingsScreen

/**
 * Главный экран приложения с навигацией
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()

    // Проверяем, показан ли онбординг
    val onboardingShown = remember { SettingsPreferences.loadOnboardingShown(context) }
    val startDestination = if (onboardingShown) Route.Home.path else Route.Onboarding.path

    // Получаем текущий маршрут
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Определяем, должна ли отображаться нижняя навигация
    val shouldShowBottomNav = when (currentRoute) {
        Route.Alarm.path,
        Route.Counter.path,
        Route.CounterMode.path,
        Route.CustomCounterSetup.path,
        Route.Onboarding.path -> false
        else -> true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavigationHost(
            navController = navController,
            startDestination = startDestination,
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
    startDestination: String = Route.Home.path,
    modifier: Modifier = Modifier
) {
    // Shared ViewModel для счётчика
    val counterViewModel: CounterViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Онбординг
        composable(Route.Onboarding.path) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Onboarding.path) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Home.path) {
            HomeScreen(
                onNavigateToAlarm = { navController.navigate(Route.Alarm.path) },
                onNavigateToCounter = { navController.navigate(Route.CounterMode.path) }
            )
        }

        composable(Route.Notes.path) {
            NotesScreen()
        }

        composable(Route.Settings.path) {
            SettingsScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Route.Onboarding.path)
                }
            )
        }

        composable(Route.Alarm.path) {
            AlarmScreen(onNavigateBack = { navController.navigateUp() })
        }

        // Экран выбора режима счётчика
        composable(Route.CounterMode.path) {
            CounterModeScreen(
                onNavigateBack = { navController.navigateUp() },
                onModeSelected = { mode ->
                    counterViewModel.setMode(mode)
                    navController.navigate(Route.Counter.path)
                },
                onCustomSetup = {
                    navController.navigate(Route.CustomCounterSetup.path)
                }
            )
        }

        // Экран настройки кастомного режима
        composable(Route.CustomCounterSetup.path) {
            val savedItems = remember { counterViewModel.loadCustomItems() }
            CustomCounterSetupScreen(
                onNavigateBack = { navController.navigateUp() },
                onStartCounter = { customMode ->
                    counterViewModel.setMode(customMode)
                    navController.navigate(Route.Counter.path) {
                        // Убираем CustomCounterSetup из стека, оставляем CounterMode
                        popUpTo(Route.CounterMode.path) { inclusive = false }
                    }
                },
                initialItems = savedItems
            )
        }

        // Основной экран счётчика
        composable(Route.Counter.path) {
            CounterScreen(
                viewModel = counterViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
