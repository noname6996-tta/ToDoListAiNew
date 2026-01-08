package com.tta.todolistainew.feature.task.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tta.todolistainew.core.common.Resource
import com.tta.todolistainew.core.common.UiEvent
import com.tta.todolistainew.feature.task.domain.model.Task
import com.tta.todolistainew.feature.task.domain.repository.TaskRepository
import com.tta.todolistainew.feature.task.domain.usecase.GetTasksUseCase
import com.tta.todolistainew.navigation.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Task List screen.
 * Exposes UI state via StateFlow and one-time events via Channel.
 */
class TaskListViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    // UI State exposed as StateFlow
    private val _uiState = MutableStateFlow(TaskListUiState.Loading)
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()
    
    // One-time UI events exposed as Flow (using Channel for buffering)
    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()
    
    init {
        loadTasks()
    }
    
    /**
     * Load all tasks using the GetTasksUseCase.
     */
    private fun loadTasks() {
        viewModelScope.launch {
            getTasksUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update { 
                            TaskListUiState.success(resource.data)
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            TaskListUiState.error(resource.message)
                        }
                        _uiEvent.send(UiEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }
    
    /**
     * Add a new task.
     */
    fun addTask(title: String, description: String) {
        if (title.isBlank()) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar("Task title cannot be empty"))
            }
            return
        }
        
        viewModelScope.launch {
            try {
                val task = Task(
                    title = title.trim(),
                    description = description.trim()
                )
                taskRepository.addTask(task)
                _uiEvent.send(UiEvent.ShowSnackbar("Task added successfully"))
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowError(e.message ?: "Failed to add task"))
            }
        }
    }
    
    /**
     * Toggle task completion status.
     */
    fun toggleTaskCompletion(taskId: Long) {
        viewModelScope.launch {
            try {
                taskRepository.toggleTaskCompletion(taskId)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowError(e.message ?: "Failed to update task"))
            }
        }
    }
    
    /**
     * Delete a task.
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(task)
                _uiEvent.send(UiEvent.ShowSnackbar("Task deleted"))
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowError(e.message ?: "Failed to delete task"))
            }
        }
    }
    
    /**
     * Navigate to task detail screen.
     */
    fun onTaskClick(taskId: Long) {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.Navigate(Route.TaskDetail(taskId)))
        }
    }
    
    /**
     * Retry loading tasks after an error.
     */
    fun retry() {
        loadTasks()
    }
    
    /**
     * Factory for creating TaskListViewModel with dependencies.
     */
    class Factory(
        private val getTasksUseCase: GetTasksUseCase,
        private val taskRepository: TaskRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
                return TaskListViewModel(getTasksUseCase, taskRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
