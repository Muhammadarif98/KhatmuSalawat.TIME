package com.example.khatmusalawattime.presentation.ui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.presentation.ui.components.AnimateContent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Главный экран приложения.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
    onNavigateToAlarm: () -> Unit = { navController.navigate("alarm") },
    onNavigateToCounter: () -> Unit = { navController.navigate("counter") }
) {
    // Загружаем данные при открытии экрана
    LaunchedEffect(Unit) {
        viewModel.loadReminderData()
    }
    
    // Состояние
    val reminderData by viewModel.reminderData.collectAsState()
    
    // Основной контейнер
    Box(
        modifier = Modifier
            .paint(
                painter = painterResource(id = R.drawable.newback),
                contentScale = ContentScale.FillBounds
            )
            .fillMaxSize()
    ) {
        // Информация о хатму/салавате - 50dp от верхнего края
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 130.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Текущая дата
            val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM"))
            
            // Определяем текст для отображения в зависимости от дня недели
            val displayText = when (LocalDate.now().dayOfWeek) {
                DayOfWeek.THURSDAY -> "${reminderData?.datesKhunzakhSalawat?.get(currentDate) ?: "Загрузка..."}"
                DayOfWeek.FRIDAY -> "Шазалийский"
                else -> "${reminderData?.datesKhunzakhHatmu?.get(currentDate) ?: "Загрузка..."}"
            }
            val displayLongText = when (LocalDate.now().dayOfWeek) {
                DayOfWeek.THURSDAY -> "Салават в ${reminderData?.datesKhunzakhSalawat?.get(currentDate) ?: "Загрузка..."}"
                DayOfWeek.FRIDAY -> "Шазалийский Хатму"
                else -> "Хатму в ${reminderData?.datesKhunzakhHatmu?.get(currentDate) ?: "Загрузка..."}"
            }
            AnimateContent(shortText = "$displayText", longText = " Сегодня $displayLongText")
        }
        
        // Кнопки навигации - размещены по бокам внизу экрана
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(top = 20.dp, bottom = 95.dp, start = 24.dp, end = 24.dp)
        ) {
            // Кликабельная картинка для таймера (слева)
            Image(
                painter = painterResource(id = R.drawable.timerbtn),
                contentDescription = "Таймер",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .width(150.dp)
                    .height(130.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() }, // Контроль состояний
                        indication = null, // Отключаем визуальные эффекты
                        onClick = { onNavigateToAlarm() }
                    )
            )

            // Кликабельная картинка для счетчика (справа)
            Image(
                painter = painterResource(id = R.drawable.counterbtn),
                contentDescription = "Счетчик",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(150.dp)
                    .height(130.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() }, // Контроль состояний
                        indication = null, // Отключаем визуальные эффекты
                        onClick = { onNavigateToCounter() }
                    )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}