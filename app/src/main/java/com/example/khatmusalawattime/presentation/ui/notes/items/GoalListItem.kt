package com.example.khatmusalawattime.presentation.ui.notes.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
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
    backgroundColor: Color,
    textColor: Color,
    accentColor: Color,
    resetSwipeTrigger: Long
) {
    val isEditing = goalList.id == editingId
    var editedTitle by remember(editingId) {
        mutableStateOf(
            TextFieldValue(
                text = goalList.title,
                selection = TextRange(goalList.title.length) // Курсор в конце текста
            )
        )
    }
    val focusRequester = remember { FocusRequester() }

    // Устанавливаем минимальную высоту 70dp
    val minHeight = 70.dp

    // Эффект для автоматического фокуса при редактировании
    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }

    if (isEditing) {
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
                .defaultMinSize(minHeight = minHeight),
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
            ListItem(
                headlineContent = {
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                    ) {
                        Text(
                            text = goalList.title,
                            color = if (goalList.isCompleted) textColor.copy(alpha = 0.5f) else textColor,
                            textDecoration = if (goalList.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            fontSize = 16.sp,
                            maxLines = Int.MAX_VALUE,
                            overflow = TextOverflow.Visible
                        )
                    }
                },
                trailingContent = {
                    Row {
                        if (goalList.isCompleted) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Выполнено",
                                tint = accentColor
                            )
                        }
                    }
                },
                modifier = Modifier
                    .clickable(onClick = onItemClick)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = minHeight),
                colors = ListItemDefaults.colors(containerColor = backgroundColor),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            )
        }
    }
}