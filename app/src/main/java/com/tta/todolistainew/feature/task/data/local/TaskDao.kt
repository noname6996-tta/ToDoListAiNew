package com.tta.todolistainew.feature.task.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Task operations.
 * All read operations return Flow to enable reactive data streams.
 */
@Dao
interface TaskDao {
    
    /**
     * Get all tasks ordered by creation date (newest first).
     */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get a single task by its ID.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<TaskEntity?>
    
    /**
     * Get all completed tasks.
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY createdAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get all pending (incomplete) tasks.
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getPendingTasks(): Flow<List<TaskEntity>>
    
    // ===== Task Type Queries =====
    
    /**
     * Get all tasks of a specific type.
     */
    @Query("SELECT * FROM tasks WHERE taskType = :taskType ORDER BY createdAt DESC")
    fun getTasksByType(taskType: TaskType): Flow<List<TaskEntity>>
    
    /**
     * Get DAILY tasks.
     */
    @Query("SELECT * FROM tasks WHERE taskType = 'DAILY' ORDER BY createdAt DESC")
    fun getDailyTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get QUICK tasks.
     */
    @Query("SELECT * FROM tasks WHERE taskType = 'QUICK' ORDER BY createdAt DESC")
    fun getQuickTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get GOAL tasks for a specific goal.
     */
    @Query("SELECT * FROM tasks WHERE taskType = 'GOAL' AND goalId = :goalId ORDER BY createdAt DESC")
    fun getTasksByGoalId(goalId: Long): Flow<List<TaskEntity>>
    
    // ===== Count Queries =====
    
    /**
     * Get count of completed tasks by type.
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE taskType = :taskType AND isCompleted = 1")
    fun getCompletedCountByType(taskType: TaskType): Flow<Int>
    
    /**
     * Get total count of tasks by type.
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE taskType = :taskType")
    fun getTotalCountByType(taskType: TaskType): Flow<Int>
    
    /**
     * Get count of completed tasks for a goal.
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE goalId = :goalId AND isCompleted = 1")
    fun getCompletedCountByGoal(goalId: Long): Flow<Int>
    
    /**
     * Get total count of tasks for a goal.
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE goalId = :goalId")
    fun getTotalCountByGoal(goalId: Long): Flow<Int>
    
    // ===== CRUD Operations =====
    
    /**
     * Insert a new task.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long
    
    /**
     * Insert multiple tasks at once.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)
    
    /**
     * Update an existing task.
     */
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    /**
     * Delete a task.
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    /**
     * Delete a task by its ID.
     */
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)
    
    /**
     * Delete all completed tasks.
     */
    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()
    
    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
    
    /**
     * Get the count of all tasks.
     */
    @Query("SELECT COUNT(*) FROM tasks")
    fun getTaskCount(): Flow<Int>
    
    /**
     * Get the count of completed tasks.
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTaskCount(): Flow<Int>
}
