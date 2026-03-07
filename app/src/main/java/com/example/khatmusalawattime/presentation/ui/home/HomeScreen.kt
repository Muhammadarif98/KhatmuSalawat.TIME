package com.example.khatmusalawattime.presentation.ui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
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
    onNavigateToAlarm: () -> Unit = {},
    onNavigateToCounter: () -> Unit = {}
) {
    // Загружаем данные при открытии экрана
    LaunchedEffect(Unit) {
        viewModel.loadReminderData()
    }
    
    // Состояние
    val reminderData by viewModel.reminderData.collectAsState()
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        InfoDialog(onDismiss = { showInfoDialog = false })
    }

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

        // Кнопка инфо
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0x40F1E4D1))
                .clickable { showInfoDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Информация",
                tint = Color(0xFF604D2E),
                modifier = Modifier.size(20.dp)
            )
        }

        // Кнопки навигации - размещены по бокам внизу экрана
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(top = 20.dp, bottom = 115.dp, start = 24.dp, end = 24.dp)
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

@Composable
private fun InfoDialog(onDismiss: () -> Unit) {
    val borderGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF4DBAD),
            Color(0xFFFDFBCC),
            Color(0xFFF4DBAD)
        )
    )
    val textColor = Color(0xFF604D2E)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 4.dp,
                    brush = borderGradient,
                    shape = RoundedCornerShape(28.dp)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFFF1E4D1))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Хатму/Салават",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Приложение показывает время коллективного чтения Хатму и Салавата для жителей Хунзаха.\n\nВ четверг — время Салавата\nВ пятницу — Шазалийский Хатму\nВ остальные дни — время Хатму",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Serif,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x33604D2E))
                        .clickable { onDismiss() }
                        .padding(horizontal = 32.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Понятно",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = textColor
                    )
                }
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