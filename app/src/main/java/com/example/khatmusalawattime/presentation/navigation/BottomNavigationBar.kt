package com.example.khatmusalawattime.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.khatmusalawattime.R

/**
 * Компонент нижней навигации для всего приложения
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?
) {
    // Цвета согласно требованиям
    val gradientColors = listOf(
        Color(0xFFE5D9A3), // Верхний цвет градиента
        Color(0xFFF6EFCF)  // Нижний цвет градиента
    )
    
    // Список экранов для навигации
    val screens = listOf(
        NavItem(Route.Notes.path, R.drawable.ic_notes, "Заметки"),
        NavItem(Route.Home.path, R.drawable.ic_mosque, "Главная"),
        NavItem(Route.Settings.path, R.drawable.ic_settings, "Настройки")
    )
    
    Box(
        modifier = Modifier
            .padding(start = 30.dp, end = 30.dp, bottom = 24.dp)
            .shadow(10.dp, shape = RoundedCornerShape(80.dp))
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(80.dp))
                .shadow(10.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors
                    )
                ),
        ) {
            Row(
                modifier = Modifier
                    .width(360.dp)
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                screens.forEachIndexed { index, item ->
                    // Добавляем элемент навигации
                    AddNavItem(
                        item = item,
                        currentRoute = currentRoute,
                        navController = navController
                    )
                    
                    // Добавляем разделитель между элементами (кроме последнего)
                    if (index < screens.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.5.dp)
                                .background(Color(0xFF827868))
                        )
                    }
                }
            }
        }
    }
}

/**
 * Данные для элемента навигации
 */
data class NavItem(
    val route: String,
    val icon: Int,
    val description: String
)

/**
 * Элемент нижней навигации
 */
@Composable
fun RowScope.AddNavItem(
    item: NavItem,
    currentRoute: String?,
    navController: NavController
) {
    // Цвета для элемента
    val selectedBgColor = Color(0xFF827868)
    val selectedIconColor = Color.White
    val unselectedIconColor = Color(0xFF827868)
    
    // Определяем, выбран ли текущий элемент
    val isSelected = currentRoute == item.route
    
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .background(if (isSelected) selectedBgColor else Color.Transparent)
            .weight(1f)
            .clickable {
                if (!isSelected) {
                    navController.navigate(item.route) {
                        popUpTo(item.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Иконка увеличенного размера
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.description,
            tint = if (isSelected) selectedIconColor else unselectedIconColor,
            modifier = Modifier.size(35.dp)
        )
    }
}