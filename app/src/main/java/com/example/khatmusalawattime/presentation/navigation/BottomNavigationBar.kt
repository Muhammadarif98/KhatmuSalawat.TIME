package com.example.khatmusalawattime.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

// ==================== Простая навигация без NavController ====================

/**
 * Данные для элемента простой навигации
 */
private data class SimpleNavItem(
    val screen: Screen,
    val icon: Int,
    val description: String
)

/**
 * Простой компонент нижней навигации для state-based навигации.
 * Быстрее чем версия с NavController.
 */
@Composable
fun SimpleBottomNavigationBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    // Определяем режим навигации по высоте navigation bar
    // Жестовая навигация: ~0-20dp, кнопочная: ~48dp
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val isGestureNavigation = navBarHeight < 40.dp
    val bottomPadding = if (isGestureNavigation) 24.dp else 0.dp

    // Кэшируем цвета
    val gradientColors = remember {
        listOf(
            Color(0xFFE5D9A3),
            Color(0xFFF6EFCF)
        )
    }
    val selectedBgColor = remember { Color(0xFF827868) }
    val selectedIconColor = remember { Color.White }
    val unselectedIconColor = remember { Color(0xFF827868) }
    val dividerColor = remember { Color(0xFF827868) }
    val gradient = remember { Brush.verticalGradient(colors = gradientColors) }

    // Список экранов для навигации
    val screens = remember {
        listOf(
            SimpleNavItem(Screen.Notes, R.drawable.ic_notes, "Заметки"),
            SimpleNavItem(Screen.Home, R.drawable.ic_mosque, "Главная"),
            SimpleNavItem(Screen.Settings, R.drawable.ic_settings, "Настройки")
        )
    }

    Box(
        modifier = Modifier
            .padding(start = 30.dp, end = 30.dp, bottom = bottomPadding)
            .shadow(10.dp, shape = RoundedCornerShape(80.dp))
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(80.dp))
                .shadow(10.dp)
                .background(brush = gradient),
        ) {
            Row(
                modifier = Modifier
                    .width(360.dp)
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                screens.forEachIndexed { index, item ->
                    val isSelected = currentScreen == item.screen

                    // Анимация масштаба иконки
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.2f else 1f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                        label = "iconScale"
                    )

                    // Анимация цвета иконки
                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) selectedIconColor else unselectedIconColor,
                        label = "iconTint"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(if (isSelected) selectedBgColor else Color.Transparent)
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!isSelected) {
                                    onScreenSelected(item.screen)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.description,
                            modifier = Modifier
                                .size(32.dp)
                                .scale(iconScale),
                            tint = iconTint
                        )
                    }

                    // Разделитель между элементами (кроме последнего)
                    if (index < screens.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.5.dp)
                                .background(dividerColor)
                        )
                    }
                }
            }
        }
    }
}