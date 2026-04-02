package com.example.khatmusalawattime.presentation.ui.notes.items

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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
    cardColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    resetSwipeTrigger: Long
) {
    // Кэшируем цвет завершённой задачи
    val completedColor = remember { Color(0xFF81C784) }

    var editedTitle by remember(task.id) {
        mutableStateOf(
            TextFieldValue(
                text = task.title,
                selection = TextRange(task.title.length)
            )
        )
    }

    // Ключ для анимаций — привязан к task.id и task.isCompleted
    val taskKey = remember(task.id, task.isCompleted) { "${task.id}_${task.isCompleted}" }

    // Анимация свечения — только при завершении
    var showCompletionGlow by remember(task.id) { mutableStateOf(false) }

    // Анимация масштаба чекбокса
    var checkboxAnimationTrigger by remember(task.id) { mutableStateOf(false) }

    val glowAlpha by animateFloatAsState(
        targetValue = if (showCompletionGlow && task.isCompleted) 0.6f else 0f,
        animationSpec = tween(400),
        label = "glow"
    )

    val checkboxScale by animateFloatAsState(
        targetValue = if (checkboxAnimationTrigger) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        label = "scale"
    )

    // Анимация цвета чекбокса — напрямую зависит от task.isCompleted
    val checkboxBgColor by animateColorAsState(
        targetValue = if (task.isCompleted) completedColor else Color.Transparent,
        animationSpec = tween(200),
        label = "checkboxBg"
    )

    val checkboxBorderColor by animateColorAsState(
        targetValue = if (task.isCompleted) completedColor else accentColor.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "checkboxBorder"
    )

    // Запускаем анимацию только при изменении состояния на "выполнено"
    LaunchedEffect(taskKey) {
        if (task.isCompleted) {
            // Задача завершена — показываем анимацию
            checkboxAnimationTrigger = true
            showCompletionGlow = true
            delay(150)
            checkboxAnimationTrigger = false
            delay(600)
            showCompletionGlow = false
        } else {
            // Задача не завершена — сбрасываем анимации
            showCompletionGlow = false
            checkboxAnimationTrigger = false
        }
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(editingId) {
        if (editingId == task.id) {
            focusRequester.requestFocus()
        }
    }

    if (editingId == task.id) {
        // Режим редактирования
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(cardColor)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Чекбокс (неактивный в режиме редактирования)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(checkboxBgColor)
                        .border(2.dp, checkboxBorderColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

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
                        .weight(1f)
                        .focusRequester(focusRequester),
                    cursorBrush = SolidColor(accentColor)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Кнопка сохранения
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                        .clickable { onSaveEdit(editedTitle.text) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Сохранить",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    } else {
        // Обычный режим
        SwipeToAction(
            onEdit = onEditClick,
            onDelete = onDeleteClick,
            editIconTint = accentColor,
            resetTrigger = resetSwipeTrigger
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardColor)
                    .clickable(onClick = onToggleCompletion)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Красивый анимированный чекбокс
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .scale(checkboxScale)
                            .drawBehind {
                                if (showCompletionGlow) {
                                    drawCircle(
                                        color = completedColor.copy(alpha = glowAlpha),
                                        radius = size.width * 0.8f
                                    )
                                }
                            }
                            .clip(CircleShape)
                            .background(checkboxBgColor)
                            .border(2.dp, checkboxBorderColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isCompleted) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Выполнено",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Текст задачи
                    Text(
                        text = task.title,
                        color = if (task.isCompleted)
                            textColor.copy(alpha = 0.5f)
                        else
                            textColor,
                        textDecoration = if (task.isCompleted)
                            TextDecoration.LineThrough
                        else
                            TextDecoration.None,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = Int.MAX_VALUE,
                        overflow = TextOverflow.Visible,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
