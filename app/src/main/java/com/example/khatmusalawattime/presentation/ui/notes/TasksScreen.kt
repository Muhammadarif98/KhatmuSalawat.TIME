package com.example.khatmusalawattime.presentation.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.presentation.ui.notes.items.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    goalList: GoalList,
    onBackClick: () -> Unit,
    viewModel: NotesViewModel,
    backgroundColor: Color,
    surfaceColor: Color,
    textColor: Color,
    accentColor: Color,
    navController: NavController
) {
    val isAddingNewTask by viewModel.isAddingNewTask.collectAsState()
    val resetSwipeTrigger by viewModel.resetSwipeAnimation.collectAsState()

    // Состояние для редактирования задачи прямо в элементе
    var editingTaskId by remember { mutableStateOf<String?>(null) }

    // Состояние для создания новой задачи
    var newTaskTitle by remember {
        mutableStateOf(
            TextFieldValue(
                text = "",
                selection = TextRange(0)
            )
        )
    }
    val focusRequesterNewTask = remember { FocusRequester() }

    // Автоматически фокусируемся при включении редактирования
    LaunchedEffect(isAddingNewTask) {
        if (isAddingNewTask) {
            focusRequesterNewTask.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(goalList.title, color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = accentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
        ) {
            // Элемент "Новая задача"
            ListItem(
                headlineContent = {
                    if (isAddingNewTask) {
                        // Поле ввода встроено прямо в элемент списка
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
                                .focusRequester(focusRequesterNewTask),
                            cursorBrush = SolidColor(accentColor)
                        )
                    } else {
                        Text(
                            "Новая задача",
                            color = textColor,
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.clickable { viewModel.toggleAddingNewTask() }
                        )
                    }
                },
                trailingContent = {
                    IconButton(onClick = {
                        if (isAddingNewTask) {
                            if (newTaskTitle.text.isNotBlank()) {
                                viewModel.addNewTask(newTaskTitle.text)
                                newTaskTitle = TextFieldValue("", selection = TextRange(0))
                            }
                            viewModel.toggleAddingNewTask()
                        } else {
                            viewModel.toggleAddingNewTask()
                        }
                    }) {
                        Icon(
                            imageVector = if (isAddingNewTask) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = if (isAddingNewTask) "Создать" else "Добавить задачу",
                            tint = accentColor
                        )
                    }
                },
                modifier = Modifier.height(70.dp),
                colors = ListItemDefaults.colors(containerColor = surfaceColor)
            )

            // Добавляем отступ
            Spacer(modifier = Modifier.height(4.dp))

            // Список задач
            LazyColumn(
            ) {
                itemsIndexed(goalList.tasks) { index, task ->
                    TaskItem(
                        task = task,
                        onToggleCompletion = { viewModel.toggleTaskCompletion(task.id) },
                        onEditClick = {
                            editingTaskId = task.id
                        },
                        onDeleteClick = { viewModel.deleteTask(task.id) },
                        editingId = editingTaskId,
                        onSaveEdit = { newTitle ->
                            viewModel.updateTaskTitle(task.id, newTitle)
                            editingTaskId = null
                        },
                        onCancelEdit = { editingTaskId = null },
                        backgroundColor = surfaceColor,
                        textColor = textColor,
                        accentColor = accentColor,
                        itemHeight = 70.dp,
                        resetSwipeTrigger = resetSwipeTrigger
                    )

                    // Добавляем Spacer после каждого элемента, кроме последнего
                    if (index < goalList.tasks.size - 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}
