package com.example.khatmusalawattime.presentation.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khatmusalawattime.domain.model.GoalList
import com.example.khatmusalawattime.domain.notes.usecase.AddGoalListUseCase
import com.example.khatmusalawattime.domain.notes.usecase.AddTaskUseCase
import com.example.khatmusalawattime.domain.notes.usecase.DeleteGoalListUseCase
import com.example.khatmusalawattime.domain.notes.usecase.DeleteTaskUseCase
import com.example.khatmusalawattime.domain.notes.usecase.GetAllGoalListsUseCase
import com.example.khatmusalawattime.domain.notes.usecase.ToggleTaskCompletionUseCase
import com.example.khatmusalawattime.domain.notes.usecase.UpdateGoalListTitleUseCase
import com.example.khatmusalawattime.domain.notes.usecase.UpdateTaskTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllGoalListsUseCase: GetAllGoalListsUseCase,
    private val addGoalListUseCase: AddGoalListUseCase,
    private val updateGoalListTitleUseCase: UpdateGoalListTitleUseCase,
    private val deleteGoalListUseCase: DeleteGoalListUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _goalLists = MutableStateFlow<List<GoalList>>(emptyList())
    val goalLists: StateFlow<List<GoalList>> = _goalLists

    private val _selectedGoalList = MutableStateFlow<GoalList?>(null)
    val selectedGoalList: StateFlow<GoalList?> = _selectedGoalList

    private val _isAddingNewList = MutableStateFlow(false)
    val isAddingNewList: StateFlow<Boolean> = _isAddingNewList

    private val _isAddingNewTask = MutableStateFlow(false)
    val isAddingNewTask: StateFlow<Boolean> = _isAddingNewTask
    
    // Триггер сброса свайпа
    private val _resetSwipeAnimation = MutableStateFlow(0L)
    val resetSwipeAnimation: StateFlow<Long> = _resetSwipeAnimation

    init {
        viewModelScope.launch {
            getAllGoalListsUseCase().collect { lists ->
                _goalLists.value = lists
            }
        }
    }

    // Функция для триггера сброса свайпа
    fun triggerSwipeReset() {
        _resetSwipeAnimation.value = System.currentTimeMillis()
    }

    fun toggleAddingNewList() {
        _isAddingNewList.value = !_isAddingNewList.value
    }

    fun toggleAddingNewTask() {
        _isAddingNewTask.value = !_isAddingNewTask.value
    }

    fun addNewList(title: String) {
        if (title.isBlank()) return
        
        viewModelScope.launch {
            try {
                addGoalListUseCase(title)
                _isAddingNewList.value = false
            } catch (e: Exception) {
                // Обработка ошибок
            }
        }
    }

    fun selectGoalList(goalList: GoalList?) {
        _selectedGoalList.value = goalList
    }

    fun addNewTask(title: String) {
        if (title.isBlank()) return
        
        _selectedGoalList.value?.let { currentList ->
            viewModelScope.launch {
                try {
                    val updatedList = addTaskUseCase(title, currentList)
                    _selectedGoalList.value = updatedList
                    _isAddingNewTask.value = false
                } catch (e: Exception) {
                    // Обработка ошибок
                }
            }
        }
    }

    fun toggleTaskCompletion(taskId: String) {
        _selectedGoalList.value?.let { currentList ->
            viewModelScope.launch {
                try {
                    val updatedList = toggleTaskCompletionUseCase(taskId, currentList)
                    _selectedGoalList.value = updatedList
                    
                    // Обновляем список в общем состоянии
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == updatedList.id) updatedList
                            else list
                        }
                    }
                } catch (e: Exception) {
                    // Обработка ошибок
                }
            }
        }
    }

    fun updateTaskTitle(taskId: String, newTitle: String) {
        if (newTitle.isBlank()) return
        
        _selectedGoalList.value?.let { currentList ->
            viewModelScope.launch {
                try {
                    val updatedList = updateTaskTitleUseCase(taskId, newTitle, currentList)
                    _selectedGoalList.value = updatedList
                    
                    // Обновляем список в общем состоянии
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == updatedList.id) updatedList
                            else list
                        }
                    }
                } catch (e: Exception) {
                    // Обработка ошибок
                }
            }
        }
    }

    fun deleteTask(taskId: String) {
        _selectedGoalList.value?.let { currentList ->
            viewModelScope.launch {
                try {
                    val updatedList = deleteTaskUseCase(taskId, currentList)
                    _selectedGoalList.value = updatedList
                    
                    // Обновляем список в общем состоянии
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == updatedList.id) updatedList
                            else list
                        }
                    }
                    
                    // Сбрасываем состояние свайпа после удаления
                    triggerSwipeReset()
                } catch (e: Exception) {
                    // Обработка ошибок
                }
            }
        }
    }

    fun updateListTitle(listId: String, newTitle: String) {
        if (newTitle.isBlank()) return
        
        viewModelScope.launch {
            try {
                updateGoalListTitleUseCase(listId, newTitle)
                
                // Если это выбранный список, обновляем его
                _selectedGoalList.value?.let { currentList ->
                    if (currentList.id == listId) {
                        _selectedGoalList.value = currentList.copy(title = newTitle)
                    }
                }
            } catch (e: Exception) {
                // Обработка ошибок
            }
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            try {
                deleteGoalListUseCase(listId)
                
                if (_selectedGoalList.value?.id == listId) {
                    _selectedGoalList.value = null
                }
                
                // Сбрасываем состояние свайпа после удаления
                triggerSwipeReset()
            } catch (e: Exception) {
                // Обработка ошибок
            }
        }
    }
} 