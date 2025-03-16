package com.example.khatmusalawattime.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.presentation.theme.BlueAccent

/**
 * Компонент нижней навигации для всего приложения
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?
) {
    // Цвета
    val bgColor = Color(0xFFF0E8C9).copy(alpha = 0.85f)
    val textColor = Color(0xFF8B7E66)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f) // Островок на 80% ширины экрана
                .height(60.dp) // Увеличили высоту навигации
                .clip(RoundedCornerShape(25.dp))
                .shadow(3.dp, RoundedCornerShape(25.dp))
                .background(bgColor)
                .padding(horizontal = 12.dp, vertical = 10.dp), // Увеличили вертикальный отступ
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка заметок (слева)
            Box(
                modifier = Modifier
                    .size(46.dp) // Увеличили размер кнопки
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (currentRoute == "notes") Color.White.copy(alpha = 0.6f) else Color.Transparent)
                    .clickable { navController.navigate("notes") {
                        // Избегаем создания нескольких копий экрана в стеке
                        popUpTo("notes") { inclusive = true }
                        launchSingleTop = true
                    } }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_edit), // Стандартная иконка карандаша
                    contentDescription = "Заметки",
                    tint = if (currentRoute == "notes") BlueAccent else textColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            
            // Кнопка главного экрана (по центру)
            Box(
                modifier = Modifier
                    .size(46.dp) // Увеличили размер кнопки
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (currentRoute == "home") Color.White.copy(alpha = 0.6f) else Color.Transparent)
                    .clickable { navController.navigate("home") {
                        // Избегаем создания нескольких копий экрана в стеке
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    } }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mosque), // Иконка мечети
                    contentDescription = "Главная",
                    tint = if (currentRoute == "home") BlueAccent else textColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            
            // Кнопка настроек (справа)
            Box(
                modifier = Modifier
                    .size(46.dp) // Увеличили размер кнопки
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (currentRoute == "settings") Color.White.copy(alpha = 0.6f) else Color.Transparent)
                    .clickable { navController.navigate("settings") {
                        // Избегаем создания нескольких копий экрана в стеке
                        popUpTo("settings") { inclusive = true }
                        launchSingleTop = true
                    } }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings), // Стандартная иконка уведомления
                    contentDescription = "Настройки",
                    tint = if (currentRoute == "settings") BlueAccent else textColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
} 