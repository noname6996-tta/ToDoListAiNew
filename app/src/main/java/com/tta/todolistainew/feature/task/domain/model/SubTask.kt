package com.tta.todolistainew.feature.task.domain.model

/**
 * Domain model representing a SubTask.
 */
data class SubTask(
    val id: Int = 0,
    val taskId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
