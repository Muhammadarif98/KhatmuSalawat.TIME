package com.example.khatmusalawattime.presentation.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTimePicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    label: String,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFF1E4D1),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = textColor.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFF9EBBD),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFF4DBAD),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 4.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка уменьшения значения
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            val newValue = if (value <= range.first) range.last else value - 1
                            onValueChange(newValue)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "−",
                        color = textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Текущее значение
                Text(
                    text = value.toString().padStart(2, '0'),
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                // Кнопка увеличения значения
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            val newValue = if (value >= range.last) range.first else value + 1
                            onValueChange(newValue)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    selectedHour: Int,
    selectedMinute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    textColor: Color,
    borderGradient: Brush,
    cardGradient: Brush
) {
    if (showDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
            androidx.compose.material3.Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(8.dp)
                    .border(
                        width = 6.dp,
                        brush = borderGradient,
                        shape = RoundedCornerShape(24.dp)
                    ),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardGradient)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Выберите время",
                            color = textColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        // Создаем изменяемые состояния
                        var hour by remember { mutableStateOf(selectedHour) }
                        var minute by remember { mutableStateOf(selectedMinute) }
                        
                        // Селектор времени с двумя колонками для часов и минут
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Селектор часов
                            CustomTimePicker(
                                value = hour,
                                range = 0..23,
                                onValueChange = { hour = it },
                                label = "Час",
                                textColor = textColor
                            )
                            
                            Text(
                                text = ":",
                                color = textColor,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            
                            // Селектор минут
                            CustomTimePicker(
                                value = minute,
                                range = 0..59,
                                onValueChange = { minute = it },
                                label = "Мин",
                                textColor = textColor
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Кнопки действий
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onDismiss() }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Отмена",
                                    color = textColor.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                            }
                            
                            Spacer(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(20.dp)
                                    .background(textColor.copy(alpha = 0.2f))
                            )
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { 
                                        onTimeSelected(hour, minute)
                                        onDismiss()
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Готово",
                                    color = textColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 