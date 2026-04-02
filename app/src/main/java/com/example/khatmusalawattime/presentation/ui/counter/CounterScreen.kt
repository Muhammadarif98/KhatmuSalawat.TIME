package com.example.khatmusalawattime.presentation.ui.counter

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.domain.model.CounterGoal
import com.example.khatmusalawattime.domain.model.CounterMode
import com.example.khatmusalawattime.domain.model.CounterState
import com.example.khatmusalawattime.presentation.ui.counter.components.AnimatedGoalPanel
import com.example.khatmusalawattime.presentation.ui.counter.components.CounterDisplayPlate
import com.example.khatmusalawattime.presentation.ui.counter.components.SetGoalDialog

@Composable
fun CounterScreen(
    viewModel: CounterViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val counterState by viewModel.counterState.collectAsState()
    val count by viewModel.count.collectAsState()
    val activeGoals by viewModel.activeGoals.collectAsState()
    val selectedGoalIndex by viewModel.selectedGoalIndex.collectAsState()
    val showGoalDialog by viewModel.showGoalDialog.collectAsState()

    // Диалог создания цели
    if (showGoalDialog) {
        SetGoalDialog(
            onDismiss = { viewModel.showGoalDialog(false) },
            onConfirm = { title, targetCount, goalType ->
                viewModel.createGoal(title, targetCount, goalType)
            }
        )
    }

    when (counterState.mode) {
        is CounterMode.Free -> {
            FreeCounterScreen(
                count = count,
                goals = activeGoals,
                selectedGoalIndex = selectedGoalIndex,
                onGoalPageChanged = { index -> viewModel.selectGoal(index) },
                onIncrement = { viewModel.increment() },
                onDecrement = { viewModel.decrement() },
                onReset = { viewModel.reset() },
                onNavigateBack = onNavigateBack,
                onShowGoalDialog = { viewModel.showGoalDialog(true) },
                onDeleteGoal = { goalId -> viewModel.deleteGoal(goalId) },
                onResetGoal = { goalId -> viewModel.resetGoalProgress(goalId) }
            )
        }
        else -> {
            ZikrCounterScreen(
                state = counterState,
                onIncrement = { viewModel.increment() },
                onDecrement = { viewModel.decrement() },
                onReset = { viewModel.reset() },
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@Composable
private fun FreeCounterScreen(
    count: Int,
    goals: List<CounterGoal>,
    selectedGoalIndex: Int,
    onGoalPageChanged: (Int) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    onNavigateBack: () -> Unit,
    onShowGoalDialog: () -> Unit,
    onDeleteGoal: (String) -> Unit,
    onResetGoal: (String) -> Unit
) {
    // Кэшируем цвета
    val textColor = remember { Color(0xFFFFFFFF) }

    // Устанавливаем светлые иконки статус-бара (для тёмного фона)
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    // Основной контейнер с фоном (как на HomeScreen)
    Box(
        modifier = Modifier
            .paint(
                painter = painterResource(id = R.drawable.free_counter_back),
                contentScale = ContentScale.FillBounds
            )
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Кнопка установки цели (в углу)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onShowGoalDialog() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Цель",
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Выдвижная панель целей с каруселью
            AnimatedGoalPanel(
                goals = goals,
                selectedIndex = selectedGoalIndex,
                onPageChanged = onGoalPageChanged,
                onDelete = onDeleteGoal,
                onReset = onResetGoal
            )

            // Верхний отступ (больше = счётчик ниже)
            Spacer(modifier = Modifier.weight(3.5f))

            // Плашка с цифрами
            CounterDisplayPlate(
                count = count,
                plateHeight = 100.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            ClickableImageButton(
                onClick = onIncrement,
                modifier = Modifier
                    .size(220.dp)
                    .offset(x = (-10).dp),
                imageNormalRes = R.drawable.clickbtn,
                imagePressedRes = R.drawable.clickpressedbtn,
                contentDescription = "Увеличить счетчик"
            )

            // Нижний отступ (меньше = счётчик ниже)
            Spacer(modifier = Modifier.weight(0.4f))

            // Нижние кнопки
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatefulImageButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(90.dp),
                    imageNormalRes = R.drawable.countbackbtn,
                    imagePressedRes = R.drawable.countbackpressedbtn,
                    contentDescription = "Назад"
                )

                StatefulImageButton(
                    onClick = onDecrement,
                    modifier = Modifier.size(90.dp),
                    imageNormalRes = R.drawable.countresetbtn,
                    imagePressedRes = R.drawable.countresetpressedbtn,
                    contentDescription = "Уменьшить"
                )

                StatefulImageButton(
                    onClick = onReset,
                    modifier = Modifier
                        .size(90.dp)
                        .offset(x = 5.dp),
                    imageNormalRes = R.drawable.countzerobtn,
                    imagePressedRes = R.drawable.countzeropressedbtn,
                    contentDescription = "Сбросить"
                )
            }
        }
    }
}

@Composable
private fun ZikrCounterScreen(
    state: CounterState,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // Кэшируем шрифт и цвета
    val russoOneFamily = remember { FontFamily(Font(R.font.russo_one_regular)) }
    val textColor = remember { Color(0xFF3D2914) }
    val completedColor = remember { Color(0xFF81C784) }
    val borderColor = remember { Color(0xFFD4A574) }

    // Устанавливаем тёмные иконки статус-бара (для светлого фона)
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    // Кэшируем градиенты
    val backgroundGradient = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF8F4E8),
                Color(0xFFE8DCC8),
                Color(0xFFD4C4A8)
            )
        )
    }

    val borderGradient = remember {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFD4A574),
                Color(0xFFF4E4C4),
                Color(0xFFD4A574),
                Color(0xFFB8956C)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Декоративная рамка
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 16.dp)
                .border(
                    width = 3.dp,
                    brush = borderGradient,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(8.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFD4A574).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp)
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Отступ сверху для статус-бара
            Spacer(Modifier.statusBarsPadding().height(40.dp))

            ZikrCounterDisplay(
                state = state,
                textColor = textColor,
                russoOneFamily = russoOneFamily
            )

            // Равномерное распределение пространства для центрирования кнопки
            Spacer(modifier = Modifier.weight(1f))

            ClickableImageButton(
                onClick = onIncrement,
                modifier = Modifier
                    .size(220.dp)
                    .offset(x = (-10).dp),
                imageNormalRes = R.drawable.clickbtn,
                imagePressedRes = R.drawable.clickpressedbtn,
                contentDescription = "Увеличить счетчик"
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatefulImageButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(90.dp),
                    imageNormalRes = R.drawable.countbackbtn,
                    imagePressedRes = R.drawable.countbackpressedbtn,
                    contentDescription = "Назад"
                )

                StatefulImageButton(
                    onClick = onDecrement,
                    modifier = Modifier.size(90.dp),
                    imageNormalRes = R.drawable.countresetbtn,
                    imagePressedRes = R.drawable.countresetpressedbtn,
                    contentDescription = "Уменьшить"
                )

                StatefulImageButton(
                    onClick = onReset,
                    modifier = Modifier
                        .size(90.dp)
                        .offset(x = 5.dp),
                    imageNormalRes = R.drawable.countzerobtn,
                    imagePressedRes = R.drawable.countzeropressedbtn,
                    contentDescription = "Сбросить"
                )
            }
        }
    }
}

