package com.tta.todolistainew.feature.goal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Room Entity representing a Goal in the local database.
 * Goals contain multiple tasks and track overall progress.
 */
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    
    // ===== Core info =====
    val title: String,
    val description: String? = null,
    
    // ===== Progress =====
    val isCompleted: Boolean = false,
    val completedAt: LocalDate? = null,
    
    // ===== Time =====
    val startDate: LocalDate? = null,
    val targetDate: LocalDate? = null,
    
    // ===== UI =====
    val color: Int? = null,
    val icon: Int? = null,
    
    // ===== Metadata =====
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
