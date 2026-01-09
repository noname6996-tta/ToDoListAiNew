package com.tta.todolistainew.feature.task.data.mapper

import com.tta.todolistainew.feature.task.data.local.TaskEntity
import com.tta.todolistainew.feature.task.domain.model.Task

/**
 * Extension functions for mapping between data layer (Entity) and domain layer models.
 * This keeps the data and domain layers decoupled.
 */

/**
 * Maps a TaskEntity (data layer) to a Task (domain layer).
 */
fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt,
        dueDate = dueDate,
        taskType = taskType,
        goalId = goalId,
        hasNotification = hasNotification,
        timeNotification = timeNotification,
        repeatNotification = repeatNotification,
        isDeleted = isDeleted,
        deletedAt = deletedAt?.let { java.time.LocalDate.ofEpochDay(it) }
    )
}

/**
 * Maps a Task (domain layer) to a TaskEntity (data layer).
 */
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt,
        dueDate = dueDate,
        taskType = taskType,
        goalId = goalId,
        hasNotification = hasNotification,
        timeNotification = timeNotification,
        repeatNotification = repeatNotification,
        isDeleted = isDeleted,
        deletedAt = deletedAt?.toEpochDay()
    )
}

/**
 * Maps a list of TaskEntity to a list of Task.
 */
fun List<TaskEntity>.toDomainList(): List<Task> {
    return map { it.toDomain() }
}

/**
 * Maps a list of Task to a list of TaskEntity.
 */
fun List<Task>.toEntityList(): List<TaskEntity> {
    return map { it.toEntity() }
}
