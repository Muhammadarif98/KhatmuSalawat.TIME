package com.example.khatmusalawattime.presentation.ui.notes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    resetTrigger: Long = 0L, // Новый параметр для сброса свайпа
    content: @Composable () -> Unit
) {
    val actionButtonWidth = 60.dp  // Увеличиваем ширину кнопок
    val totalActionWidth = actionButtonWidth * 2
    val totalActionWidthPx = with(LocalDensity.current) { totalActionWidth.toPx() }
    val itemHeight = 60.dp  // Минимальная высота элемента
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)  // Устанавливаем минимальную высоту
    ) {
        // Фон с иконками (будет виден при свайпе)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            
            // Иконка редактирования
            Box(
                modifier = Modifier
                    .width(actionButtonWidth)
                    .fillMaxHeight()
                    .background(Color(0xFF07A1E6)) // Синий для редактирования
                    .clickable(onClick = onEdit),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = Color.White,
                    modifier = Modifier.fillMaxHeight(0.7f)  // Увеличиваем размер иконки до 70% от высоты элемента
                )
            }
            
            // Иконка удаления
            Box(
                modifier = Modifier
                    .width(actionButtonWidth)
                    .fillMaxHeight()
                    .background(Color(0xFFE91E63)) // Красный для удаления
                    .clickable(onClick = { 
                        // Вызываем колбэк удаления
                        onDelete()
                    }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Color.White,
                    modifier = Modifier.fillMaxHeight(0.7f)  // Увеличиваем размер иконки до 70% от высоты элемента
                )
            }
        }

        // Создаем уникальный ключ для remember, чтобы состояние не переходило между элементами
        val swipeableKey = remember { java.util.UUID.randomUUID() }
        
        // Содержимое, которое будет свайпаться
        var offsetX by remember(swipeableKey) { mutableFloatStateOf(0f) }
        var isSwiped by remember(swipeableKey) { mutableStateOf(false) }
        
        // Эффект для сброса свайпа при изменении resetTrigger
        LaunchedEffect(resetTrigger) {
            if (resetTrigger > 0) {
                offsetX = 0f
                isSwiped = false
            }
        }
        
        val animatedOffset by animateFloatAsState(
            targetValue = if (isSwiped) -totalActionWidthPx else offsetX,
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