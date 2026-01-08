package com.tta.todolistainew.feature.task.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SubTask operations.
 */
@Dao
interface SubTaskDao {
    
    /**
     * Get all non-deleted subtasks for a specific task, ordered by orderIndex.
     */
    @Query("SELECT * FROM subtasks WHERE taskId = :taskId AND isDeleted = 0 ORDER BY orderIndex ASC")
    fun getSubTasksByTaskId(taskId: Long): Flow<List<SubTaskEntity>>
    
    /**
     * Get a single subtask by its ID.
     */
    @Query("SELECT * FROM subtasks WHERE id = :subTaskId AND isDeleted = 0")
    fun getSubTaskById(subTaskId: Int): Flow<SubTaskEntity?>
    
    /**
     * Insert a new subtask.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: SubTaskEntity): Long
    
    /**
     * Insert multiple subtasks.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTasks(subTasks: List<SubTaskEntity>)
    
    /**
     * Update an existing subtask.
     */
    @Update
    suspend fun updateSubTask(subTask: SubTaskEntity)
    
    /**
     * Soft delete a subtask.
     */
    @Query("UPDATE subtasks SET isDeleted = 1 WHERE id = :subTaskId")
    suspend fun softDeleteSubTask(subTaskId: Int)
    
    /**
     * Hard delete a subtask.
     */
    @Delete
    suspend fun deleteSubTask(subTask: SubTaskEntity)
    
    /**
     * Delete all subtasks for a task.
     */
    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    suspend fun deleteSubTasksByTaskId(taskId: Long)
    
    /**
     * Get count of completed subtasks for a task.
     */
    @Query("SELECT COUNT(*) FROM subtasks WHERE taskId = :taskId AND isCompleted = 1 AND isDeleted = 0")
    fun getCompletedSubTaskCount(taskId: Long): Flow<Int>
    
    /**
     * Get total count of subtasks for a task.
     */
    @Query("SELECT COUNT(*) FROM subtasks WHERE taskId = :taskId AND isDeleted = 0")
    fun getTotalSubTaskCount(taskId: Long): Flow<Int>
}
