package com.tta.todolistainew.feature.task.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity representing a SubTask within a Task.
 * SubTasks are smaller steps within a parent task.
 */
@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId")]
)
data class SubTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    // FK -> TaskEntity.id
    val taskId: Long,
    
    val title: String,
    
    val isCompleted: Boolean = false,
    
    // To sort subtasks
    val orderIndex: Int = 0,
    
    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
