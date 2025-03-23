package com.example.khatmusalawattime.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.presentation.ui.components.CustomSwitch

@OptIn(ExperimentalMaterial3Api::class)
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
    val textColor = Color(0xFF7C5F23)
    
    // Цвета для градиентной обводки
    val borderGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF4DBAD),
            Color(0xFFFDFBCC),
            Color(0xFFF9EBBD),
            Color(0xFFF4DBAD)
        )
    )
    
    // Цвета для градиентного фона карточек
    val cardGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFF1E4D1),
            Color(0xFFFFFFFC),
            Color(0xFFF1E4D1)
        )
    )
    
    // Доступные цветовые темы
    val colorThemes = listOf(
        Color(0xFF7C5F23), // Коричневый
        Color(0xFF2979FF), // Синий
        Color(0xFFE53935), // Красный
        Color(0xFFFF9800), // Оранжевый
        Color(0xFF03A9F4)  // Голубой
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.backsetting),
                contentScale = ContentScale.FillBounds
            )
            .statusBarsPadding()
    ) {

        // Контент настроек
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Секция темы
            SettingsSection(title = "Приложение") {
                SettingsGroup(
                    backgroundGradient = cardGradient,
                    borderGradient = borderGradient
                ) {
                    // Тема
                    SettingsItem(
                        title = "Тема",
                        icon = Icons.Default.Settings,
                        textColor = textColor,
                        trailingContent = {
                            CustomSwitch(
                                checked = isDarkMode,
                                onCheckedChange = { isDarkMode = it }
                            )
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Секция Уведомления
            SettingsSection(title = "Уведомления") {
                SettingsGroup(
                    backgroundGradient = cardGradient,
                    borderGradient = borderGradient
                ) {
                    // Общие уведомления
                    SettingsItem(
                        title = "Включить уведомления",
                        icon = Icons.Default.Notifications,
                        textColor = textColor,
                        trailingContent = {
                            CustomSwitch(
                                checked = isNotificationsEnabled,
                                onCheckedChange = { isNotificationsEnabled = it }
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
                                CustomSwitch(
                                    checked = isDailyNotificationsEnabled,
                                    onCheckedChange = { isDailyNotificationsEnabled = it }
                                )
                            }
                        )
                        
                        // Уведомления таймера
                        SettingsItem(
                            title = "Уведомления таймера",
                            textColor = textColor,
                            trailingContent = {
                                CustomSwitch(
                                    checked = isTimerNotificationsEnabled,
                                    onCheckedChange = { isTimerNotificationsEnabled = it }
                                )
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Секция Разработка
            SettingsSection(title = "Разработка") {
                SettingsGroup(
                    backgroundGradient = cardGradient,
                    borderGradient = borderGradient
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
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Секция Другое - без статистики
            SettingsSection(title = "Другое") {
                SettingsGroup(
                    backgroundGradient = cardGradient,
                    borderGradient = borderGradient
                ) {
                    // Про приложение
                    SettingsItem(
                        title = "Про Shkiper",
                        textColor = textColor,
                        leadingIcon = painterResource(id = R.drawable.ic_settings),
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
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Секция Поддержать
            SettingsSection(title = "Поддержать") {
                SettingsGroup(
                    backgroundGradient = cardGradient,
                    borderGradient = borderGradient
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
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Информация
            SettingsSection(title = "Информация") {
                SettingsGroup(
                    backgroundGradient = cardGradient,
                    borderGradient = borderGradient
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
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Для нижней навигации
        }
    }
}

@Composable
fun SettingsGroup(
    backgroundGradient: Brush,
    borderGradient: Brush,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 10.dp,
                brush = borderGradient,
                shape = RoundedCornerShape(35.dp)
            ),
        shape = RoundedCornerShape(35.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundGradient)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
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
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF7C5F23),
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
        )
        
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
} 