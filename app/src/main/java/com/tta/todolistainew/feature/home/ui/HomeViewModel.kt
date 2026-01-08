package com.tta.todolistainew.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tta.todolistainew.core.common.Resource
import com.tta.todolistainew.core.common.UiEvent
import com.tta.todolistainew.feature.goal.domain.usecase.GetGoalsUseCase
import com.tta.todolistainew.feature.task.data.local.TaskType
import com.tta.todolistainew.feature.task.domain.repository.TaskRepository
import com.tta.todolistainew.feature.task.domain.usecase.GetTasksByTypeUseCase
import com.tta.todolistainew.navigation.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen.
 */
class HomeViewModel(
    private val getTasksByTypeUseCase: GetTasksByTypeUseCase, // We might not need this if we only show stats
    private val getGoalsUseCase: GetGoalsUseCase,
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            // Combine flows for real-time updates
            val dailyTotalFlow = taskRepository.getTotalCountByType(TaskType.DAILY)
            val dailyCompletedFlow = taskRepository.getCompletedCountByType(TaskType.DAILY)
            
            val quickTotalFlow = taskRepository.getTotalCountByType(TaskType.QUICK)
            val quickCompletedFlow = taskRepository.getCompletedCountByType(TaskType.QUICK)
            
            val goalsFlow = getGoalsUseCase()
            
            // Combine all flows
            combine(
                dailyTotalFlow,
                dailyCompletedFlow,
                quickTotalFlow,
                quickCompletedFlow,
                goalsFlow
            ) { dailyTotal, dailyCompleted, quickTotal, quickCompleted, goalsResource ->
                
                var goalsWithProgress = emptyList<GoalWithProgress>()
                var isLoading = true
                var error: String? = null
                
                if (goalsResource is Resource.Success) {
                    isLoading = false
                    // For each goal, fetch its stats. 
                    // Note: This is efficient enough for small number of active goals.
                    // Ideally, we'd have a use case returning combined data or a Room relation.
                    goalsWithProgress = goalsResource.data.map { goal ->
                        val total = taskRepository.getTotalCountByGoal(goal.id).first()
                        val completed = taskRepository.getCompletedCountByGoal(goal.id).first()
                        GoalWithProgress(goal, total, completed)
                    }
                } else if (goalsResource is Resource.Error) {
                    isLoading = false
                    error = goalsResource.message
                }
                
                HomeUiState(
                    isLoading = isLoading,
                    dailyTasksTotal = dailyTotal,
                    dailyTasksCompleted = dailyCompleted,
                    quickTasksTotal = quickTotal,
                    quickTasksCompleted = quickCompleted,
                    goals = goalsWithProgress,
                    errorMessage = error
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
    
    fun onDailyTasksClick() {
        // Navigate to daily tasks list
        // TODO: Implement navigation
        viewModelScope.launch {
            _uiEvent.send(UiEvent.ShowToast("Daily Tasks clicked"))
        }
    }
    
    fun onQuickTasksClick() {
        // Navigate to quick tasks list
        // TODO: Implement navigation
        viewModelScope.launch {
             _uiEvent.send(UiEvent.ShowToast("Quick Tasks clicked"))
        }
    }
    
    fun onGoalClick(goalId: Long) {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.Navigate(Route.GoalDetail(goalId)))
        }
    }
    
    fun onAddClick() {
        // Navigate to add new task/goal
        // For now, let's just trigger the bottom sheet in the UI
    }
    
    class Factory(
        private val getTasksByTypeUseCase: GetTasksByTypeUseCase,
        private val getGoalsUseCase: GetGoalsUseCase,
        private val taskRepository: TaskRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(
                    getTasksByTypeUseCase,
                    getGoalsUseCase,
                    taskRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
