package com.example.khatmusalawattime

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.khatmusalawattime.domain.model.ReminderData
import com.example.khatmusalawattime.presentation.navigation.AppNavigation
import com.example.khatmusalawattime.presentation.notification.scheduleDailyNotification
import com.example.khatmusalawattime.presentation.theme.KhatmuSalawatTIMETheme
import com.example.khatmusalawattime.presentation.ui.home.HomeScreen
import com.example.khatmusalawattime.presentation.ui.home.HomeViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // Запрос разрешения на уведомления
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Разрешение на уведомления получено")
        } else {
            Log.w(TAG, "Разрешение на уведомления не получено")
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Запрашиваем разрешение на уведомления
        requestNotificationPermission()

        setContent {
            KhatmuSalawatTIMETheme {
                AppNavigation()

                // Получаем ViewModel
                val viewModel: HomeViewModel = hiltViewModel()

                // Планируем уведомление после загрузки данных
                LaunchedEffect(Unit) {
                    // Чтение данных из JSON
                    val reminderData = readReminderDataFromJson(this@MainActivity)
                    scheduleDailyNotification(this@MainActivity, reminderData, getCurrentDate())
                }
            }
        }
    }
    
    private fun requestNotificationPermission() {
        // Для Android 13 (API level 33) и выше требуется явное разрешение
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Проверяем наличие разрешения
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Запрашиваем разрешение, если оно не выдано
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.d(TAG, "Разрешение на уведомления уже предоставлено")
            }
        } else {
            Log.d(TAG, "Для этой версии Android не требуется явное разрешение")
        }
    }
}


fun readReminderDataFromJson(context: Context): ReminderData {
    val inputStream: InputStream = context.resources.openRawResource(R.raw.reminder_data)
    val jsonString = inputStream.bufferedReader().use { it.readText() }
    return Gson().fromJson(jsonString, ReminderData::class.java)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getCurrentDate(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d MMMM") // Формат: "1 января"
    return currentDate.format(formatter)
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