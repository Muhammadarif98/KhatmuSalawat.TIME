package com.example.khatmusalawattime.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Простая навигация на основе состояния — мгновенные переходы без анимаций.
 * Быстрее чем NavHost, т.к. нет overhead библиотеки навигации.
 */

sealed interface Screen {
    data object Home : Screen
    data object Notes : Screen
    data object Settings : Screen
    data object Alarm : Screen
    data object CounterMode : Screen
    data object Counter : Screen
    data object CustomCounterSetup : Screen
    data object Onboarding : Screen
}

@Stable
class AppNavigator {
    var currentScreen by mutableStateOf<Screen>(Screen.Home)
        private set

    // Стек для навигации назад
    private val backStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        if (currentScreen != screen) {
            backStack.add(currentScreen)
            currentScreen = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeLast()
            true
        } else {
            false
        }
    }

    fun navigateToAndClearStack(screen: Screen) {
        backStack.clear()
        currentScreen = screen
    }

    fun popUpTo(target: Screen, inclusive: Boolean = false) {
        val index = backStack.indexOfLast { it == target }
        if (index >= 0) {
            val removeCount = backStack.size - index - (if (inclusive) 0 else 1)
            repeat(removeCount) {
                if (backStack.isNotEmpty()) backStack.removeLast()
            }
        }
        if (inclusive && backStack.isNotEmpty() && backStack.last() == target) {
            backStack.removeLast()
        }
    }

    fun canGoBack(): Boolean = backStack.isNotEmpty()
}

@Composable
fun rememberAppNavigator(startScreen: Screen = Screen.Home): AppNavigator {
    return remember {
        AppNavigator().apply {
            navigateToAndClearStack(startScreen)
        }
    }
}
