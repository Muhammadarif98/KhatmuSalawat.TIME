package com.example.khatmusalawattime.presentation.ui.notes

import android.util.Log
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
import com.example.khatmusalawattime.domain.repository.CounterGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NotesViewModel"

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllGoalListsUseCase: GetAllGoalListsUseCase,
    private val addGoalListUseCase: AddGoalListUseCase,
    private val updateGoalListTitleUseCase: UpdateGoalListTitleUseCase,
    private val deleteGoalListUseCase: DeleteGoalListUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val counterGoalRepository: CounterGoalRepository
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

                // Синхронизируем selectedGoalList с актуальными данными из БД
                _selectedGoalList.value?.let { selected ->
                    _selectedGoalList.value = lists.find { it.id == selected.id }
                }
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
                _isAddingNewList.value = false
                triggerSwipeReset()

                // Сохраняем в БД — Flow автоматически обновит _goalLists
                addGoalListUseCase(title)
            } catch (e: Exception) {
                _isAddingNewList.value = false
                triggerSwipeReset()
            }
        }
    }

    fun selectGoalList(goalList: GoalList?) {
        if (goalList == null) {
            _selectedGoalList.value = null
            return
        }

        // Находим актуальную версию списка из _goalLists по ID
        val freshGoalList = _goalLists.value.find { it.id == goalList.id }
        _selectedGoalList.value = freshGoalList ?: goalList
    }

    fun addNewTask(title: String) {
        if (title.isBlank()) return

        _selectedGoalList.value?.let { currentList ->
            viewModelScope.launch {
                try {
                    // Оптимистичное обновление UI
                    val newTask = com.example.khatmusalawattime.domain.model.Task(
                        id = java.util.UUID.randomUUID().toString(),
                        title = title,
                        isCompleted = false,
                        createdAt = java.util.Date(),
                        updatedAt = java.util.Date()
                    )
                    val optimisticList = currentList.copy(tasks = currentList.tasks + newTask)
                    _selectedGoalList.value = optimisticList
                    _isAddingNewTask.value = false
                    triggerSwipeReset()

                    // Сохраняем в БД — Flow автоматически обновит данные
                    addTaskUseCase(title, currentList)
                } catch (e: Exception) {
                    _isAddingNewTask.value = false
                    triggerSwipeReset()
                }
            }
        }
    }

    fun toggleTaskCompletion(taskId: String) {
        _selectedGoalList.value?.let { currentList ->
            viewModelScope.launch {
                try {
                    // Найти задачу чтобы узнать её текущий статус и linkedGoalId
                    val task = currentList.tasks.find { it.id == taskId }
                    val newCompletedState = !(task?.isCompleted ?: false)
                    val linkedGoalId = task?.linkedGoalId

                    // Оптимистичное обновление UI (немедленное)
                    val updatedTasks = currentList.tasks.map { t ->
                        if (t.id == taskId) t.copy(isCompleted = newCompletedState)
                        else t
                    }
                    val optimisticList = currentList.copy(tasks = updatedTasks)
                    _selectedGoalList.value = optimisticList
                    triggerSwipeReset()

                    // Сохраняем в БД — Flow автоматически обновит _goalLists и _selectedGoalList
                    toggleTaskCompletionUseCase(taskId, currentList)

                    // Синхронизируем с целями счётчика если есть связь
                    if (linkedGoalId != null) {
                        try {
                            if (newCompletedState) {
                                counterGoalRepository.completeGoal(linkedGoalId)
                            }
                            // Если задачу размечают как незавершённую,
                            // цель не сбрасываем — это нелогично
                        } catch (e: Exception) {
                            Log.e(TAG, "Ошибка синхронизации с целью: ${e.message}")
                        }
                    }
                } catch (e: Exception) {
                    // В случае ошибки Flow восстановит данные из БД
                    triggerSwipeReset()
                }
            }
        }
    }

    fun updateTaskTitle(taskId: String, newTitle: String) {
        if (newTitle.isBlank()) return

        _selectedGoalList.value?.let { currentList ->
            viewModelScope.launch {
                try {
                    // Оптимистичное обновление UI
                    val updatedTasks = currentList.tasks.map { task ->
                        if (task.id == taskId) task.copy(title = newTitle)
                        else task
                    }
                    _selectedGoalList.value = currentList.copy(tasks = updatedTasks)
                    triggerSwipeReset()

                    // Сохраняем в БД — Flow автоматически обновит данные
                    updateTaskTitleUseCase(taskId, newTitle, currentList)
                } catch (e: Exception) {
                    triggerSwipeReset()
                }
            }
        }
    }

    fun deleteTask(taskId: String) {
        _selectedGoalList.value?.let { currentList ->
            viewModelScope.launch {
                try {
                    // Оптимистичное обновление UI
                    val updatedTasks = currentList.tasks.filter { it.id != taskId }
                    _selectedGoalList.value = currentList.copy(tasks = updatedTasks)
                    triggerSwipeReset()

                    // Удаляем из БД — Flow автоматически обновит данные
                    deleteTaskUseCase(taskId, currentList)
                } catch (e: Exception) {
                    triggerSwipeReset()
                }
            }
        }
    }

    fun updateListTitle(listId: String, newTitle: String) {
        if (newTitle.isBlank()) return

        viewModelScope.launch {
            try {
                // Оптимистичное обновление UI
                _selectedGoalList.value?.let { currentList ->
                    if (currentList.id == listId) {
                        _selectedGoalList.value = currentList.copy(title = newTitle)
                    }
                }
                triggerSwipeReset()

                // Сохраняем в БД — Flow автоматически обновит данные
                updateGoalListTitleUseCase(listId, newTitle)
            } catch (e: Exception) {
                triggerSwipeReset()
            }
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            try {
                // Оптимистичное обновление UI
                if (_selectedGoalList.value?.id == listId) {
                    _selectedGoalList.value = null
                }
                triggerSwipeReset()

                // Удаляем из БД — Flow автоматически обновит _goalLists
                deleteGoalListUseCase(listId)
            } catch (e: Exception) {
                triggerSwipeReset()
            }
        }
    }
} 