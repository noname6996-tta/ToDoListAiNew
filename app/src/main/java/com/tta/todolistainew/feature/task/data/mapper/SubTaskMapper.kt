package com.tta.todolistainew.feature.task.data.mapper

import com.tta.todolistainew.feature.task.data.local.SubTaskEntity
import com.tta.todolistainew.feature.task.domain.model.SubTask

/**
 * Extension functions for mapping between SubTaskEntity and SubTask domain model.
 */

fun SubTaskEntity.toDomain(): SubTask {
    return SubTask(
        id = id,
        taskId = taskId,
        title = title,
        isCompleted = isCompleted,
        orderIndex = orderIndex,
        createdAt = createdAt
    )
}

fun SubTask.toEntity(): SubTaskEntity {
    return SubTaskEntity(
        id = id,
        taskId = taskId,
        title = title,
        isCompleted = isCompleted,
        orderIndex = orderIndex,
        createdAt = createdAt,
        isDeleted = false
    )
}

fun List<SubTaskEntity>.toDomainList(): List<SubTask> {
    return map { it.toDomain() }
}
