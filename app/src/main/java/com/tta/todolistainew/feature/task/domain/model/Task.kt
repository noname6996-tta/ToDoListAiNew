package com.tta.todolistainew.feature.task.domain.model

import com.tta.todolistainew.feature.task.data.local.TaskType
import java.time.LocalDate

/**
 * Domain model representing a Task.
 * This is a clean model used in the domain and UI layers,
 * independent of any data layer implementations.
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,

    // ===== Status =====
    var isCompleted: Boolean = false,
    var isDeleted: Boolean = false,
    var deletedAt: LocalDate? = null,

    // ===== TaskType =====
    val taskType: TaskType = TaskType.QUICK,
    val goalId: Long? = null,

    // ===== Notification =====
    var hasNotification: Boolean = false,
    var timeNotification: Long? = null,
    var repeatNotification: Int? = null
) {
    /**
     * Returns true if the task has a due date set.
     */
    val hasDueDate: Boolean get() = dueDate != null
    
    /**
     * Returns true if the task is overdue (past due date and not completed).
     */
    val isOverdue: Boolean
        get() = dueDate != null && 
                dueDate < System.currentTimeMillis() && 
                !isCompleted
}
