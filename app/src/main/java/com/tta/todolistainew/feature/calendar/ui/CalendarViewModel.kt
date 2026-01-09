package com.tta.todolistainew.feature.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tta.todolistainew.feature.task.domain.model.Task
import com.tta.todolistainew.feature.task.domain.repository.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    // View Mode: Month or Week
    private val _isMonthView = MutableStateFlow(true)
    val isMonthView: StateFlow<Boolean> = _isMonthView.asStateFlow()

    // Selected Date
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Tasks for selected date
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForSelectedDate: StateFlow<List<Task>> = _selectedDate
        .flatMapLatest { date ->
            taskRepository.getTasksForDate(date)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Split tasks into ToDo and Completed
    val todoTasks: StateFlow<List<Task>> = tasksForSelectedDate
        .map { tasks -> tasks.filter { !it.isCompleted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTasks: StateFlow<List<Task>> = tasksForSelectedDate
        .map { tasks -> tasks.filter { it.isCompleted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleViewMode() {
        _isMonthView.value = !_isMonthView.value
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onTaskClick(taskId: Long) {
        // Handle task click (navigation usually handled by UI via callback)
    }

    fun onTaskCheckChanged(taskId: Long) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(taskId)
        }
    }

    class Factory(private val taskRepository: TaskRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                return CalendarViewModel(taskRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