@Composable
private fun ZikrCounterDisplay(
    state: CounterState,
    textColor: Color,
    russoOneFamily: FontFamily
) {
    val currentZikr = state.currentZikr ?: return
    val zikrColor = Color(currentZikr.color)

    val animatedProgress by animateFloatAsState(
        targetValue = state.currentZikrProgress,
        animationSpec = tween(300),
        label = "progress"
    )

    val animatedTotalProgress by animateFloatAsState(
        targetValue = state.totalProgress,
        animationSpec = tween(300),
        label = "totalProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Индикатор зикров (точки)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            state.zikrItems.forEachIndexed { index, zikr ->
                val isActive = index == state.currentZikrIndex
                val isCompleted = index < state.currentZikrIndex ||
                        (index == state.currentZikrIndex && state.isCompleted)

                val dotColor by animateColorAsState(
                    targetValue = when {
                        isCompleted -> Color(0xFF81C784)
                        isActive -> Color(zikr.color)
                        else -> textColor.copy(alpha = 0.3f)
                    },
                    label = "dotColor"
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(if (isActive) 14.dp else 10.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                        .then(
                            if (isActive) Modifier.border(
                                2.dp,
                                textColor.copy(alpha = 0.3f),
                                CircleShape
                            ) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(8.dp)
                        )
                    }
                }
            }
        }

        // Название зикра
        AnimatedContent(
            targetState = currentZikr.name,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "zikrName"
        ) { name ->
            Text(
                text = name,
                style = TextStyle(
                    fontSize = 26.sp,
                    fontFamily = russoOneFamily,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                ),
                textAlign = TextAlign.Center
            )
        }

        // Арабский текст
        if (currentZikr.arabicText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedContent(
                targetState = currentZikr.arabicText,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "arabicText"
            ) { arabic ->
                Text(
                    text = arabic,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = textColor.copy(alpha = 0.7f)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Счётчик
        if (state.isCompleted) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF81C784),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ЗАВЕРШЕНО!",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontFamily = russoOneFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF81C784)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ВСЕГО: ${state.totalZikrCount}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = textColor.copy(alpha = 0.7f)
                    )
                )
            }
        } else {
            // Текущий счёт
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = state.currentCount.toString(),
                    style = TextStyle(
                        fontSize = 72.sp,
                        fontFamily = russoOneFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor
                    )
                )
                Text(
                    text = " / ${currentZikr.targetCount}",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = russoOneFamily,
                        fontWeight = FontWeight.Normal,
                        color = textColor.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Прогресс текущего зикра
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = zikrColor,
                    trackColor = textColor.copy(alpha = 0.15f),
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Общий прогресс
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Общий: ${state.completedTotalCount} / ${state.totalZikrCount}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = textColor.copy(alpha = 0.5f)
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(textColor.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedTotalProgress)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF81C784))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun StatefulImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageNormalRes: Int,
    imagePressedRes: Int,
    contentDescription: String?
) {
    var pressed by remember { mutableStateOf(false) }

    Image(
        painter = painterResource(id = if (pressed) imagePressedRes else imageNormalRes),
        contentDescription = contentDescription,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                    },
                    onTap = { onClick() }
                )
            }
    )
}
@Composable
fun ClickableImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageNormalRes: Int,
    imagePressedRes: Int,
    contentDescription: String?,
) {
    var pressed by remember { mutableStateOf(false) }

    androidx.compose.foundation.Image(
        painter = painterResource(id = if (pressed) imagePressedRes else imageNormalRes),
        contentDescription = contentDescription,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                    },
                    onTap = { onClick() }
                )
            }
    )
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
