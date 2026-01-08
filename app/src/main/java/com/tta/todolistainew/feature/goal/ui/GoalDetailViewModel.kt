package com.tta.todolistainew.feature.goal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tta.todolistainew.core.common.UiEvent
import com.tta.todolistainew.feature.goal.domain.repository.GoalRepository
import com.tta.todolistainew.feature.task.data.local.TaskType
import com.tta.todolistainew.feature.task.domain.model.Task
import com.tta.todolistainew.feature.task.domain.repository.TaskRepository
import com.tta.todolistainew.navigation.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Goal Detail screen.
 * Fetches goal details and associated tasks.
 * Refactored to take goalId directly for simpler manual DI.
 */
class GoalDetailViewModel(
    private val goalId: Long,
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GoalDetailUiState())
    val uiState: StateFlow<GoalDetailUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()
    
    init {
        loadGoalDetails()
    }
    
    private fun loadGoalDetails() {
        viewModelScope.launch {
            val goalFlow = goalRepository.getGoalById(goalId)
            val tasksFlow = taskRepository.getTasksByGoalId(goalId)
            
            combine(goalFlow, tasksFlow) { goal, tasks ->
                GoalDetailUiState(
                    isLoading = false,
                    goal = goal,
                    tasks = tasks,
                    errorMessage = if (goal == null) "Goal not found" else null
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
    
    fun addTaskToGoal(title: String, description: String) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                taskType = TaskType.GOAL,
                goalId = goalId,
                dueDate = _uiState.value.goal?.targetDate?.toEpochDay()?.times(86400000) // Default to goal target date if available
            )
            taskRepository.addTask(task)
            _uiEvent.send(UiEvent.ShowToast("Task added to goal"))
        }
    }
    
    fun onTaskClick(taskId: Long) {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.Navigate(Route.TaskDetail(taskId)))
        }
    }
    
    fun toggleTaskCompletion(taskId: Long) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(taskId)
        }
    }
    
    fun navigateBack() {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.NavigateBack)
        }
    }
    
    class Factory(
        private val goalId: Long,
        private val goalRepository: GoalRepository,
        private val taskRepository: TaskRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GoalDetailViewModel::class.java)) {
                return GoalDetailViewModel(
                    goalId,
                    goalRepository,
                    taskRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
