package com.example.khatmusalawattime.presentation.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.model.Task
import com.example.khatmusalawattime.presentation.theme.BlueAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val goalLists by viewModel.goalLists.collectAsState()
    val selectedGoalList by viewModel.selectedGoalList.collectAsState()
    val isAddingNewList by viewModel.isAddingNewList.collectAsState()
    val isAddingNewTask by viewModel.isAddingNewTask.collectAsState()
    val resetSwipeTrigger by viewModel.resetSwipeAnimation.collectAsState()

    val backgroundColor = Color(0xFFECDCC3)
    val surfaceColor = Color(0xFFDDCBB8)
    val textColor = Color(0xFF2C2C2C)
    val accentColor = BlueAccent

    // Состояние для редактирования списка прямо в элементе
    var editingListId by remember { mutableStateOf<String?>(null) }

    // Состояние для создания нового списка
    var newListTitle by remember {
        mutableStateOf(
            TextFieldValue(
                text = "",
                selection = TextRange(0)
            )
        )
    }
    val focusRequesterNewList = remember { FocusRequester() }

    // Автоматически фокусируемся при включении редактирования
    LaunchedEffect(isAddingNewList) {
        if (isAddingNewList) {
            focusRequesterNewList.requestFocus()
        }
    }

    if (selectedGoalList == null) {
        // Главный экран со списком целей
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Цели", color = textColor) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor
                    )
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(80.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .shadow(5.dp, RoundedCornerShape(28.dp))
                            .background(surfaceColor)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Кнопка заметок (слева)
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(Color.White.copy(alpha = 0.6f)) // Активна на экране заметок
                                .clickable { /* Уже на экране заметок */ }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_notes),
                                contentDescription = "Заметки",
                                tint = accentColor, // Акцентный цвет для активной вкладки
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(width = 62.dp, height = 62.dp)
                                .clip(RoundedCornerShape(15.dp)), // Активна на экране заметок,
                            contentAlignment = Alignment.Center
                        ) {

                        }
                        // Кнопка главного экрана (по центру)
                        Box(
                            modifier = Modifier
                                .size(width = 62.dp, height = 62.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(Color.Transparent)
                                .clickable {
                                    navController.navigate("home") {
                                        popUpTo("notes") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Главная",
                                    tint = textColor.copy(alpha = 0.6f),
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "Главная",
                                    fontSize = 12.sp,
                                    color = textColor.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(paddingValues)
            ) {
                // Поле для добавления нового списка
                ListItem(
                    headlineContent = {
                        if (isAddingNewList) {
                            BasicTextField(
                                value = newListTitle,
                                onValueChange = { newListTitle = it },
                                textStyle = TextStyle(
                                    color = textColor,
                                    fontSize = 16.sp
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (newListTitle.text.isNotBlank()) {
                                            viewModel.addNewList(newListTitle.text)
                                            newListTitle =
                                                TextFieldValue("", selection = TextRange(0))
                                            viewModel.toggleAddingNewList()
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequesterNewList),
                                cursorBrush = SolidColor(accentColor)
                            )
                        } else {
                            Text(
                                "Новый список",
                                color = textColor,
                                style = TextStyle(fontSize = 16.sp),
                                modifier = Modifier.clickable { viewModel.toggleAddingNewList() }
                            )
                        }
                    },
                    trailingContent = {
                        IconButton(onClick = {
                            if (isAddingNewList) {
                                if (newListTitle.text.isNotBlank()) {
                                    viewModel.addNewList(newListTitle.text)
                                    newListTitle = TextFieldValue("", selection = TextRange(0))
                                }
                                viewModel.toggleAddingNewList()
                            } else {
                                viewModel.toggleAddingNewList()
                            }
                        }) {
                            Icon(
                                imageVector = if (isAddingNewList) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = if (isAddingNewList) "Создать" else "Добавить список",
                                tint = accentColor
                            )
                        }
                    },
                    modifier = Modifier.height(60.dp),
                    colors = ListItemDefaults.colors(containerColor = surfaceColor)
                )

                // Добавляем отступ
                Spacer(modifier = Modifier.height(4.dp))

                // Список целей
                LazyColumn(
                ) {
                    itemsIndexed(goalLists) { index, goalList ->
                        GoalListItem(
                            goalList = goalList,
                            onItemClick = { viewModel.selectGoalList(goalList) },
                            onEditClick = {
                                editingListId = goalList.id
                            },
                            onDeleteClick = { viewModel.deleteList(goalList.id) },
                            editingId = editingListId,
                            onSaveEdit = { newTitle ->
                                viewModel.updateListTitle(goalList.id, newTitle)
                                editingListId = null
                            },
                            onCancelEdit = { editingListId = null },
                            backgroundColor = surfaceColor,
                            textColor = textColor,
                            accentColor = accentColor,
                            resetSwipeTrigger = resetSwipeTrigger
                        )

                        // Добавляем Spacer после каждого элемента, кроме последнего
                        if (index < goalLists.size - 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    } else {
        // Экран подзадач
        selectedGoalList?.let { goalList ->
            TasksScreen(
                goalList = goalList,
                onBackClick = { viewModel.selectGoalList(null) },
                viewModel = viewModel,
                backgroundColor = backgroundColor,
                surfaceColor = surfaceColor,
                textColor = textColor,
                accentColor = accentColor,
                navController = navController
            )
        }
    }
}

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
    val itemHeight = 60.dp  // Фиксированная высота для всех элементов

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
                .height(itemHeight),  // Добавляем фиксированную высоту
            colors = ListItemDefaults.colors(containerColor = backgroundColor)
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
                    Text(
                        text = goalList.title,
                        color = if (goalList.isCompleted) textColor.copy(alpha = 0.5f) else textColor,
                        textDecoration = if (goalList.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        style = TextStyle(fontSize = 16.sp)
                    )
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
                    .height(itemHeight),  // Добавляем фиксированную высоту
                colors = ListItemDefaults.colors(containerColor = backgroundColor)
            )
        }
    }
}

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
                    IconButton(onClick = onBackClick) {
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
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(80.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .shadow(5.dp, RoundedCornerShape(28.dp))
                        .background(surfaceColor)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Кнопка задач (слева)
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White.copy(alpha = 0.6f)) // Активна на экране заметок
                            .clickable { /* Уже на экране заметок */ }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notes),
                            contentDescription = "Заметки",
                            tint = accentColor, // Акцентный цвет для активной вкладки
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 62.dp, height = 62.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ){}
                    // Кнопка возврата к спискам (по центру)
                    Box(
                        modifier = Modifier
                            .size(width = 62.dp, height = 62.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Transparent)
                            .clickable { onBackClick() }
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад к спискам",
                                tint = textColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Списки",
                                fontSize = 12.sp,
                                color = textColor.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
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
                modifier = Modifier.height(60.dp),
                colors = ListItemDefaults.colors(
                    containerColor = surfaceColor
                )
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
    resetSwipeTrigger: Long
) {
    val isEditing = task.id == editingId
    var editedTitle by remember(editingId) {
        mutableStateOf(
            TextFieldValue(
                text = task.title,
                selection = TextRange(task.title.length) // Курсор в конце текста
            )
        )
    }
    val focusRequester = remember { FocusRequester() }
    val itemHeight = 60.dp  // Фиксированная высота для всех элементов

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
                .height(itemHeight),  // Добавляем фиксированную высоту
            colors = ListItemDefaults.colors(containerColor = backgroundColor)
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
                    Text(
                        text = task.title,
                        color = if (task.isCompleted) textColor.copy(alpha = 0.5f) else textColor,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        style = TextStyle(fontSize = 16.sp)
                    )
                },
                trailingContent = {
                    // Круглый чекбокс
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable(onClick = onToggleCompletion)
                            .border(
                                width = 2.dp,
                                color = if (task.isCompleted) accentColor else Color(0xFF00BCD4).copy(
                                    alpha = 0.7f
                                ),
                                shape = CircleShape
                            )
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isCompleted) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Выполнено",
                                tint = Color(0xFF00BCD4),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight),  // Добавляем фиксированную высоту
                colors = ListItemDefaults.colors(containerColor = backgroundColor)
            )
        }
    }
} 