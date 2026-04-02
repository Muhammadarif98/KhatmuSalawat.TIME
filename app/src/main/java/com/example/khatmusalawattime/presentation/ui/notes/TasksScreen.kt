package com.example.khatmusalawattime.presentation.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.presentation.ui.notes.items.TaskItem

@Composable
fun TasksScreen(
    goalList: GoalList,
    onBackClick: () -> Unit,
    viewModel: NotesViewModel,
    backgroundGradient: Brush,
    cardColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    accentColor: Color
) {
    val isAddingNewTask by viewModel.isAddingNewTask.collectAsState()
    val resetSwipeTrigger by viewModel.resetSwipeAnimation.collectAsState()

    // Собираем актуальное состояние selectedGoalList из ViewModel
    val currentGoalList by viewModel.selectedGoalList.collectAsState()
    // Используем актуальный список или fallback на переданный параметр
    val activeGoalList = currentGoalList ?: goalList

    var editingTaskId by remember { mutableStateOf<String?>(null) }

    var newTaskTitle by remember {
        mutableStateOf(
            TextFieldValue(
                text = "",
                selection = TextRange(0)
            )
        )
    }
    val focusRequesterNewTask = remember { FocusRequester() }

    LaunchedEffect(isAddingNewTask) {
        if (isAddingNewTask) {
            focusRequesterNewTask.requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Шапка с кнопкой назад и заголовком
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка назад
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f))
                        .clickable(onClick = onBackClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Заголовок
                Text(
                    text = activeGoalList.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Карточка "Новая задача"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardColor)
                    .clickable {
                        if (!isAddingNewTask) viewModel.toggleAddingNewTask()
                    }
                    .padding(16.dp)
            ) {
                if (isAddingNewTask) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            textStyle = TextStyle(
                                color = textColor,
                                fontSize = 16.sp
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (newTaskTitle.text.isNotBlank()) {
                                        viewModel.addNewTask(newTaskTitle.text)
                                        newTaskTitle = TextFieldValue("", selection = TextRange(0))
                                        viewModel.toggleAddingNewTask()
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 40.dp)
                                .focusRequester(focusRequesterNewTask),
                            cursorBrush = SolidColor(accentColor)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                                .clickable {
                                    if (newTaskTitle.text.isNotBlank()) {
                                        viewModel.addNewTask(newTaskTitle.text)
                                        newTaskTitle = TextFieldValue("", selection = TextRange(0))
                                    }
                                    viewModel.toggleAddingNewTask()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Создать",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Новая задача",
                            color = secondaryTextColor,
                            fontSize = 16.sp
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Добавить",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Список задач
            LazyColumn {
                itemsIndexed(activeGoalList.tasks, key = { _, task -> task.id }) { index, task ->
                    TaskItem(
                        task = task,
                        onToggleCompletion = { viewModel.toggleTaskCompletion(task.id) },
                        onEditClick = { editingTaskId = task.id },
                        onDeleteClick = { viewModel.deleteTask(task.id) },
                        editingId = editingTaskId,
                        onSaveEdit = { newTitle ->
                            viewModel.updateTaskTitle(task.id, newTitle)
                            editingTaskId = null
                        },
                        onCancelEdit = { editingTaskId = null },
                        cardColor = cardColor,
                        textColor = textColor,
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor,
                        resetSwipeTrigger = resetSwipeTrigger
                    )

                    if (index < activeGoalList.tasks.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
