package com.tta.todolistainew.feature.task.domain.usecase

import com.tta.todolistainew.core.common.Resource
import com.tta.todolistainew.feature.task.domain.model.Task
import com.tta.todolistainew.feature.task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * UseCase for getting all tasks.
 * Encapsulates the business logic for fetching tasks and wraps the result in a Resource.
 */
class GetTasksUseCase(
    private val taskRepository: TaskRepository
) {
    /**
     * Invokes the use case to get all tasks.
     * Emits Loading, then Success with data, or Error if an exception occurs.
     */
    operator fun invoke(): Flow<Resource<List<Task>>> {
        return taskRepository.getTasks()
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
