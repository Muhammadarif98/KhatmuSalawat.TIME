package com.example.khatmusalawattime.presentation.ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * AppWrapper - компонент-обертка для всего приложения,
 * который применяет настройки прозрачного статус-бара ко
 * всем экранам.
 */
@Composable
fun AppWrapper(content: @Composable () -> Unit) {
    // Получаем доступ к окну активности
    val view = LocalView.current
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        if (window != null) {
            // Делаем фон статус-бара прозрачным
            window.statusBarColor = Color.Transparent.toArgb()
            // Разрешаем контенту заходить под статус-бар
            WindowCompat.setDecorFitsSystemWindows(window, false)
            // Иконки статус-бара тёмные (для светлого фона)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }

        onDispose {
            // Ничего не делаем при выходе, так как настройки должны сохраняться
        }
    }

    // Вызываем основной контент
    content()
} 