package com.example.khatmusalawattime.presentation.ui.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.khatmusalawattime.R

@Composable
fun AlarmScreen(
    viewModel: AlarmViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    // Colors
    val bgColor = Color(0xFFF5EFD9)
    val timerBgColor = Color(0xFFE6D7B5)
    val buttonBgColor = Color(0xFFF0E8C9)
    val textColor = Color(0xFF8B7E66)
    val accentColor = Color(0xFFD9CAA4)
    
    // States
    val selectedTime by viewModel.selectedTime.collectAsState()
    val formattedTime by viewModel.formattedTime.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    val isActive by viewModel.isActive.collectAsState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Лампы вверху экрана (исламские светильники/фонари)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(5) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lamp),
                        contentDescription = "Исламский светильник",
                        modifier = Modifier.size(48.dp),
                        tint = accentColor
                    )
                }
            }
            
            // Основной таймер
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(timerBgColor)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Горизонтальная линия вверху
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(2.dp)
                            .background(accentColor)
                            .padding(vertical = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Время таймера
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Получаем минуты и секунды из формата "MM:SS"
                            val parts = formattedTime.split(":")
                            val minutes = parts.getOrNull(0) ?: "00"
                            val seconds = parts.getOrNull(1) ?: "00"
                            
                            // Большой текст минут
                            Text(
                                text = minutes,
                                style = TextStyle(
                                    fontSize = 80.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            )
                            
                            // Двоеточие
                            Text(
                                text = ":",
                                style = TextStyle(
                                    fontSize = 80.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            )
                            
                            // Большой текст секунд
                            Text(
                                text = seconds,
                                style = TextStyle(
                                    fontSize = 80.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            )
                        }
                        
                        // Кнопка проверки звука
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 16.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                                .clickable { viewModel.testSound() }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_lock_silent_mode_off),
                                contentDescription = "Проверить звук",
                                tint = textColor
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Горизонтальная линия внизу
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(2.dp)
                            .background(accentColor)
                            .padding(vertical = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Кнопки выбора времени
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TimeButton(time = 1, selectedTime = selectedTime.toInt(), onClick = { viewModel.selectTime(1) })
                        TimeButton(time = 5, selectedTime = selectedTime.toInt(), onClick = { viewModel.selectTime(5) })
                        TimeButton(time = 10, selectedTime = selectedTime.toInt(), onClick = { viewModel.selectTime(10) })
                        TimeButton(time = 15, selectedTime = selectedTime.toInt(), onClick = { viewModel.selectTime(15) })
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Кнопки управления
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Кнопка назад
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, accentColor, RoundedCornerShape(8.dp))
                        .clickable { navController.navigateUp() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Назад",
                        tint = textColor
                    )
                }
                
                // Кнопка Play/Pause
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, accentColor, RoundedCornerShape(8.dp))
                        .clickable { viewModel.toggleTimerState() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val iconRes = if (timerState is TimerState.Running) {
                        R.drawable.ic_pause
                    } else {
                        R.drawable.ic_play
                    }
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (timerState is TimerState.Running) "Пауза" else "Старт",
                        tint = textColor
                    )
                }
                
                // Кнопка Reset
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, accentColor, RoundedCornerShape(8.dp))
                        .clickable { viewModel.resetTimer() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_reset),
                        contentDescription = "Сброс",
                        tint = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun TimeButton(
    time: Int,
    selectedTime: Int,
    onClick: () -> Unit
) {
    val isSelected = time == selectedTime
    val buttonColor = if (isSelected) Color(0xFFD9CAA4) else Color(0xFFF0E8C9)
    val textColor = Color(0xFF8B7E66)
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(buttonColor)
            .border(1.dp, Color(0xFFD9CAA4), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$time",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    AlarmScreen()
}