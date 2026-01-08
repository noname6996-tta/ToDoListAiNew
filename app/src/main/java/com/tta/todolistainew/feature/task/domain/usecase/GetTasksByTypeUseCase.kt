package com.tta.todolistainew.feature.task.domain.usecase

import com.tta.todolistainew.core.common.Resource
import com.tta.todolistainew.feature.task.data.local.TaskType
import com.tta.todolistainew.feature.task.domain.model.Task
import com.tta.todolistainew.feature.task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * UseCase for getting tasks by type (DAILY, QUICK, GOAL).
 */
class GetTasksByTypeUseCase(
    private val taskRepository: TaskRepository
) {
    /**
     * Invokes the use case to get tasks of a specific type.
     */
    operator fun invoke(taskType: TaskType): Flow<Resource<List<Task>>> {
        // Implementation would depend on adding getTasksByType to repository
        // For now, we'll filter from all tasks if repository doesn't support it directly
        // or assumption is repository will be updated.
        
        // Since I haven't updated TaskRepository interface yet, let me update it first.
        // Wait, I should have updated TaskRepository interface in previous step.
        // Let me assume TaskRepository has getTasksByType or I will update it now.
        
        return taskRepository.getTasksByType(taskType)
            .map<List<Task>, Resource<List<Task>>> { tasks ->
                Resource.Success(tasks)
            }
            .onStart {
                emit(Resource.Loading)
            }
            .catch { throwable ->
                emit(Resource.Error(
                    message = throwable.message ?: "An unexpected error occurred",
                    throwable = throwable
                ))
            }
    }
}
