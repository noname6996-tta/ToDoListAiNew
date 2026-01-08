package com.tta.todolistainew.feature.goal.data.mapper

import com.tta.todolistainew.feature.goal.data.local.GoalEntity
import com.tta.todolistainew.feature.goal.domain.model.Goal

/**
 * Extension functions for mapping between GoalEntity and Goal domain model.
 */

fun GoalEntity.toDomain(): Goal {
    return Goal(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        completedAt = completedAt,
        startDate = startDate,
        targetDate = targetDate,
        color = color,
        icon = icon,
        createdAt = createdAt
    )
}

fun Goal.toEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        completedAt = completedAt,
        startDate = startDate,
        targetDate = targetDate,
        color = color,
        icon = icon,
        createdAt = createdAt,
        isDeleted = false
    )
}

fun List<GoalEntity>.toDomainList(): List<Goal> {
    return map { it.toDomain() }
}
