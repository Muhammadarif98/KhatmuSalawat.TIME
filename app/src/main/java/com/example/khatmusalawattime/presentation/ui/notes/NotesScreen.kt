package com.example.khatmusalawattime.presentation.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.khatmusalawattime.presentation.ui.notes.items.GoalListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel()
) {
    val goalLists by viewModel.goalLists.collectAsState()
    val selectedGoalList by viewModel.selectedGoalList.collectAsState()
    val isAddingNewList by viewModel.isAddingNewList.collectAsState()
    val isAddingNewTask by viewModel.isAddingNewTask.collectAsState()
    val resetSwipeTrigger by viewModel.resetSwipeAnimation.collectAsState()

    // Цвета в стиле экрана счётчика — кэшируем чтобы не создавать при каждой recomposition
    val backgroundGradient = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF8F4E8),
                Color(0xFFE8DCC8),
                Color(0xFFD4C4A8)
            )
        )
    }
    val cardColor = remember { Color(0xFFFAF6F0) }
    val textColor = remember { Color(0xFF3D2914) }
    val secondaryTextColor = remember { Color(0xFF8B7355) }
    val accentColor = remember { Color(0xFFD4A574) }

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
                // Заголовок
                Text(
                    text = "Цели",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(top = 16.dp, bottom = 20.dp)
                )

                // Карточка "Новый список"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardColor)
                        .clickable {
                            if (!isAddingNewList) viewModel.toggleAddingNewList()
                        }
                        .padding(16.dp)
                ) {
                    if (isAddingNewList) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
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
                                            newListTitle = TextFieldValue("", selection = TextRange(0))
                                            viewModel.toggleAddingNewList()
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 40.dp)
                                    .focusRequester(focusRequesterNewList),
                                cursorBrush = SolidColor(accentColor)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(accentColor)
                                    .clickable {
                                        if (newListTitle.text.isNotBlank()) {
                                            viewModel.addNewList(newListTitle.text)
                                            newListTitle = TextFieldValue("", selection = TextRange(0))
                                        }
                                        viewModel.toggleAddingNewList()
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
                                text = "Новый список",
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

                // Список целей
                LazyColumn {
                    itemsIndexed(goalLists, key = { _, item -> item.id }) { index, goalList ->
                        GoalListItem(
                            goalList = goalList,
                            onItemClick = { viewModel.selectGoalList(goalList) },
                            onEditClick = { editingListId = goalList.id },
                            onDeleteClick = { viewModel.deleteList(goalList.id) },
                            editingId = editingListId,
                            onSaveEdit = { newTitle ->
                                viewModel.updateListTitle(goalList.id, newTitle)
                                editingListId = null
                            },
                            onCancelEdit = { editingListId = null },
                            cardColor = cardColor,
                            textColor = textColor,
                            secondaryTextColor = secondaryTextColor,
                            accentColor = accentColor,
                            resetSwipeTrigger = resetSwipeTrigger
                        )

                        if (index < goalLists.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
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
                backgroundGradient = backgroundGradient,
                cardColor = cardColor,
                textColor = textColor,
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor
            )
        }
    }
}