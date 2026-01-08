package com.tta.todolistainew.feature.task.domain.model

/**
 * Domain model representing a Task.
 * This is a clean model used in the domain and UI layers,
 * independent of any data layer implementations.
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null
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
