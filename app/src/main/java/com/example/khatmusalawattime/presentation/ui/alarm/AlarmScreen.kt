package com.example.khatmusalawattime.presentation.ui.alarm

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.khatmusalawattime.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.wajahatkarim.flippable.FlipAnimationType
import com.wajahatkarim.flippable.Flippable
import com.wajahatkarim.flippable.rememberFlipController

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AlarmScreen(
    viewModel: AlarmViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    // Colors
    val pageBgColor = Color(0xFFFFF2CC) // Светлый фоновый цвет страницы
    val deviceBgColor = Color(0xFFE8D8B1) // Цвет устройства как на изображении
    val dBgColor = Color(0xFFD2BD85) // Цвет устройства как на изображении
    val dBgColor2 = Color(0xFFEEE3C5) // Цвет устройства как на изображении
    val dBgColor3 = Color(0xFFBCA586) // Цвет устройства как на изображении
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
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth() // Ширина устройства - 85% экрана
                    .padding(vertical = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Устройство таймера - коричневый островок
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 50.dp, bottomEnd = 50.dp))
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 50.dp, bottomEnd = 50.dp),
                            spotColor = Color.Black.copy(alpha = 0.3f)
                        )
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    dBgColor,
                                    dBgColor2,
                                    dBgColor3
                                )
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Изображение пользователя с закругленными краями
                        Box(
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .fillMaxWidth()
                                .aspectRatio(16f / 11f)
                                .clip(RoundedCornerShape(32.dp))
                                .clickable {
                                    if (galleryPermissionState.status.isGranted) {
                                        galleryLauncher.launch("image/*")
                                    } else {
                                        galleryPermissionState.launchPermissionRequest()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                userImageUri != null -> {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = userImageUri,
                                            onError = {
                                                viewModel.resetUserImage()
                                            }
                                        ),
                                        contentDescription = "Выбранное изображение",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                else -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.huzur),
                                        contentDescription = "Изображение по умолчанию",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.3f)
                                            ),
                                            startY = 0f,
                                            endY = Float.POSITIVE_INFINITY
                                        )
                                    )
                            )

                            when {
                                userImageUri != null -> {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Удалить изображение",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(12.dp)
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .padding(4.dp)
                                            .clickable { viewModel.resetUserImage() }
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Добавить фото",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(12.dp)
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // Время таймера - с большим 3D эффектом
                        FlipClockStyleTimer(formattedTime)

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Кнопки выбора времени - как на изображении
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TimeButton(
                                time = 5,
                                selectedTime = selectedTime.toInt(),
                                releasedImage = R.drawable.five,          // Изображение для обычного состояния
                                pressedImage = R.drawable.fivepressed,    // Изображение для нажатого/выбранного состояния
                                onClick = { viewModel.selectTime(5) }
                            )
                            TimeButton(
                                time = 10,
                                selectedTime = selectedTime.toInt(),
                                releasedImage = R.drawable.ten,
                                pressedImage = R.drawable.tenpressed,
                                onClick = { viewModel.selectTime(10) }
                            )
                            TimeButton(
                                time = 15,
                                selectedTime = selectedTime.toInt(),
                                releasedImage = R.drawable.fifteen,
                                pressedImage = R.drawable.fifteenpressed,
                                onClick = { viewModel.selectTime(15) }
                            )
                            //TimeButton(time = 5, selectedTime = selectedTime.toInt(), onClick = { viewModel.selectTime(5) })
                            //TimeButton(time = 10, selectedTime = selectedTime.toInt(), onClick = { viewModel.selectTime(10) })
                            //TimeButton(time = 15, selectedTime = selectedTime.toInt(), onClick = { viewModel.selectTime(15) })
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(90.dp))

                // Кнопки управления внизу экрана с красивой рамкой
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(28.dp),
                            spotColor = Color.Black.copy(alpha = 0.25f)
                        )
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFE8DCC8),
                                    Color(0xFFD9C9A8),
                                    Color(0xFFC9B998)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(4.dp)
                ) {
                    // Внутренняя рамка
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFF5EDD8),
                                        Color(0xFFE8DBC0),
                                        Color(0xFFDDD0B0)
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Кнопка назад
                            ControlButton(
                                image = R.drawable.backbtn,
                                contentDescription = "Назад",
                                onClick = onNavigateBack,
                                size = 80.dp
                            )

                            // Кнопка Play/Pause
                            ControlButton(
                                image = if (timerState is TimerState.Running) R.drawable.pausebtn else R.drawable.playbtn,
                                contentDescription = if (timerState is TimerState.Running) "Пауза" else "Старт",
                                onClick = { viewModel.toggleTimerState() },
                                size = 80.dp
                            )

                            // Кнопка Reset
                            ControlButton(
                                image = R.drawable.resetbtn,
                                contentDescription = "Сброс",
                                onClick = { viewModel.resetTimer() },
                                size = 80.dp
                            )
                        }
                    }
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
            }
        }
    }
}
@Composable
fun ControlButton(
    image: Int,
    contentDescription: String,
    onClick: () -> Unit,
    size: Dp
) {
    Image(
        painter = painterResource(id = image),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        contentScale = ContentScale.FillBounds
    )
}
@Composable
fun FlipClockStyleTimer(
    formattedTime: String,
    modifier: Modifier = Modifier
) {
    val parts = formattedTime.split(":")
    val minutes = parts.getOrNull(0)?.padStart(2, '0') ?: "00"
    val seconds = parts.getOrNull(1)?.padStart(2, '0') ?: "00"

    val minTens = minutes[0].digitToInt()
    val minOnes = minutes[1].digitToInt()
    val secTens = seconds[0].digitToInt()
    val secOnes = seconds[1].digitToInt()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FlipDigitTile(minTens, "minTens")
        Spacer(Modifier.width(0.dp))
        FlipDigitTile(minOnes, "minOnes")
        Spacer(Modifier.width(15.dp))
        FlipDigitTile(secTens, "secTens")
        Spacer(Modifier.width(0.dp))
        FlipDigitTile(secOnes, "secOnes")
    }
}

