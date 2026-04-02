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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khatmusalawattime.domain.model.CounterGoal
import com.example.khatmusalawattime.domain.model.GoalType

/**
 * Выдвижная панель цели с анимацией
 * Показывает компактный индикатор, который разворачивается в полную панель
 */
@Composable
fun AnimatedGoalPanel(
    goals: List<CounterGoal>,
    selectedIndex: Int,
    onPageChanged: (Int) -> Unit,
    onDelete: (String) -> Unit,
    onReset: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    if (goals.isEmpty()) return

    // Актуальное значение selectedIndex для использования в coroutines
    val currentSelectedIndex by rememberUpdatedState(selectedIndex)

    val pagerState = rememberPagerState(
        initialPage = selectedIndex.coerceIn(0, (goals.size - 1).coerceAtLeast(0)),
        pageCount = { goals.size }
    )

    // Синхронизация пейджера с внешним индексом
    LaunchedEffect(selectedIndex) {
        if (pagerState.currentPage != selectedIndex && selectedIndex in goals.indices) {
            pagerState.animateScrollToPage(selectedIndex)
        }
    }

    // Уведомление о смене страницы (используем currentSelectedIndex чтобы избежать stale closure)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page != currentSelectedIndex) {
                onPageChanged(page)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Кнопка показа/скрытия + индикатор точек
        GoalToggleButton(
            isExpanded = isExpanded,
            progress = goals.getOrNull(pagerState.currentPage)?.progress ?: 0f,
            pageCount = goals.size,
            currentPage = pagerState.currentPage,
            onClick = { isExpanded = !isExpanded }
        )

        // Выдвижная панель с HorizontalPager
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
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val goal = goals[page]
                ExpandedGoalCard(
                    goal = goal,
                    onDelete = {
                        onDelete(goal.id)
                        isExpanded = false
                    },
                    onReset = { onReset(goal.id) },
                    onCollapse = { isExpanded = false }
                )
            }
        }
    }
}

@Composable
private fun GoalToggleButton(
    isExpanded: Boolean,
    progress: Float,
    pageCount: Int,
    currentPage: Int,
    onClick: () -> Unit
) {
    val completedColor = remember { Color(0xFF81C784) }
    val buttonGradient = remember {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF81C784).copy(alpha = 0.3f),
                Color(0xFF4CAF50).copy(alpha = 0.3f)
            )
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(buttonGradient)
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
                // Галочка если цель завершена
                if (progress >= 1f) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = completedColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(
                    text = if (isExpanded) "Скрыть" else "Цели ($pageCount)",
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

        // Индикатор точек (если больше 1 цели)
        if (pageCount > 1) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(pageCount) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentPage) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentPage) Color.White
                                else Color.White.copy(alpha = 0.4f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandedGoalCard(
    goal: CounterGoal,
    onDelete: () -> Unit,
    onReset: () -> Unit,
    onCollapse: () -> Unit
) {
    // Кэшируем цвета и градиенты
    val completedColor = remember { Color(0xFF81C784) }
    val deleteButtonBgColor = remember { Color.Red.copy(alpha = 0.3f) }
    val resetButtonBgColor = remember { Color(0xFF64B5F6).copy(alpha = 0.3f) }
    val cardGradient = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.4f),
                Color.Black.copy(alpha = 0.3f)
            )
        )
    }
    val trackColor = remember { Color.White.copy(alpha = 0.2f) }

    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardGradient)
            .padding(16.dp)
    ) {
        Column {
            // Заголовок с кнопкой закрытия
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = goal.title,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = when (goal.goalType) {
                            GoalType.DAILY -> "Дневная цель"
                            GoalType.ZIKR_TARGET -> "Цель по количеству"
                        },
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }

                // Кнопки управления
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Сбросить прогресс
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(resetButtonBgColor)
                            .clickable { onReset() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Сбросить прогресс",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Удалить
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(deleteButtonBgColor)
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Удалить цель",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Большой счётчик прогресса или галочка
            if (goal.isCompleted) {
                // Завершённая цель - показываем галочку
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Завершено",
                        tint = completedColor,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Выполнено!",
                        color = completedColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = goal.currentCount.toString(),
                        color = completedColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                    Text(
                        text = " / ${goal.targetCount}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Прогресс бар
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = completedColor,
                trackColor = trackColor,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Процент выполнения
            Text(
                text = "${(goal.progress * 100).toInt()}% выполнено",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF8B7355)
@Composable
private fun AnimatedGoalPanelPreview() {
    val sampleGoals = listOf(
        CounterGoal(
            id = "1",
            title = "Салават 1000",
            targetCount = 1000,
            currentCount = 350,
            goalType = GoalType.ZIKR_TARGET
        ),
        CounterGoal(
            id = "2",
            title = "Истигфар 100",
            targetCount = 100,
            currentCount = 100,
            goalType = GoalType.DAILY,
            isCompleted = true
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF8B7355))
            .padding(16.dp)
    ) {
        AnimatedGoalPanel(
            goals = sampleGoals,
            selectedIndex = 0,
            onPageChanged = {},
            onDelete = {},
            onReset = {}
        )
    }
}
