package com.tta.todolistainew.feature.goal.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Goal operations.
 * All read operations return Flow to enable reactive data streams.
 */
@Dao
interface GoalDao {
    
    /**
     * Get all non-deleted goals ordered by creation date (newest first).
     */
    @Query("SELECT * FROM goals WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>
    
    /**
     * Get a single goal by its ID.
     */
    @Query("SELECT * FROM goals WHERE id = :goalId AND isDeleted = 0")
    fun getGoalById(goalId: Long): Flow<GoalEntity?>
    
    /**
     * Get all completed goals.
     */
    @Query("SELECT * FROM goals WHERE isCompleted = 1 AND isDeleted = 0 ORDER BY completedAt DESC")
    fun getCompletedGoals(): Flow<List<GoalEntity>>
    
    /**
     * Get all active (incomplete) goals.
     */
    @Query("SELECT * FROM goals WHERE isCompleted = 0 AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getActiveGoals(): Flow<List<GoalEntity>>
    
    /**
     * Insert a new goal.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long
    
    /**
     * Update an existing goal.
     */
    @Update
    suspend fun updateGoal(goal: GoalEntity)
    
    /**
     * Soft delete a goal (sets isDeleted = true).
     */
    @Query("UPDATE goals SET isDeleted = 1 WHERE id = :goalId")
    suspend fun softDeleteGoal(goalId: Long)
    
    /**
     * Hard delete a goal.
     */
    @Delete
    suspend fun deleteGoal(goal: GoalEntity)
    
    /**
     * Get count of all active goals.
     */
    @Query("SELECT COUNT(*) FROM goals WHERE isDeleted = 0")
    fun getGoalCount(): Flow<Int>
}
