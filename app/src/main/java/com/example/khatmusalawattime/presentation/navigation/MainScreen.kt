package com.example.khatmusalawattime.presentation.navigation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
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
 * Главный экран приложения с простой навигацией на основе состояния.
 * Мгновенные переходы без анимаций — быстрее чем NavHost.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val context = LocalContext.current

    // Проверяем, показан ли онбординг
    val onboardingShown = remember { SettingsPreferences.loadOnboardingShown(context) }
    val startScreen = if (onboardingShown) Screen.Home else Screen.Onboarding

    // Навигатор
    val navigator = rememberAppNavigator(startScreen)

    // Shared ViewModel для счётчика — создаём один раз
    val counterViewModel: CounterViewModel = hiltViewModel()

    // Обработка системной кнопки "Назад"
    BackHandler(enabled = navigator.canGoBack()) {
        navigator.navigateBack()
    }

    // Определяем, должна ли отображаться нижняя навигация
    val shouldShowBottomNav = when (navigator.currentScreen) {
        Screen.Alarm,
        Screen.Counter,
        Screen.CounterMode,
        Screen.CustomCounterSetup,
        Screen.Onboarding -> false
        else -> true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Контент экрана — мгновенное переключение
        ScreenContent(
            screen = navigator.currentScreen,
            navigator = navigator,
            counterViewModel = counterViewModel
        )

        // Нижняя навигация
        if (shouldShowBottomNav) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .align(Alignment.BottomCenter)
            ) {
                SimpleBottomNavigationBar(
                    currentScreen = navigator.currentScreen,
                    onScreenSelected = { screen ->
                        navigator.navigateTo(screen)
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ScreenContent(
    screen: Screen,
    navigator: AppNavigator,
    counterViewModel: CounterViewModel
) {
    when (screen) {
        Screen.Onboarding -> {
            OnboardingScreen(
                onFinish = {
                    navigator.navigateToAndClearStack(Screen.Home)
                }
            )
        }

        Screen.Home -> {
            HomeScreen(
                onNavigateToAlarm = { navigator.navigateTo(Screen.Alarm) },
                onNavigateToCounter = { navigator.navigateTo(Screen.CounterMode) }
            )
        }

        Screen.Notes -> {
            NotesScreen()
        }

        Screen.Settings -> {
            SettingsScreen(
                onNavigateToOnboarding = {
                    navigator.navigateTo(Screen.Onboarding)
                }
            )
        }

        Screen.Alarm -> {
            AlarmScreen(
                onNavigateBack = { navigator.navigateBack() }
            )
        }

        Screen.CounterMode -> {
            val stats by counterViewModel.stats.collectAsState()
            val statsExpanded by counterViewModel.statsExpanded.collectAsState()

            CounterModeScreen(
                onNavigateBack = { navigator.navigateBack() },
                onModeSelected = { mode ->
                    counterViewModel.setMode(mode)
                    navigator.navigateTo(Screen.Counter)
                },
                onCustomSetup = {
                    navigator.navigateTo(Screen.CustomCounterSetup)
                },
                stats = stats,
                statsExpanded = statsExpanded,
                onToggleStatsExpanded = { counterViewModel.toggleStatsExpanded() }
            )
        }

        Screen.CustomCounterSetup -> {
            val savedItems = remember { counterViewModel.loadCustomItems() }
            CustomCounterSetupScreen(
                onNavigateBack = { navigator.navigateBack() },
                onStartCounter = { customMode ->
                    counterViewModel.setMode(customMode)
                    // Переходим к Counter и убираем CustomCounterSetup из стека
                    navigator.popUpTo(Screen.CounterMode, inclusive = false)
                    navigator.navigateTo(Screen.Counter)
                },
                initialItems = savedItems
            )
        }

        Screen.Counter -> {
            CounterScreen(
                viewModel = counterViewModel,
                onNavigateBack = { navigator.navigateBack() }
            )
        }
    }
}
