package com.tta.todolistainew.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tta.todolistainew.core.common.Resource
import com.tta.todolistainew.core.common.UiEvent
import com.tta.todolistainew.feature.goal.domain.model.Goal
import com.tta.todolistainew.feature.goal.domain.repository.GoalRepository
import com.tta.todolistainew.feature.goal.domain.usecase.GetGoalsUseCase
import com.tta.todolistainew.feature.task.data.local.TaskType
import com.tta.todolistainew.feature.task.domain.model.Task
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
import java.time.LocalDate

/**
 * ViewModel for the Home screen.
 */
class HomeViewModel(
    private val getTasksByTypeUseCase: GetTasksByTypeUseCase,
    private val getGoalsUseCase: GetGoalsUseCase,
    private val taskRepository: TaskRepository,
    private val goalRepository: GoalRepository // Added GoalRepository
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
        viewModelScope.launch {
            _uiEvent.send(UiEvent.ShowToast("Daily Tasks List - Coming Soon"))
        }
    }
    
    fun onQuickTasksClick() {
        viewModelScope.launch {
             _uiEvent.send(UiEvent.ShowToast("Quick Tasks List - Coming Soon"))
        }
    }
    
    fun onGoalClick(goalId: Long) {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.Navigate(Route.GoalDetail(goalId)))
        }
    }
    
    /**
     * Adds a new task of a specific type.
     */
    fun addTask(
        title: String, 
        description: String, 
        type: TaskType, 
        dueDate: LocalDate? = null,
        hasNotification: Boolean = false,
        notificationTime: Long? = null
    ) {
        viewModelScope.launch {
            val dueDateMillis = dueDate?.atStartOfDay(java.time.ZoneId.systemDefault())
                ?.toInstant()?.toEpochMilli()

            val task = Task(
                title = title,
                description = description,
                taskType = type,
                dueDate = dueDateMillis,
                hasNotification = hasNotification,
                timeNotification = notificationTime
            )
            taskRepository.addTask(task)
            _uiEvent.send(UiEvent.ShowToast("Task added"))
        }
    }
    
    /**
     * Adds a new goal.
     */
    fun addGoal(title: String, description: String, targetDate: LocalDate? = null) {
        viewModelScope.launch {
            val goal = Goal(
                title = title,
                description = description,
                targetDate = targetDate,
                startDate = LocalDate.now()
            )
            goalRepository.addGoal(goal)
            _uiEvent.send(UiEvent.ShowToast("Goal created"))
        }
    }
    
    class Factory(
        private val getTasksByTypeUseCase: GetTasksByTypeUseCase,
        private val getGoalsUseCase: GetGoalsUseCase,
        private val taskRepository: TaskRepository,
        private val goalRepository: GoalRepository // Update factory
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(
                    getTasksByTypeUseCase,
                    getGoalsUseCase,
                    taskRepository,
                    goalRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
