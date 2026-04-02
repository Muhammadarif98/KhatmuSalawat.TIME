package com.example.khatmusalawattime

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.khatmusalawattime.domain.model.ReminderData
import com.example.khatmusalawattime.presentation.navigation.MainScreen
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

        // Включаем edge-to-edge для полноэкранного отображения
        enableEdgeToEdge()

        // Запрашиваем разрешения при первом запуске
        requestAllPermissions()

        setContent {
            KhatmuSalawatTIMETheme {
                AppWrapper {
                    MainScreen()

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
    
    private fun requestAllPermissions() {
        // Запрашиваем разрешение на уведомления
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(permission)
            }
        }
        
        // Запрашиваем разрешение на точные будильники для Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                try {
                    val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, packageName)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при запросе разрешения на точные будильники: ${e.message}")
                }
            }
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