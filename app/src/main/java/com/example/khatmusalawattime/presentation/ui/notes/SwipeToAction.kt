package com.example.khatmusalawattime.presentation.ui.notes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun SwipeToAction(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    editIconTint: Color = Color.White,
    resetTrigger: Long = 0L,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val totalActionWidthPx = remember(density) {
        with(density) { (58.dp * 2 + 28.dp).toPx() } // actionButtonWidth * 2 + отступы
    }

    // Состояние диалога подтверждения удаления
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Цвета в тёплой палитре — кэшируем
    val editButtonColor = remember { Color(0xFF5B8FB9) }
    val editButtonBorder = remember { Color(0xFF4A7A9E) }
    val deleteButtonColor = remember { Color(0xFFD4837A) }
    val deleteButtonBorder = remember { Color(0xFFC06B62) }
    val buttonBgColor = remember { Color(0xFFF5EFE4) }

    // Кэшируем градиенты
    val editGradient = remember {
        Brush.verticalGradient(listOf(Color(0xFF5B8FB9), Color(0xFF5B8FB9).copy(alpha = 0.85f)))
    }
    val deleteGradient = remember {
        Brush.verticalGradient(listOf(Color(0xFFD4837A), Color(0xFFD4837A).copy(alpha = 0.85f)))
    }

    // Цвета для диалога
    val dialogTitleColor = remember { Color(0xFF3D2914) }
    val dialogTextColor = remember { Color(0xFF8B7355) }
    val dialogContainerColor = remember { Color(0xFFFAF6F0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Подстраиваемся под контент
    ) {
        // Фон с кнопками действий
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(16.dp))
                .background(buttonBgColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            // Кнопка редактирования - круглая с тенью
            Box(
                modifier = Modifier
                    .padding(vertical = 6.dp, horizontal = 4.dp)
                    .size(48.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(brush = editGradient)
                    .border(1.5.dp, editButtonBorder, CircleShape)
                    .clickable(onClick = onEdit),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Кнопка удаления - круглая с тенью
            Box(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .padding(start = 4.dp, end = 12.dp)
                    .size(48.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(brush = deleteGradient)
                    .border(1.5.dp, deleteButtonBorder, CircleShape)
                    .clickable { showDeleteDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Диалог подтверждения удаления
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(
                        text = "Удалить?",
                        color = dialogTitleColor
                    )
                },
                text = {
                    Text(
                        text = "Это действие нельзя отменить",
                        color = dialogTextColor
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            onDelete()
                        }
                    ) {
                        Text(
                            text = "Удалить",
                            color = deleteButtonColor
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(
                            text = "Отмена",
                            color = dialogTextColor
                        )
                    }
                },
                containerColor = dialogContainerColor,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Содержимое, которое будет свайпаться
        var offsetX by remember { mutableFloatStateOf(0f) }
        var isSwiped by remember { mutableStateOf(false) }
        
        // Эффект для сброса свайпа при изменении resetTrigger
        LaunchedEffect(resetTrigger) {
            if (resetTrigger > 0) {
                offsetX = 0f
                isSwiped = false
            }
        }
        
        val animatedOffset by animateFloatAsState(
            targetValue = if (isSwiped) -totalActionWidthPx else offsetX,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
            label = "offset"
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()  // Используем всю высоту контейнера
                .fillMaxWidth()
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        if (!isSwiped) {
                            // Только если не в отодвинутом состоянии, разрешаем свайп влево
                            offsetX = (offsetX + delta).coerceIn(-totalActionWidthPx, 0f)
                        } else if (delta > 0) {
                            // Если в отодвинутом состоянии, разрешаем только свайп вправо
                            offsetX = (offsetX + delta).coerceIn(-totalActionWidthPx, 0f)
                        }
                    },
                    onDragStopped = {
                        if (!isSwiped) {
                            // Определяем, нужно ли зафиксировать свайп
                            if (offsetX < -totalActionWidthPx * 0.5f) {
                                isSwiped = true
                            } else {
                                // Недостаточный свайп, возвращаем элемент в исходное положение
                                offsetX = 0f
                            }
                        } else {
                            // Проверяем, нужно ли вернуть в исходное положение
                            if (offsetX > -totalActionWidthPx * 0.5f) {
                                isSwiped = false
                                offsetX = 0f
                            } else {
                                // Оставляем в отодвинутом состоянии
                                offsetX = -totalActionWidthPx
                            }
                        }
                    }
                )
        ) {
            content()
        }
    }
} 