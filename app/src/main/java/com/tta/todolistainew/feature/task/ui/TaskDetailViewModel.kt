package com.tta.todolistainew.feature.task.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tta.todolistainew.core.common.UiEvent
import com.tta.todolistainew.feature.task.domain.model.Task
import com.tta.todolistainew.feature.task.domain.repository.TaskRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.ZoneId

data class TaskDetailUiState(
    val isLoading: Boolean = true,
    val task: Task? = null,
    val title: String = "",
    val description: String = "",
    val dueDate: LocalDate? = null,
    val hasNotification: Boolean = false,
    val notificationTime: LocalTime? = null,
    val isCompleted: Boolean = false
)

class TaskDetailViewModel(
    private val taskId: Long,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId).first()
            if (task != null) {
                val dueDate = task.dueDate?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                }
                
                val notifTime = task.timeNotification?.let {
                     Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime()
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        task = task,
                        title = task.title,
                        description = task.description,
                        dueDate = dueDate,
                        hasNotification = task.hasNotification,
                        notificationTime = notifTime,
                        isCompleted = task.isCompleted
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.ShowToast("Task not found"))
                _uiEvent.send(UiEvent.NavigateBack)
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateDueDate(date: LocalDate?) {
        _uiState.update { it.copy(dueDate = date) }
    }
    
    fun toggleNotification(enabled: Boolean) {
        _uiState.update { it.copy(hasNotification = enabled) }
    }
    
    fun updateNotificationTime(time: LocalTime?) {
        _uiState.update { it.copy(notificationTime = time) }
    }
    
    fun toggleCompletion(completed: Boolean) {
        _uiState.update { it.copy(isCompleted = completed) }
    }

    fun saveTask() {
        viewModelScope.launch {
            val state = _uiState.value
            val task = state.task ?: return@launch

            val dueDateMillis = state.dueDate?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()?.toEpochMilli()

            var notifTimeMillis: Long? = null
            if (state.hasNotification && state.dueDate != null) {
                // If notification time is set, use it with due date
                if (state.notificationTime != null) {
                    notifTimeMillis = LocalDateTime.of(state.dueDate, state.notificationTime)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                }
                // If notificationTime is NULL but hasNotification is TRUE, 
                // we leave notifTimeMillis as NULL, allowing the Scheduler/Repo to default to "5 mins before".
            }

            val updatedTask = task.copy(
                title = state.title,
                description = state.description,
                dueDate = dueDateMillis,
                hasNotification = state.hasNotification,
                timeNotification = notifTimeMillis,
                isCompleted = state.isCompleted
            )

            taskRepository.updateTask(updatedTask)
            _uiEvent.send(UiEvent.ShowToast("Task updated"))
            _uiEvent.send(UiEvent.NavigateBack)
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            val task = _uiState.value.task ?: return@launch
            taskRepository.deleteTask(task)
            _uiEvent.send(UiEvent.ShowToast("Task deleted"))
            _uiEvent.send(UiEvent.NavigateBack)
        }
    }
    
    class Factory(
        private val taskId: Long,
        private val taskRepository: TaskRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
                return TaskDetailViewModel(taskId, taskRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
