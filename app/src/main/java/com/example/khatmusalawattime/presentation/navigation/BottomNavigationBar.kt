package com.example.khatmusalawattime.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.presentation.theme.BlueAccent

/**
 * Компонент нижней навигации для всего приложения
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?
) {
    // Цвета
    val gradientColors = listOf(Color(0xFFFAF2B4), Color(0xFFECB694))
    val selectedColor = Color(0xFFFFF6EF)
    val iconColor = Color(0xFF333333)
    val selectedIconColor = BlueAccent

    // Анимация
    val buttonSize = 50.dp
    val animatedOffset = remember { Animatable(0f) }
    var containerWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    LaunchedEffect(currentRoute, containerWidth) {
        val target = when (currentRoute) {
            "notes" -> 0f
            "home" -> with(density) { (containerWidth / 2 - buttonSize / 2).toPx() }
            "settings" -> with(density) { (containerWidth - buttonSize).toPx() }
            else -> 0f
        }
        animatedOffset.animateTo(target)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .shadow(2.dp, CircleShape)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(60.dp)
            ) {
                containerWidth = maxWidth

                // Анимированный индикатор
                Box(
                    modifier = Modifier
                        .offset { IntOffset(animatedOffset.value.toInt(), 0) }
                        .size(buttonSize)
                        .clip(CircleShape)
                        .background(selectedColor.copy(alpha = 0.9f))
                )

                // Основной ряд кнопок
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(60.dp)
                        .clip(CircleShape)
                        .shadow(2.dp, CircleShape)
                        .background(Brush.verticalGradient(gradientColors))
                        .padding(horizontal = 1.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationButton(
                        route = "notes",
                        icon = R.drawable.ic_notes,
                        description = "Заметки",
                        padding = PaddingValues(start = 5.dp, top = 5.dp, bottom = 5.dp),
                        currentRoute = currentRoute,
                        selectedColor = selectedColor,
                        iconColor = iconColor,
                        selectedIconColor = selectedIconColor,
                        navController = navController
                    )

                    NavigationButton(
                        route = "home",
                        icon = R.drawable.ic_mosque,
                        description = "Главная",
                        padding = PaddingValues(5.dp),
                        currentRoute = currentRoute,
                        selectedColor = selectedColor,
                        iconColor = iconColor,
                        selectedIconColor = selectedIconColor,
                        navController = navController
                    )

                    NavigationButton(
                        route = "settings",
                        icon = R.drawable.ic_settings,
                        description = "Настройки",
                        padding = PaddingValues(end = 5.dp, top = 5.dp, bottom = 3.dp),
                        currentRoute = currentRoute,
                        selectedColor = selectedColor,
                        iconColor = iconColor,
                        selectedIconColor = selectedIconColor,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationButton(
    route: String,
    icon: Int,
    description: String,
    padding: PaddingValues,
    currentRoute: String?,
    selectedColor: Color,
    iconColor: Color,
    selectedIconColor: Color,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .size(50.dp)
            .clip(CircleShape)
            .background( // Фон кнопки
                if (currentRoute == route) selectedColor.copy(alpha = 0.9f)
                else Color.Transparent
            )
            .clickable {
                navController.navigate(route) {
                    popUpTo(route) { inclusive = true }
                    launchSingleTop = true
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = description,
            tint = if (currentRoute == route) selectedIconColor else iconColor,
            modifier = Modifier.size(25.dp)
        )
    }
}