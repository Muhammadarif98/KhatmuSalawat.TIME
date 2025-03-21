package com.example.khatmusalawattime.presentation.ui.counter

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

@Composable
fun CounterScreen(
    viewModel: CounterViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    // Colors
    val goldBgColor = Color(0xFFF2DFB8) // Золотистый фон с фото
    val goldDarkerColor = Color(0xFFE8D8B1) // Более темный золотистый для кнопок
    val textColor = Color(0xFFB3A379) // Цвет текста как на изображении
    val buttonBgColor = Color(0xFFEBE3C9) // Светло-бежевый для кнопок
    val dividerColor = Color(0xFFD4C9A8) // Цвет разделителей
    
    // State
    val count by viewModel.count.collectAsState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = goldBgColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Верхняя часть с лампами
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Левая лампа
                SmallLamp()
                
                // Центральная лампа больше
                MainLamp()
                
                // Правая лампа
                SmallLamp()
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Разделитель
            Divider(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(2.dp),
                color = dividerColor
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Большое число счетчика
    Text(
                text = count.toString(),
                style = TextStyle(
                    fontSize = 100.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Разделитель
            Divider(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(2.dp),
                color = dividerColor
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Большая кнопка с иконкой пальца
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color.Black.copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(buttonBgColor)
                    .clickable { viewModel.increment() }
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_edit),
                    contentDescription = "Увеличить счетчик",
                    tint = textColor,
                    modifier = Modifier.size(80.dp)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Нижняя панель с кнопками управления счетчиком
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Кнопка "назад"
                NavigationButton(
                    icon = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    onClick = {
                        try {
                            if (navController.previousBackStackEntry != null) {
                                navController.navigateUp()
                            } else {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        } catch (e: Exception) {
                            navController.navigate("home")
                        }
                    },
                    bgColor = buttonBgColor,
                    iconTint = textColor
                )
                
                // Кнопка уменьшения счетчика
                NavigationButton(
                    icon = Icons.Default.Refresh,
                    contentDescription = "Уменьшить",
                    onClick = { viewModel.decrement() },
                    bgColor = buttonBgColor,
                    iconTint = textColor,
                    withBorder = true
                )
                
                // Кнопка сброса (с иконкой "0")
                NavigationButton(
                    icon = "0",
                    contentDescription = "Сбросить",
                    onClick = { viewModel.reset() },
                    bgColor = buttonBgColor,
                    iconTint = textColor
                )
            }
        }
    }
}

@Composable
fun SmallLamp() {
    Box(
        modifier = Modifier
            .size(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
            contentDescription = "Декоративная лампа",
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun MainLamp() {
    Box(
        modifier = Modifier
            .size(90.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
            contentDescription = "Главная лампа",
            modifier = Modifier.size(90.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun NavigationButton(
    icon: Any,
    contentDescription: String,
    onClick: () -> Unit,
    bgColor: Color,
    iconTint: Color,
    withBorder: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(
                width = if (withBorder) 2.dp else 0.dp,
                color = iconTint.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        when (icon) {
            is androidx.compose.ui.graphics.vector.ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            is Int -> {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = contentDescription,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            is String -> {
                // Для текстовой иконки "0"
                Text(
                    text = icon,
                    color = iconTint,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CounterScreenPreview() {
    CounterScreen()
}