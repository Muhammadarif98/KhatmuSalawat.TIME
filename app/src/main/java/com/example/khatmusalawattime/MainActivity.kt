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
import com.example.khatmusalawattime.presentation.ui.theme.AppWrapper
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
                AppWrapper {
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
    }
    
    // Переопределяем метод onSaveInstanceState для лучшего сохранения состояния приложения
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: сохраняем состояние активити")
    }
    
    // Переопределяем метод onRestoreInstanceState для восстановления состояния
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState: восстанавливаем состояние активити")
    }
    
    // Переопределяем метод для обработки низкоуровневых событий памяти
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d(TAG, "onTrimMemory вызван с уровнем: $level")
        // Рекомендуется сохранять важное состояние при TRIM_MEMORY_UI_HIDDEN (уровень 20)
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Log.d(TAG, "Приложение ушло в фон, сохраняем состояние")
            // Здесь можно добавить дополнительное сохранение
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