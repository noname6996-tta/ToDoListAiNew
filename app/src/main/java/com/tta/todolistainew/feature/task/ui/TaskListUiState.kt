package com.tta.todolistainew.feature.task.ui

import com.tta.todolistainew.feature.task.domain.model.Task

/**
 * Data class representing the UI state for the Task List screen.
 * Follows the single source of truth principle for UI state management.
 */
data class TaskListUiState(
    /**
     * Whether data is currently being loaded.
     */
    val isLoading: Boolean = true,
    
    /**
     * The list of tasks to display.
     */
    val tasks: List<Task> = emptyList(),
    
    /**
     * Error message to display, null if no error.
     */
    val errorMessage: String? = null,
    
    /**
     * Whether the empty state should be shown (no tasks available).
     */
    val isEmpty: Boolean = false
) {
    companion object {
        /**
         * Initial loading state.
         */
        val Loading = TaskListUiState(isLoading = true)
        
        /**
         * Empty state when no tasks are available.
         */
        val Empty = TaskListUiState(isLoading = false, isEmpty = true)
        
        /**
         * Creates a success state with the given tasks.
         */
        fun success(tasks: List<Task>) = TaskListUiState(
            isLoading = false,
            tasks = tasks,
            isEmpty = tasks.isEmpty()
        )
        
        /**
         * Creates an error state with the given message.
         */
        fun error(message: String) = TaskListUiState(
            isLoading = false,
            errorMessage = message
        )
    }
}
