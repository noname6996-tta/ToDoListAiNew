package com.tta.todolistainew.feature.goal.data.repository

import com.tta.todolistainew.feature.goal.data.local.GoalDao
import com.tta.todolistainew.feature.goal.data.mapper.toDomain
import com.tta.todolistainew.feature.goal.data.mapper.toDomainList
import com.tta.todolistainew.feature.goal.data.mapper.toEntity
import com.tta.todolistainew.feature.goal.domain.model.Goal
import com.tta.todolistainew.feature.goal.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Implementation of GoalRepository.
 */
class GoalRepositoryImpl(
    private val goalDao: GoalDao
) : GoalRepository {
    
    override fun getGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { entities ->
            entities.toDomainList()
        }
    }
    
    override fun getGoalById(goalId: Long): Flow<Goal?> {
        return goalDao.getGoalById(goalId).map { entity ->
            entity?.toDomain()
        }
    }
    
    override suspend fun addGoal(goal: Goal): Long {
        return goalDao.insertGoal(goal.toEntity())
    }
    
    override suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal.toEntity())
    }
    
    override suspend fun deleteGoal(goalId: Long) {
        goalDao.softDeleteGoal(goalId)
    }
    
    override suspend fun toggleGoalCompletion(goalId: Long) {
        val goal = goalDao.getGoalById(goalId).first()
        goal?.let {
            val isCompleted = !it.isCompleted
            val completedAt = if (isCompleted) LocalDate.now() else null
            
            val updatedGoal = it.copy(
                isCompleted = isCompleted,
                completedAt = completedAt
            )
            goalDao.updateGoal(updatedGoal)
        }
    }
}
