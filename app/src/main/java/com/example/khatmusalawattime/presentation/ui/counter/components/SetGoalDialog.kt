package com.example.khatmusalawattime.presentation.ui.counter.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.domain.model.GoalType

// Бежевая тема для диалога (в стиле приложения)
private val accentColor = Color(0xFF7C5F23)  // Коричневый акцент
private val dialogBg = Color(0xFFF1E4D1)     // Бежевый фон
private val cardBg = Color(0xFFFAF7F2)       // Светлый карточный фон
private val textPrimary = Color(0xFF604D2E)  // Тёмно-коричневый текст
private val textSecondary = Color(0xFF8B7355) // Приглушённый коричневый
private val inputBg = Color.White            // Белый для инпутов

@Composable
fun SetGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, targetCount: Int, goalType: GoalType) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetCount by remember { mutableStateOf("1000") }
    var goalType by remember { mutableStateOf(GoalType.ZIKR_TARGET) }

    val russoOneFamily = FontFamily(Font(R.font.russo_one_regular))

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(dialogBg)
                .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Заголовок
                Text(
                    text = "Новая цель",
                    fontFamily = russoOneFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Название цели
                Text(
                    text = "Название",
                    color = textSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                StyledTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Например: 1000 зикров"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Количество
                Text(
                    text = "Количество",
                    color = textSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                StyledTextField(
                    value = targetCount,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            targetCount = newValue
                        }
                    },
                    placeholder = "1000",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Тип цели
                Text(
                    text = "Тип цели",
                    color = textSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Дневная
                GoalTypeOption(
                    title = "Дневная",
                    description = "Сбрасывается каждый день",
                    isSelected = goalType == GoalType.DAILY,
                    onClick = { goalType = GoalType.DAILY }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // На зикр
                GoalTypeOption(
                    title = "Постоянная",
                    description = "Накопительная цель",
                    isSelected = goalType == GoalType.ZIKR_TARGET,
                    onClick = { goalType = GoalType.ZIKR_TARGET }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Отмена
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .clickable { onDismiss() }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Отмена",
                            color = textSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Создать
                    val isValid = title.isNotBlank() && (targetCount.toIntOrNull() ?: 0) > 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isValid) accentColor else accentColor.copy(alpha = 0.3f))
                            .clickable(enabled = isValid) {
                                val count = targetCount.toIntOrNull() ?: 0
                                if (title.isNotBlank() && count > 0) {
                                    onConfirm(title, count, goalType)
                                }
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Создать",
                            color = if (isValid) Color.White else textSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            color = textPrimary,
            fontSize = 14.sp
        ),
        cursorBrush = SolidColor(accentColor),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(inputBg)
                    .padding(14.dp)
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = textSecondary,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun GoalTypeOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.15f) else cardBg)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) accentColor else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кастомный radio
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, if (isSelected) accentColor else textSecondary, CircleShape)
                .padding(4.dp)
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                color = textSecondary,
                fontSize = 11.sp
            )
        }
    }
}
