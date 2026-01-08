package com.tta.todolistainew.feature.task.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tta.todolistainew.feature.goal.data.local.GoalEntity

/**
 * Room Entity representing a Task in the local database.
 * This is the data layer model that maps directly to the database table.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("goalId")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    
    // Task type: DAILY, QUICK, or GOAL
    val taskType: TaskType = TaskType.QUICK,
    
    // FK -> GoalEntity.id (null if not a GOAL type task)
    val goalId: Long? = null
)
