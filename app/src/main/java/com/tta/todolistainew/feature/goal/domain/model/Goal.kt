package com.tta.todolistainew.feature.goal.domain.model

import java.time.LocalDate

/**
 * Domain model representing a Goal.
 */
data class Goal(
    val id: Long = 0L,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val completedAt: LocalDate? = null,
    val startDate: LocalDate? = null,
    val targetDate: LocalDate? = null,
    val color: Int? = null,
    val icon: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Helper to check if the goal is active (not completed).
     */
    val isActive: Boolean get() = !isCompleted
    
    /**
     * Helper to check if the goal is overdue.
     */
    val isOverdue: Boolean
        get() = targetDate != null && 
                targetDate.isBefore(LocalDate.now()) && 
                !isCompleted
}
