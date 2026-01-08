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
     * Returns a Flow to observe changes in real-time.
     */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get a single task by its ID.
     * Returns a Flow to observe changes in real-time.
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
    
    /**
     * Insert a new task. If a task with the same ID exists, it will be replaced.
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
