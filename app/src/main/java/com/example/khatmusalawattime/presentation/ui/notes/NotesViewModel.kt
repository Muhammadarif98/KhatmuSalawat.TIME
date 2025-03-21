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
import kotlinx.coroutines.flow.first
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
                // Создаем новый список локально для оптимистичного обновления
                val newListId = java.util.UUID.randomUUID().toString()
                val currentDate = java.util.Date()
                val newList = com.example.khatmusalawattime.domain.model.GoalList(
                    id = newListId,
                    title = title,
                    isCompleted = false,
                    tasks = emptyList(),
                    createdAt = currentDate,
                    updatedAt = currentDate
                )
                
                // Оптимистично добавляем в общий список
                _goalLists.update { currentLists ->
                    currentLists + newList
                }
                
                // Триггерим первое обновление UI
                triggerSwipeReset()
                
                // Фактическое добавление в базу данных
                addGoalListUseCase(title)
                
                _isAddingNewList.value = false
                
                // Триггерим повторное обновление UI
                triggerSwipeReset()
            } catch (e: Exception) {
                // В случае ошибки восстанавливаем предыдущее состояние через переинициализацию всех списков
                viewModelScope.launch {
                    try {
                        // Получаем только первое значение из потока
                        val lists = getAllGoalListsUseCase().first()
                        _goalLists.value = lists
                        _isAddingNewList.value = false
                        triggerSwipeReset()
                    } catch (e: Exception) {
                        // Обрабатываем возможные ошибки при получении списков
                        _isAddingNewList.value = false
                    }
                }
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
        
        // Триггерим обновление UI после выбора списка
        triggerSwipeReset()
    }

    fun addNewTask(title: String) {
        if (title.isBlank()) return
        
        _selectedGoalList.value?.let { currentList ->
            viewModelScope.launch {
                try {
                    // Создаем новую задачу локально для оптимистичного обновления
                    val newTaskId = java.util.UUID.randomUUID().toString()
                    val currentDate = java.util.Date()
                    val newTask = com.example.khatmusalawattime.domain.model.Task(
                        id = newTaskId,
                        title = title,
                        isCompleted = false,
                        createdAt = currentDate,
                        updatedAt = currentDate
                    )
                    
                    // Создаем обновленный список с новой задачей
                    val optimisticList = currentList.copy(
                        tasks = currentList.tasks + newTask
                    )
                    
                    // Сразу обновляем UI для мгновенной реакции
                    _selectedGoalList.value = optimisticList
                    
                    // Обновляем в общем списке для синхронизации
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == currentList.id) optimisticList
                            else list
                        }
                    }
                    
                    // Триггерим первое обновление UI
                    triggerSwipeReset()
                    
                    // Фактическое добавление в базу данных
                    val updatedList = addTaskUseCase(title, currentList)
                    
                    // Обновляем данные после получения результата из базы
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == updatedList.id) updatedList
                            else list
                        }
                    }
                    
                    // Устанавливаем выбранный список из обновленного общего списка
                    _selectedGoalList.value = _goalLists.value.find { it.id == updatedList.id }
                    
                    _isAddingNewTask.value = false
                    
                    // Триггерим повторное обновление UI
                    triggerSwipeReset()
                } catch (e: Exception) {
                    // В случае ошибки восстанавливаем исходное состояние
                    _selectedGoalList.value = currentList
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == currentList.id) currentList
                            else list
                        }
                    }
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
                    // Оптимистичное обновление UI (немедленное, до выполнения UseCase)
                    val updatedTasks = currentList.tasks.map { task ->
                        if (task.id == taskId) task.copy(isCompleted = !task.isCompleted)
                        else task
                    }
                    val optimisticList = currentList.copy(tasks = updatedTasks)
                    
                    // Сразу обновляем UI для мгновенной реакции
                    _selectedGoalList.value = optimisticList
                    
                    // Обновляем в общем списке для синхронизации
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == currentList.id) optimisticList
                            else list
                        }
                    }
                    
                    // Триггерим первое обновление UI
                    triggerSwipeReset()
                    
                    // Выполняем фактическое обновление в базе данных
                    val updatedList = toggleTaskCompletionUseCase(taskId, currentList)
                    
                    // Обновляем данные после получения результата из базы
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == updatedList.id) updatedList
                            else list
                        }
                    }
                    
                    // Устанавливаем выбранный список из обновленного общего списка
                    _selectedGoalList.value = _goalLists.value.find { it.id == updatedList.id }
                    
                    // Триггерим повторное обновление UI
                    triggerSwipeReset()
                } catch (e: Exception) {
                    // В случае ошибки восстанавливаем исходное состояние
                    _selectedGoalList.value = currentList
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == currentList.id) currentList
                            else list
                        }
                    }
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
                    // Оптимистичное обновление названия задачи
                    val updatedTasks = currentList.tasks.map { task ->
                        if (task.id == taskId) task.copy(title = newTitle)
                        else task
                    }
                    val optimisticList = currentList.copy(tasks = updatedTasks)
                    
                    // Сразу обновляем UI для мгновенной реакции
                    _selectedGoalList.value = optimisticList
                    
                    // Обновляем в общем списке для синхронизации
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == currentList.id) optimisticList
                            else list
                        }
                    }
                    
                    // Триггерим первое обновление UI
                    triggerSwipeReset()
                    
                    // Фактическое обновление в базе данных
                    val updatedList = updateTaskTitleUseCase(taskId, newTitle, currentList)
                    
                    // Обновляем данные после получения результата из базы
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == updatedList.id) updatedList
                            else list
                        }
                    }
                    
                    // Устанавливаем выбранный список из обновленного общего списка
                    _selectedGoalList.value = _goalLists.value.find { it.id == updatedList.id }
                    
                    // Триггерим повторное обновление UI
                    triggerSwipeReset()
                } catch (e: Exception) {
                    // В случае ошибки восстанавливаем исходное состояние
                    _selectedGoalList.value = currentList
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == currentList.id) currentList
                            else list
                        }
                    }
                    triggerSwipeReset()
                }
            }
        }
    }

    fun deleteTask(taskId: String) {
        _selectedGoalList.value?.let { currentList ->
            viewModelScope.launch {
                try {
                    // Оптимистичное удаление задачи из списка
                    val updatedTasks = currentList.tasks.filter { it.id != taskId }
                    val optimisticList = currentList.copy(tasks = updatedTasks)
                    
                    // Сразу обновляем UI для мгновенной реакции
                    _selectedGoalList.value = optimisticList
                    
                    // Обновляем в общем списке для синхронизации
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == currentList.id) optimisticList
                            else list
                        }
                    }
                    
                    // Триггерим первое обновление UI
                    triggerSwipeReset()
                    
                    // Фактическое удаление в базе данных
                    val updatedList = deleteTaskUseCase(taskId, currentList)
                    
                    // Обновляем данные после получения результата из базы
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == updatedList.id) updatedList
                            else list
                        }
                    }
                    
                    // Устанавливаем выбранный список из обновленного общего списка
                    _selectedGoalList.value = _goalLists.value.find { it.id == updatedList.id }
                    
                    // Триггерим повторное обновление UI
                    triggerSwipeReset()
                } catch (e: Exception) {
                    // В случае ошибки восстанавливаем исходное состояние
                    _selectedGoalList.value = currentList
                    _goalLists.update { currentLists ->
                        currentLists.map { list ->
                            if (list.id == currentList.id) currentList
                            else list
                        }
                    }
                    triggerSwipeReset()
                }
            }
        }
    }

    fun updateListTitle(listId: String, newTitle: String) {
        if (newTitle.isBlank()) return
        
        // Сохраняем текущие списки для восстановления в случае ошибки
        val currentLists = _goalLists.value
        val currentSelectedList = _selectedGoalList.value
        
        viewModelScope.launch {
            try {
                // Оптимистичное обновление названия списка
                _goalLists.update { lists ->
                    lists.map { list ->
                        if (list.id == listId) list.copy(title = newTitle)
                        else list
                    }
                }
                
                // Если это выбранный список, обновляем его из _goalLists для синхронизации
                _selectedGoalList.value?.let { currentList ->
                    if (currentList.id == listId) {
                        _selectedGoalList.value = _goalLists.value.find { it.id == listId }
                    }
                }
                
                // Триггерим первое обновление UI
                triggerSwipeReset()
                
                // Фактическое обновление в базе данных
                updateGoalListTitleUseCase(listId, newTitle)
                
                // Триггерим повторное обновление UI после операции с базой
                triggerSwipeReset()
            } catch (e: Exception) {
                // В случае ошибки восстанавливаем исходное состояние
                _goalLists.value = currentLists
                _selectedGoalList.value = currentSelectedList
                triggerSwipeReset()
            }
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            try {
                deleteGoalListUseCase(listId)
                
                // Немедленно удаляем список из общего состояния
                _goalLists.update { currentLists ->
                    currentLists.filter { it.id != listId }
                }
                
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