package com.example.khatmusalawattime

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.khatmusalawattime.presentation.navigation.AppNavigation
import com.example.khatmusalawattime.presentation.notification.NotificationScheduler
import com.example.khatmusalawattime.presentation.theme.KhatmuSalawatTIMETheme
import com.example.khatmusalawattime.presentation.ui.home.HomeScreen
import com.example.khatmusalawattime.presentation.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KhatmuSalawatTIMETheme {
                AppNavigation()

                // Получаем ViewModel
                val viewModel: HomeViewModel = hiltViewModel()

                // Планируем уведомление после загрузки данных
                LaunchedEffect(Unit) {
                    viewModel.reminderData.collect { reminderData ->
                        if (reminderData != null) {
                            val notificationScheduler = NotificationScheduler(this@MainActivity)
                            notificationScheduler.scheduleDailyNotification(reminderData)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KhatmuSalawatTIMETheme {
        HomeScreen(
            viewModel = hiltViewModel(), // Используем реальный ViewModel (не будет работать в Preview)
            onNavigateToAlarm = {},
            onNavigateToCounter = {}
        )
    }
}