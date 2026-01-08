package com.tta.todolistainew.feature.home.ui

import com.tta.todolistainew.feature.goal.domain.model.Goal

/**
 * UI State for the Home screen.
 * Contains data for Daily Tasks, Quick Tasks, and Goals.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    
    // Daily Tasks Stats
    val dailyTasksTotal: Int = 0,
    val dailyTasksCompleted: Int = 0,
    
    // Quick Tasks Stats
    val quickTasksTotal: Int = 0,
    val quickTasksCompleted: Int = 0,
    
    // Goals List with stats
    // We'll wrap Goal with its stats in a data class later if needed, 
    // or request them separately. For now, let's keep it simple.
    // Actually, we need stats per goal displayed on the home screen card.
    val goals: List<GoalWithProgress> = emptyList(),
    
    val errorMessage: String? = null
)

/**
 * Helper class to hold a Goal and its progress stats.
 */
data class GoalWithProgress(
    val goal: Goal,
    val totalTasks: Int,
    val completedTasks: Int
)
