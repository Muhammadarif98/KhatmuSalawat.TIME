package com.example.khatmusalawattime.presentation.ui.notes.items

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khatmusalawattime.domain.model.Task
import com.example.khatmusalawattime.presentation.ui.notes.SwipeToAction
import kotlinx.coroutines.delay

@Composable
fun TaskItem(
    task: Task,
    onToggleCompletion: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    editingId: String?,
    onSaveEdit: (String) -> Unit,
    onCancelEdit: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    accentColor: Color,
    itemHeight: Dp = 70.dp,
    resetSwipeTrigger: Long
) {
    // Состояние для редактирования
    var editedTitle by remember(task.id) {
        mutableStateOf(
            TextFieldValue(
                text = task.title,
                selection = TextRange(task.title.length)
            )
        )
    }

    // Состояние для анимации свечения после установки галочки
    var showCompletionGlow by remember { mutableStateOf(false) }

    // Состояние для отслеживания предыдущего значения isCompleted
    // Это предотвратит запуск эффекта при первой загрузке
    var prevIsCompleted by remember { mutableStateOf(task.isCompleted) }

    // Анимируемое значение для эффекта свечения с более плавной анимацией
    val glowAlpha by animateFloatAsState(
        targetValue = if (showCompletionGlow) 0.5f else 0f,
        animationSpec = tween(
            durationMillis = 1000, // Общая длительность анимации — 1 секунда
            easing = { fraction ->
                fraction * 2f // Линейное увеличение от 0 до 1
            }
        ),
        label = "glow"
    )

    // Эффект для отслеживания изменения состояния задачи и показа свечения
    LaunchedEffect(task.isCompleted) {
        // Проверяем, было ли изменение состояния и не является ли это первой загрузкой
        if (task.isCompleted != prevIsCompleted && task.isCompleted) {
            showCompletionGlow = true
            delay(1300) // Показываем свечение немного дольше для более заметного эффекта
            showCompletionGlow = false
        }
        // Обновляем предыдущее состояние
        prevIsCompleted = task.isCompleted
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(editingId) {
        if (editingId == task.id) {
            focusRequester.requestFocus()
        }
    }

    if (editingId == task.id) {
        // Режим редактирования - без свайпа
        ListItem(
            headlineContent = {
                BasicTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { onSaveEdit(editedTitle.text) }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    cursorBrush = SolidColor(accentColor)
                )
            },
            trailingContent = {
                IconButton(onClick = { onSaveEdit(editedTitle.text) }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Сохранить",
                        tint = accentColor
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = itemHeight),
            colors = ListItemDefaults.colors(containerColor = backgroundColor),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        )
    } else {
        // Обычный режим отображения - со свайпом
        SwipeToAction(
            onEdit = onEditClick,
            onDelete = onDeleteClick,
            editIconTint = accentColor,
            resetTrigger = resetSwipeTrigger
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 0.dp)
            ) {
                ListItem(
                    headlineContent = {
                        Box(
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                        ) {
                            Text(
                                text = task.title,
                                color = if (task.isCompleted) textColor.copy(alpha = 0.5f) else textColor,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                fontSize = 16.sp,
                                maxLines = Int.MAX_VALUE, // Разрешаем неограниченное число строк
                                overflow = TextOverflow.Visible // Текст не будет обрезаться
                            )
                        }
                    },
                    trailingContent = {
                        // Круглый чекбокс с эффектом свечения
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                // Эффект свечения вокруг галочки когда она только что установлена
                                .drawBehind {
                                    if (showCompletionGlow) {
                                        drawCircle(
                                            color = Color(0xFF00BCD4).copy(alpha = glowAlpha),
                                            radius = size.width * 0.6f, // Уменьшаем радиус, чтобы он был примерно на 1dp больше размера круга
                                            center = center
                                        )
                                    }
                                }
                                .clip(CircleShape)
                                .background(
                                    color = if (task.isCompleted)
                                        Color(0xFF00BCD4)
                                    else
                                        Color.Transparent
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFF00BCD4).copy(alpha = 0.7f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (task.isCompleted) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Выполнено",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = itemHeight)
                        .clickable(onClick = onToggleCompletion),  // Клик по всему элементу для изменения состояния
                    colors = ListItemDefaults.colors(containerColor = backgroundColor),
                    tonalElevation = 0.dp, // Устраняем эффект тени
                    shadowElevation = 0.dp
                )
            }
        }
    }
}