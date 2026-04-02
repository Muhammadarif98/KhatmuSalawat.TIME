package com.example.khatmusalawattime.presentation.ui.notes.items

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.presentation.ui.notes.SwipeToAction

@Composable
fun GoalListItem(
    goalList: GoalList,
    onItemClick: () -> Unit,
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
    val isEditing = goalList.id == editingId
    val completedColor = remember { Color(0xFF81C784) }

    var editedTitle by remember(editingId) {
        mutableStateOf(
            TextFieldValue(
                text = goalList.title,
                selection = TextRange(goalList.title.length)
            )
        )
    }
    val focusRequester = remember { FocusRequester() }

    // Подсчёт задач
    val totalTasks = goalList.tasks.size
    val completedTasks = goalList.tasks.count { it.isCompleted }
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

    // Анимация прогресса
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "progress"
    )

    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }

    if (isEditing) {
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
                // Иконка
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.List,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                BasicTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
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
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                        .clickable { onSaveEdit(editedTitle.text) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Сохранить",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
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
                    .clickable(onClick = onItemClick)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Иконка списка
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (goalList.isCompleted)
                                    completedColor.copy(alpha = 0.15f)
                                else
                                    accentColor.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (goalList.isCompleted)
                                Icons.Outlined.CheckCircle
                            else
                                Icons.Outlined.List,
                            contentDescription = null,
                            tint = if (goalList.isCompleted)
                                completedColor
                            else
                                accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Текст и прогресс
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = goalList.title,
                            color = if (goalList.isCompleted)
                                textColor.copy(alpha = 0.5f)
                            else
                                textColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (totalTasks > 0) {
                            Spacer(modifier = Modifier.height(8.dp))

                            // Прогресс-бар
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = if (goalList.isCompleted)
                                    completedColor
                                else
                                    accentColor,
                                trackColor = secondaryTextColor.copy(alpha = 0.2f),
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Счётчик задач
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$completedTasks/$totalTasks",
                            color = if (goalList.isCompleted)
                                completedColor
                            else
                                accentColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "задач",
                            color = secondaryTextColor,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
