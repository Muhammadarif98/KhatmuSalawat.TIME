package com.example.khatmusalawattime.presentation.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.khatmusalawattime.domain.model.ReminderData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.DayOfWeek

/**
 * Главный экран приложения.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAlarm: () -> Unit, // Колбэк для навигации на экран будильника
    onNavigateToCounter: () -> Unit // Колбэк для навигации на экран счетчика
) {
    // Загружаем данные при первом запуске экрана
    LaunchedEffect(Unit) {
        viewModel.loadReminderData()
    }

    // Состояние данных
    val reminderData by viewModel.reminderData.collectAsState()
    val error by viewModel.error.collectAsState()

    // Текущая дата
    val currentDate = getCurrentDate()

    // UI
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (error != null) {
                // Отображаем ошибку, если она есть
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            } else if (reminderData != null) {
                // Отображаем данные
                DisplayTime(reminderData!!, currentDate)
            } else {
                // Отображаем загрузку
                CircularProgressIndicator()
            }

            // Кнопки для навигации
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateToAlarm) {
                Text("Будильник")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onNavigateToCounter) {
                Text("Счетчик")
            }
        }
    }
}

/**
 * Функция для отображения времени в зависимости от дня недели.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DisplayTime(reminderData: ReminderData, currentDate: String) {
    val displayText = when {
        isThursday() -> "Салават: ${reminderData.datesKhunzakhSalawat[currentDate]}"
        isFriday() -> "Сегодня Шазалийский Хатму"
        else -> "Хатму: ${reminderData.datesKhunzakhHatmu[currentDate]}"
    }

    // Отображаем данные
    Text(
        text = "Сегодня $currentDate",
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = displayText,
        fontSize = 24.sp,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
    )
}

/**
 * Получить текущую дату в формате "день месяц".
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun getCurrentDate(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d MMMM") // Формат: "1 января"
    return currentDate.format(formatter)
}

/**
 * Проверить, является ли день четвергом.
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun isThursday(): Boolean {
    return LocalDate.now().dayOfWeek == DayOfWeek.THURSDAY
}

/**
 * Проверить, является ли день пятницей.
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun isFriday(): Boolean {
    return LocalDate.now().dayOfWeek == DayOfWeek.FRIDAY
}