@Composable
fun FlipDigitTile(
    digit: Int,
    key: String
) {
    var previousDigit by remember(key) { mutableStateOf(digit) }
    var flipTrigger by remember(key) { mutableStateOf(0) }

    LaunchedEffect(digit) {
        if (digit != previousDigit) {
            flipTrigger++
            previousDigit = digit
        }
    }

    FlippableTile(
        value = digit.toString(),
        trigger = flipTrigger
    )
}

@Composable
fun FlippableTile(
    value: String,
    trigger: Int
) {
    val controller = rememberFlipController()

    LaunchedEffect(trigger) {
        controller.flip()
    }

    Flippable(
        frontSide = {
            TimerTile(value)
        },
        backSide = {
            TimerTile(value)
        },
        flipController = controller,
        flipAnimationType = FlipAnimationType.VERTICAL_CLOCKWISE,
        flipDurationMs = 400,
        cameraDistance = 40f,
        modifier = Modifier
    )
}

@Composable
fun TimerTile(
    value: String
) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        // 1. Изображение доски (фон)
        Image(
            painter = painterResource(id = R.drawable.doska),
            contentDescription = "Доска для цифры",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Цифра поверх доски
        Text(
            text = value,
            fontSize = 85.sp,
            fontFamily = FontFamily(Font(R.font.russo_one_regular)),
            color = Color(0xFFA58C6B),
            modifier = Modifier.align(Alignment.Center).offset(x = 3.dp, y = -3.dp)
        )
    }
}





// ... остальные импорты остаются без изменений

@Composable
fun TimeButton(
    time: Int,
    selectedTime: Int,
    releasedImage: Int,
    pressedImage: Int,
    onClick: () -> Unit
) {
    val isSelected = time == selectedTime
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val imageRes = if (isPressed || isSelected) pressedImage else releasedImage

    // Анимированное изменение тени
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 16.dp else if (isPressed) 8.dp else 12.dp,
        animationSpec = tween(durationMillis = 150)
    )

    Box(
        modifier = Modifier
            .size(86.dp)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(16.dp),
                clip = true,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "$time минут",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}


@Preview(showSystemUi = true)
@Composable
fun AlarmScreenPreview() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFFFF2CC)) {
        AlarmScreen(
            viewModel = hiltViewModel()
        )
    }
}
/*
@Composable
fun ControlButton(
    image: Int,
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
            painter = painterResource(id = image),
            contentDescription = contentDescription,
            tint = textColor
        )
    }
}
*/

/*
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
}*/