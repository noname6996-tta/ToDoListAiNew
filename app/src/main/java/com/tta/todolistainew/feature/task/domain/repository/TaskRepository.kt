package com.tta.todolistainew.feature.task.domain.repository

import com.tta.todolistainew.feature.task.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Task operations.
 * Defined in the domain layer to maintain clean architecture principles.
 * Implementation resides in the data layer.
 */
interface TaskRepository {
    
    /**
     * Get all tasks as a Flow for reactive updates.
     */
    fun getTasks(): Flow<List<Task>>
    
    /**
     * Get a single task by its ID.
     */
    fun getTaskById(taskId: Long): Flow<Task?>
    
    /**
     * Get all completed tasks.
     */
    fun getCompletedTasks(): Flow<List<Task>>
    
    /**
     * Get all pending (incomplete) tasks.
     */
    fun getPendingTasks(): Flow<List<Task>>
    
    /**
     * Add a new task.
     * @return The ID of the newly created task
     */
    suspend fun addTask(task: Task): Long
    
    /**
     * Update an existing task.
     */
    suspend fun updateTask(task: Task)
    
    /**
     * Delete a task.
     */
    suspend fun deleteTask(task: Task)
    
    /**
     * Delete a task by its ID.
     */
    suspend fun deleteTaskById(taskId: Long)
    
    /**
     * Toggle the completion status of a task.
     */
    suspend fun toggleTaskCompletion(taskId: Long)
    
    /**
     * Delete all completed tasks.
     */
    suspend fun deleteCompletedTasks()
}
