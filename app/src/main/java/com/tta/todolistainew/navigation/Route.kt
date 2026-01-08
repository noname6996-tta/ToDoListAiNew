package com.tta.todolistainew.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe route definitions for Navigation Compose.
 * Uses Kotlin serialization for type-safe navigation arguments.
 */
@Serializable
sealed class Route {
    
    /**
     * Route for the login screen.
     */
    @Serializable
    data object Login : Route()
    
    /**
     * Route for the task list screen (home screen).
     */
    @Serializable
    data object TaskList : Route()
    
    /**
     * Route for viewing/editing a specific task.
     * @param taskId The ID of the task to view/edit
     */
    @Serializable
    data class TaskDetail(val taskId: Long) : Route()
}
