package com.example.khatmusalawattime.presentation.ui.alarm

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import coil.compose.rememberAsyncImagePainter
import com.example.khatmusalawattime.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AlarmScreen(
    viewModel: AlarmViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    // Colors
    val pageBgColor = Color(0xFFFFF2CC) // Светлый фоновый цвет страницы
    val deviceBgColor = Color(0xFFE8D8B1) // Цвет устройства как на изображении
    val textColor = Color(0xFF8B7E66)
    val accentColor = Color(0xFFD9CAA4)
    
    // States
    val selectedTime by viewModel.selectedTime.collectAsState()
    val formattedTime by viewModel.formattedTime.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    val isActive by viewModel.isActive.collectAsState()
    val userImageUri by viewModel.userImageUri.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    
    // Контекст для доступа к ресурсам
    val context = LocalContext.current
    
    // Определяем, какое разрешение запрашивать в зависимости от версии Android
    val galleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    // Запрос разрешения на доступ к галерее
    val galleryPermissionState = rememberPermissionState(permission = galleryPermission)
    
    // Launcher для выбора изображения из галереи
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.saveUserImage(it)
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = pageBgColor // Светлый фон для всей страницы
    ) {
        // Основной контейнер с выравниванием по центру
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f) // Ширина устройства - 85% экрана
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Устройство таймера - коричневый островок
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(40.dp))
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(40.dp),
                            spotColor = Color.Black.copy(alpha = 0.3f)
                        )
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    deviceBgColor.copy(alpha = 0.95f),
                                    deviceBgColor
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Изображение пользователя с закругленными краями
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 11f)
                                .clip(RoundedCornerShape(32.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF75D9E8),
                                            Color(0xFF3D98E8)
                                        )
                                    )
                                )
                                .clickable {
                                    // Проверяем разрешение перед открытием галереи
                                    if (galleryPermissionState.status.isGranted) {
                                        galleryLauncher.launch("image/*")
                                    } else {
                                        // Запрашиваем разрешение
                                        galleryPermissionState.launchPermissionRequest()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Показываем пользовательское изображение, если оно выбрано
                            userImageUri?.let { uri ->
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = uri,
                                        onError = {
                                            // В случае ошибки загрузки изображения, сбрасываем его
                                            Log.e("AlarmScreen", "Ошибка загрузки изображения: ${it.result.throwable.message}")
                                            viewModel.resetUserImage()
                                        }
                                    ),
                                    contentDescription = "Пользовательское изображение",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            
                            // Кнопка сброса изображения
                            if (userImageUri != null) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 8.dp, end = 8.dp)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.8f))
                                        .clickable { viewModel.resetUserImage() }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Сбросить изображение",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Горизонтальная линия - верхняя
                        Row(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .shadow(2.dp, RoundedCornerShape(1.dp))
                                    .background(accentColor)
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .size(6.dp)
                                    .shadow(2.dp, CircleShape)
                                    .background(accentColor)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .shadow(2.dp, RoundedCornerShape(1.dp))
                                    .background(accentColor)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Время таймера - с большим 3D эффектом
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Получаем минуты и секунды из формата "MM:SS"
                            val parts = formattedTime.split(":")
                            val minutes = parts.getOrNull(0) ?: "00"
                            val seconds = parts.getOrNull(1) ?: "00"
                            
                            // Большой текст минут
                            Text(
                                text = minutes,
                                style = TextStyle(
                                    fontSize = 100.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                ),
                                modifier = Modifier.shadow(2.dp)
                            )
                            
                            // Двоеточие
                            Text(
                                text = ":",
                                style = TextStyle(
                                    fontSize = 100.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                ),
                                modifier = Modifier.shadow(2.dp)
                            )
                            
                            // Большой текст секунд
    Text(
                                text = seconds,
                                style = TextStyle(
                                    fontSize = 100.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                ),
                                modifier = Modifier.shadow(2.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Горизонтальная линия - нижняя
                        Row(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .shadow(2.dp, RoundedCornerShape(1.dp))
                                    .background(accentColor)
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .size(6.dp)
                                    .shadow(2.dp, CircleShape)
                                    .background(accentColor)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .shadow(2.dp, RoundedCornerShape(1.dp))
                                    .background(accentColor)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Кнопки выбора времени - как на изображении
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TimeButton(time = 5, selectedTime = selectedTime.toInt(), onClick = { viewModel.selectTime(5) })
                            TimeButton(time = 10, selectedTime = selectedTime.toInt(), onClick = { viewModel.selectTime(10) })
                            TimeButton(time = 15, selectedTime = selectedTime.toInt(), onClick = { viewModel.selectTime(15) })
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Кнопки управления внизу экрана - как на изображении
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Кнопка назад
                    ControlButton(
                        icon = R.drawable.ic_back,
                        contentDescription = "Назад",
                        onClick = { navController.navigateUp() }
                    )
                    
                    // Кнопка Play/Pause (больше других)
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = textColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.toggleTimerState() }
                            .padding(12.dp),
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
                            tint = textColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    // Кнопка Reset
                    ControlButton(
                        icon = R.drawable.ic_reset,
                        contentDescription = "Сброс",
                        onClick = { viewModel.resetTimer() }
                    )
                }
                
                // Кнопка переключения звука/вибрации
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(deviceBgColor.copy(alpha = 0.6f))
                        .clickable { viewModel.toggleSoundMode() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Показываем разные иконки в зависимости от режима
                    val iconRes = if (soundEnabled) {
                        android.R.drawable.ic_lock_silent_mode_off // Иконка со звуком
                    } else {
                        android.R.drawable.ic_lock_silent_mode // Иконка беззвучного режима
                    }
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (soundEnabled) "Режим звука" else "Режим вибрации",
                        tint = textColor
                    )
                }
                
                // Кнопка проверки звука (в правом верхнем углу устройства)
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp, end = 8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(deviceBgColor.copy(alpha = 0.6f))
                        .clickable { viewModel.testSound() }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_lock_silent_mode_off),
                        contentDescription = "Проверить звук",
                        tint = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun ControlButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deviceBgColor = Color(0xFFE8D8B1)
    val textColor = Color(0xFF8B7E66)

    Box(
        modifier = modifier
            .size(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = textColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            tint = textColor
        )
    }
}

@Composable
fun TimeButton(
    time: Int,
    selectedTime: Int,
    onClick: () -> Unit
) {
    val isSelected = time == selectedTime
    val textColor = Color(0xFF8B7E66)
    
    // Градиентный фон с 3D эффектом для выбранной кнопки
    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(
                elevation = if (isSelected) 4.dp else 2.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (isSelected) Color(0xFFD9CAA4) else Color(0xFFF0E8C9),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFD9CAA4),
                shape = RoundedCornerShape(8.dp)
            )
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