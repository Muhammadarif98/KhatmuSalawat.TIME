package com.example.khatmusalawattime.presentation.ui.counter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.domain.model.CounterMode
import com.example.khatmusalawattime.domain.model.CounterStats
import com.example.khatmusalawattime.domain.model.WirdPreset
import com.example.khatmusalawattime.presentation.ui.counter.components.CounterStatsSection

@Composable
fun CounterModeScreen(
    onNavigateBack: () -> Unit,
    onModeSelected: (CounterMode) -> Unit,
    onCustomSetup: () -> Unit,
    stats: CounterStats = CounterStats(),
    statsExpanded: Boolean = false,
    onToggleStatsExpanded: () -> Unit = {}
) {
    // Кэшируем шрифт и цвета
    val russoOneFamily = remember { FontFamily(Font(R.font.russo_one_regular)) }
    val cardBgColor = remember { Color(0xFFFAF7F2) }
    val textColor = remember { Color(0xFF4A3D2A) }
    val accentColor = remember { Color(0xFF7C5F23) }

    // Кэшируем градиенты
    val borderGradient = remember {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFF4DBAD),
                Color(0xFFFDFBCC),
                Color(0xFFF4DBAD)
            )
        )
    }

    // Загружаем сохранённое значение wird count
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("counter_mode_prefs", android.content.Context.MODE_PRIVATE) }
    var selectedWirdCount by remember { mutableIntStateOf(prefs.getInt("wird_count", 100)) }
    var showWirdOptions by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.newback),
                contentScale = ContentScale.FillBounds
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(cardBgColor.copy(alpha = 0.9f))
                        .border(2.dp, borderGradient, CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Выбор режима",
                    fontFamily = russoOneFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Секция статистики
            CounterStatsSection(
                stats = stats,
                isExpanded = statsExpanded,
                onToggle = onToggleStatsExpanded
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mode cards
            // 1. Free mode
            ModeCard(
                title = "Свободный",
                description = "Бесконечный счётчик без ограничений",
                icon = Icons.Default.Add,
                iconColor = Color(0xFF64B5F6),
                borderGradient = borderGradient,
                cardBgColor = cardBgColor,
                textColor = textColor,
                onClick = { onModeSelected(CounterMode.Free) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Azkar mode
            ModeCard(
                title = "Азкары",
                description = "ИСТИГЪФАР (10) → СУБХ1АНАЛЛАГЬ (33) → АЛЬХ1АМДУЛИЛЛАГЬ (33) → АЛЛАГЬУ АКБАР (34)",
                icon = Icons.Default.Star,
                iconColor = Color(0xFF81C784),
                borderGradient = borderGradient,
                cardBgColor = cardBgColor,
                textColor = textColor,
                onClick = { onModeSelected(CounterMode.Azkar) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Wird mode with options
            ModeCardWithOptions(
                title = "Вирды",
                description = "ИСТИГЪФАР → САЛАВАТ → ЛЯЯ ИЛЯЯГЬА ИЛЛАЛЛААГЬ",
                icon = Icons.Default.Check,
                iconColor = Color(0xFFFFB74D),
                borderGradient = borderGradient,
                cardBgColor = cardBgColor,
                textColor = textColor,
                expanded = showWirdOptions,
                onExpandToggle = { showWirdOptions = !showWirdOptions },
                selectedCount = selectedWirdCount,
                onCountSelected = { count ->
                    selectedWirdCount = count
                    // Сохраняем выбор
                    prefs.edit().putInt("wird_count", count).apply()
                    onModeSelected(CounterMode.Wird(count))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Custom mode
            ModeCard(
                title = "Настраиваемый",
                description = "Создайте свой набор зикров",
                icon = Icons.Default.Edit,
                iconColor = Color(0xFFBA68C8),
                borderGradient = borderGradient,
                cardBgColor = cardBgColor,
                textColor = textColor,
                onClick = { onCustomSetup() }
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    borderGradient: Brush,
    cardBgColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, borderGradient, RoundedCornerShape(20.dp))
            .background(cardBgColor.copy(alpha = 0.95f))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ModeCardWithOptions(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    borderGradient: Brush,
    cardBgColor: Color,
    textColor: Color,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    selectedCount: Int,
    onCountSelected: (Int) -> Unit
) {
    // Анимация поворота стрелки
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "arrowRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, borderGradient, RoundedCornerShape(20.dp))
            .background(cardBgColor.copy(alpha = 0.95f))
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandToggle() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = textColor.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }

                // Анимированная стрелка
                Text(
                    text = "▼",
                    fontSize = 16.sp,
                    color = textColor.copy(alpha = 0.5f),
                    modifier = Modifier.graphicsLayer {
                        rotationZ = arrowRotation
                    }
                )
            }

            // Выдвижная секция с красивой анимацией
            AnimatedVisibility(
                visible = expanded,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { -it / 2 },
                    animationSpec = tween(200)
                ) + shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(200)
                ) + fadeOut(animationSpec = tween(150))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Выберите количество:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WirdPreset.availableCounts.forEach { count ->
                            WirdCountChip(
                                count = count,
                                isSelected = count == selectedCount,
                                textColor = textColor,
                                accentColor = iconColor,
                                onClick = { onCountSelected(count) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Информационная подсказка с градиентом
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(iconColor.copy(alpha = 0.1f))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Каждый зикр по $selectedCount раз = ${selectedCount * 3} всего",
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WirdCountChip(
    count: Int,
    isSelected: Boolean,
    textColor: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    // Анимация цветов
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else Color.Transparent,
        animationSpec = tween(200),
        label = "bgColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else textColor.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "borderColor"
    )
    val chipTextColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else textColor,
        animationSpec = tween(200),
        label = "chipTextColor"
    )

    // Анимация масштаба при выборе — уменьшена для избежания наложения
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    // Внешний контейнер для отступов между чипами
    Box(
        modifier = Modifier.padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                .background(bgColor)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = chipTextColor
            )
        }
    }
}
