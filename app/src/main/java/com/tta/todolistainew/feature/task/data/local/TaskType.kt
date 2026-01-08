package com.tta.todolistainew.feature.task.data.local

/**
 * Enum representing the type of task.
 */
enum class TaskType {
    /**
     * Daily task or habit - recurring tasks
     */
    DAILY,
    
    /**
     * Quick one-time task
     */
    QUICK,
    
    /**
     * Goal-based task - linked to a specific goal
     */
    GOAL
}
