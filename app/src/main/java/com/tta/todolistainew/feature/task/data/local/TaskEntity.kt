package com.tta.todolistainew.feature.task.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a Task in the local database.
 * This is the data layer model that maps directly to the database table.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null
)
