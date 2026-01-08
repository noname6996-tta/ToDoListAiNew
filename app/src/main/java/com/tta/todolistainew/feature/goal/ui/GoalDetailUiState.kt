package com.tta.todolistainew.feature.goal.ui

import com.tta.todolistainew.feature.goal.domain.model.Goal
import com.tta.todolistainew.feature.task.domain.model.Task

/**
 * UI State for Goal Detail screen.
 */
data class GoalDetailUiState(
    val isLoading: Boolean = true,
    val goal: Goal? = null,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null
)
