package com.tta.todolistainew.feature.goal.domain.usecase

import com.tta.todolistainew.core.common.Resource
import com.tta.todolistainew.feature.goal.domain.model.Goal
import com.tta.todolistainew.feature.goal.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * UseCase for getting all active goals.
 */
class GetGoalsUseCase(
    private val goalRepository: GoalRepository
) {
    /**
     * Invokes the use case to get all active goals.
     */
    operator fun invoke(): Flow<Resource<List<Goal>>> {
        return goalRepository.getGoals()
            .map<List<Goal>, Resource<List<Goal>>> { goals ->
                Resource.Success(goals)
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
