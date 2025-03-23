package com.example.khatmusalawattime.presentation.ui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.khatmusalawattime.R
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
                painter = painterResource(id = R.drawable.back),
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
                DayOfWeek.THURSDAY -> "Салават: ${reminderData?.datesKhunzakhSalawat?.get(currentDate) ?: "Загрузка..."}"
                DayOfWeek.FRIDAY -> "Сегодня Шазалийский Хатму"
                else -> "Хатму: ${reminderData?.datesKhunzakhHatmu?.get(currentDate) ?: "Загрузка..."}"
            }
            
            Text(
                text = displayText,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Кнопки навигации - размещены по бокам внизу экрана
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            // Кнопка таймера (слева)
            Button(
                onClick = onNavigateToAlarm,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .width(150.dp)
                    .height(60.dp)
            ) {
                Text("Таймер", fontSize = 18.sp)
            }
            
            // Кнопка счетчика (справа)
            Button(
                onClick = onNavigateToCounter,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(150.dp)
                    .height(60.dp)
            ) {
                Text("Счетчик", fontSize = 18.sp)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}