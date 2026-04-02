package com.example.khatmusalawattime.presentation.ui.counter.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khatmusalawattime.domain.model.CounterStats
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CounterStatsSection(
    stats: CounterStats,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Кнопка-тогглер с градиентом
        StatsToggleButton(
            isExpanded = isExpanded,
            totalCount = stats.totalAllTime,
            onClick = onToggle
        )

        // Выдвижная панель с анимацией
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(250)
            ) + shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(250)
            ) + fadeOut(animationSpec = tween(200))
        ) {
            ExpandedStatsCard(stats = stats)
        }
    }
}

@Composable
private fun StatsToggleButton(
    isExpanded: Boolean,
    totalCount: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF64B5F6).copy(alpha = 0.3f),
                        Color(0xFF42A5F5).copy(alpha = 0.3f)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Мини индикатор
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF64B5F6))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isExpanded) "Скрыть статистику" else "Статистика • ${formatNumber(totalCount)}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = if (isExpanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun ExpandedStatsCard(stats: CounterStats) {
    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.4f),
                        Color.Black.copy(alpha = 0.3f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column {
            // Заголовок
            Text(
                text = "Ваша статистика",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Основные метрики
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    label = "Всего",
                    value = formatNumber(stats.totalAllTime),
                    color = Color(0xFF81C784)
                )
                StatCard(
                    label = "Streak",
                    value = "${stats.currentStreak} дн.",
                    color = Color(0xFFFFB74D)
                )
                StatCard(
                    label = "В среднем",
                    value = formatNumber(stats.averagePerDay.toInt()),
                    color = Color(0xFF64B5F6)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Мини-график последних 7 дней
            Text(
                text = "Последние 7 дней",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            WeekChart(stats = stats)
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun WeekChart(
    stats: CounterStats,
    modifier: Modifier = Modifier
) {
    val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val historyMap = stats.last7Days.associateBy {
        dateFormat.format(it.date)
    }

    val maxCount = stats.last7Days.maxOfOrNull { it.totalCount } ?: 1

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 6 downTo 0) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -i)

            val dateKey = dateFormat.format(calendar.time)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val dayIndex = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
            val dayLabel = days.getOrElse(dayIndex.coerceIn(0, 6)) { "?" }

            val count = historyMap[dateKey]?.totalCount ?: 0
            val height = if (maxCount > 0) (count.toFloat() / maxCount * 50).coerceAtLeast(6f) else 6f
            val isToday = i == 0

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Значение над столбцом
                if (count > 0) {
                    Text(
                        text = formatNumber(count),
                        fontSize = 8.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(height.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when {
                                isToday && count > 0 -> Color(0xFF81C784)
                                count > 0 -> Color(0xFF64B5F6)
                                else -> Color.White.copy(alpha = 0.15f)
                            }
                        )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = dayLabel,
                    fontSize = 10.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isToday) Color.White else Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000f)
        number >= 1_000 -> String.format("%.1fK", number / 1_000f)
        else -> number.toString()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF8B7355)
@Composable
private fun CounterStatsSectionPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF8B7355))
            .padding(16.dp)
    ) {
        CounterStatsSection(
            stats = CounterStats(
                totalAllTime = 15420,
                currentStreak = 7,
                averagePerDay = 520.0f
            ),
            isExpanded = true,
            onToggle = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF8B7355)
@Composable
private fun StatsToggleButtonPreview() {
    Box(
        modifier = Modifier
            .background(Color(0xFF8B7355))
            .padding(16.dp)
    ) {
        StatsToggleButton(
            isExpanded = false,
            totalCount = 15420,
            onClick = {}
        )
    }
}
