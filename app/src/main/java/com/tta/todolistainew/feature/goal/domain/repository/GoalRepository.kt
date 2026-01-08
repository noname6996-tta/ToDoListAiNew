package com.tta.todolistainew.feature.goal.domain.repository

import com.tta.todolistainew.feature.goal.domain.model.Goal
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Goal operations.
 */
interface GoalRepository {
    
    /**
     * Get all active goals.
     */
    fun getGoals(): Flow<List<Goal>>
    
    /**
     * Get a single goal by its ID.
     */
    fun getGoalById(goalId: Long): Flow<Goal?>
    
    /**
     * Add a new goal.
     * @return The ID of the newly created goal
     */
    suspend fun addGoal(goal: Goal): Long
    
    /**
     * Update an existing goal.
     */
    suspend fun updateGoal(goal: Goal)
    
    /**
     * Soft delete a goal.
     */
    suspend fun deleteGoal(goalId: Long)
    
    /**
     * Toggle the completion status of a goal.
     */
    suspend fun toggleGoalCompletion(goalId: Long)
}
