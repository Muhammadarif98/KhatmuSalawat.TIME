package com.example.khatmusalawattime.presentation.ui.settings

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.khatmusalawattime.R

@Composable
fun SettingsScreen(
    navController: NavController = rememberNavController()
) {
    // Состояния для настроек
    var isDarkMode by remember { mutableStateOf(false) }
    var selectedColorTheme by remember { mutableStateOf(0) } // 0 - коричневый, 1 - синий, 2 - красный, 3 - оранжевый, 4 - голубой
    var isNotificationsEnabled by remember { mutableStateOf(true) }
    var isTimerNotificationsEnabled by remember { mutableStateOf(true) }
    var isDailyNotificationsEnabled by remember { mutableStateOf(true) }
    
    // Цвета для UI
    val backgroundColor = Color(0xFFFAF7F2) // Почти белый фон с легким бежевым оттенком
    val cardBackgroundColor = Color(0xFFEFD2AE) // Почти белый фон для карточек
    val textColor = Color(0xFF7C5F23)
    val accentColor = Color(0xFF7C5F23)
    
    // Доступные цветовые темы
    val colorThemes = listOf(
        Color(0xFF7C5F23), // Коричневый
        Color(0xFF2979FF), // Синий
        Color(0xFFE53935), // Красный
        Color(0xFFFF9800), // Оранжевый
        Color(0xFF03A9F4)  // Голубой
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Заголовок
            Text(
                text = "Приложение",
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            )
            
            // Секция Приложение
            SettingsGroup(
                backgroundColor = cardBackgroundColor
            ) {
                // Тема
                SettingsItem(
                    title = "Тема",
                    icon = Icons.Default.Settings,
                    textColor = textColor,
                    trailingContent = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { isDarkMode = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colorThemes[selectedColorTheme],
                                checkedTrackColor = colorThemes[selectedColorTheme].copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }
                )
                
                // Цвета
                SettingsItem(
                    title = "Цвета",
                    icon = Icons.Default.Settings,
                    textColor = textColor,
                    hasBottomContent = true,
                    trailingContent = {}
                )
                
                // Выбор цветовой темы
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    colorThemes.forEachIndexed { index, color ->
                        ColorThemeItem(
                            color = color,
                            isSelected = index == selectedColorTheme,
                            onClick = { selectedColorTheme = index }
                        )
                    }
                }
                
                // Язык
                SettingsItem(
                    title = "Язык",
                    icon = Icons.Default.Settings,
                    textColor = textColor,
                    trailingContent = {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Русский",
                                color = textColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Russian",
                                color = textColor.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                )
                
                // Дополнительно
                SettingsItem(
                    title = "Дополнительно",
                    icon = Icons.Default.Settings,
                    textColor = textColor,
                    trailingContent = {}
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Секция Уведомления
            Text(
                text = "Уведомления",
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            )
            
            SettingsGroup(
                backgroundColor = cardBackgroundColor
            ) {
                // Общие уведомления
                SettingsItem(
                    title = "Включить уведомления",
                    icon = Icons.Default.Notifications,
                    textColor = textColor,
                    trailingContent = {
                        Switch(
                            checked = isNotificationsEnabled,
                            onCheckedChange = { isNotificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colorThemes[selectedColorTheme],
                                checkedTrackColor = colorThemes[selectedColorTheme].copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }
                )
                
                // Дополнительные настройки уведомлений отображаются только если основные включены
                if (isNotificationsEnabled) {
                    // Ежедневные уведомления
                    SettingsItem(
                        title = "Напоминание в 16:00",
                        textColor = textColor,
                        trailingContent = {
                            Switch(
                                checked = isDailyNotificationsEnabled,
                                onCheckedChange = { isDailyNotificationsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = colorThemes[selectedColorTheme],
                                    checkedTrackColor = colorThemes[selectedColorTheme].copy(alpha = 0.5f),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                                )
                            )
                        }
                    )
                    
                    // Уведомления таймера
                    SettingsItem(
                        title = "Уведомления таймера",
                        textColor = textColor,
                        trailingContent = {
                            Switch(
                                checked = isTimerNotificationsEnabled,
                                onCheckedChange = { isTimerNotificationsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = colorThemes[selectedColorTheme],
                                    checkedTrackColor = colorThemes[selectedColorTheme].copy(alpha = 0.5f),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                                )
                            )
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Секция Резервное копирование
            Text(
                text = "Резервное копирование",
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            )
            
            SettingsGroup(
                backgroundColor = cardBackgroundColor
            ) {
                // Сохранить
                SettingsItem(
                    title = "Сохранить",
                    textColor = textColor,
                    leadingIcon = painterResource(id = R.drawable.ic_notes),
                    trailingContent = {}
                )
                
                // Загрузить
                SettingsItem(
                    title = "Загрузить",
                    textColor = textColor,
                    leadingIcon = painterResource(id = R.drawable.ic_mosque),
                    trailingContent = {}
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Секция Разработка
            Text(
                text = "Разработка",
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            )
            
            SettingsGroup(
                backgroundColor = cardBackgroundColor
            ) {
                // Исходный код
                SettingsItem(
                    title = "Исходный код",
                    textColor = textColor,
                    leadingIcon = painterResource(id = R.drawable.ic_settings),
                    trailingContent = {}
                )
                
                // Отслеживание проблем
                SettingsItem(
                    title = "Отслеживание проблем",
                    textColor = textColor,
                    leadingIcon = painterResource(id = R.drawable.ic_notes),
                    trailingContent = {}
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Секция Другое
            Text(
                text = "Другое",
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            )
            
            SettingsGroup(
                backgroundColor = cardBackgroundColor
            ) {
                // Про приложение
                SettingsItem(
                    title = "Про Shkiper",
                    textColor = textColor,
                    leadingIcon = painterResource(id = R.drawable.ic_settings),
                    trailingContent = {}
                )
                
                // Статистика
                SettingsItem(
                    title = "Статистика",
                    textColor = textColor,
                    leadingIcon = painterResource(id = R.drawable.ic_mosque),
                    trailingContent = {}
                )
                
                // Вступление
                SettingsItem(
                    title = "Вступление",
                    textColor = textColor,
                    leadingIcon = painterResource(id = R.drawable.ic_notes),
                    trailingContent = {}
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Секция Поддержать
            Text(
                text = "Поддержать",
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            )
            
            SettingsGroup(
                backgroundColor = cardBackgroundColor,
                hasBorderColor = true,
                borderColor = accentColor
            ) {
                // Оценить
                SettingsItem(
                    title = "Оценить Shkiper",
                    textColor = textColor,
                    leadingIcon = painterResource(id = R.drawable.ic_settings),
                    trailingContent = {}
                )
                
                // Поддержка разработки
                SettingsItem(
                    title = "Поддержка разработки",
                    textColor = textColor,
                    leadingIcon = painterResource(id = R.drawable.ic_mosque),
                    trailingContent = {}
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Информация
            Text(
                text = "Информация",
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            )
            
            SettingsGroup(
                backgroundColor = cardBackgroundColor
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "Ваши данные хранятся исключительно на вашем устройстве. Удаление данных вашего приложения может привести к безвозвратной потере данных. Чтобы предотвратить это, не забудьте сохранить файл данных перед выполнением сброса.",
                        color = textColor.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Для нижней навигации
        }
    }
}

@Composable
fun SettingsGroup(
    backgroundColor: Color,
    hasBorderColor: Boolean = false,
    borderColor: Color = Color.Transparent,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (hasBorderColor) {
                    Modifier.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(24.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    textColor: Color,
    icon: ImageVector? = null,
    leadingIcon: Any? = null,
    hasBottomContent: Boolean = false,
    trailingContent: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    icon != null -> {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    leadingIcon != null && leadingIcon is Int -> {
                        Icon(
                            painter = painterResource(id = leadingIcon),
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = title,
                    color = textColor,
                    fontSize = 16.sp
                )
            }
            
            trailingContent()
        }
        
        if (!hasBottomContent) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(start = 56.dp, end = 16.dp)
                    .align(Alignment.BottomCenter)
                    .background(textColor.copy(alpha = 0.1f))
            )
        }
    }
}

@Composable
fun SettingsSubItem(
    title: String,
    textColor: Color,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 14.sp
        )
        
        trailingContent()
    }
}

@Composable
fun ColorThemeItem(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.White)
            .padding(2.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color,
                            color.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
} 