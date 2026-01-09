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
     * Route for the home screen (was TaskList).
     */
    @Serializable
    data object Home : Route()
    
    /**
     * Route for the task list screen (legacy/fallback or specific list).
     */
    @Serializable
    data object TaskList : Route()
    
    /**
     * Route for viewing/editing a specific task.
     * @param taskId The ID of the task to view/edit
     */
    @Serializable
    data class TaskDetail(val taskId: Long) : Route()
    
    /**
     * Route for viewing a specific goal.
     */
    @Serializable
    data class GoalDetail(val goalId: Long) : Route()

    @Serializable
    data object UserInfo : Route()

    @Serializable
    data object Settings : Route()

    @Serializable
    data object AboutUs : Route()

    @Serializable
    data object PrivacyPolicy : Route()
}
