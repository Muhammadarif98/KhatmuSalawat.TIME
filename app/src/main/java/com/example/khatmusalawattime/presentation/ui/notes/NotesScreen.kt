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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khatmusalawattime.presentation.theme.BlueAccent
import com.example.khatmusalawattime.presentation.ui.notes.items.GoalListItem

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
                    title = { Text("Цели", fontSize = 30.sp,
                        color = textColor) },
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
                                fontSize = 16.sp,
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
                    modifier = Modifier.height(70.dp),
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
                    item {
                        Spacer(modifier = Modifier.height(78.dp))
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