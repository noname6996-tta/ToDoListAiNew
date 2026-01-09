package com.tta.todolistainew.feature.task.data.repository

import com.tta.todolistainew.feature.task.data.local.TaskDao
import com.tta.todolistainew.feature.task.data.local.TaskType
import com.tta.todolistainew.feature.task.data.mapper.toDomain
import com.tta.todolistainew.feature.task.data.mapper.toDomainList
import com.tta.todolistainew.feature.task.data.mapper.toEntity
import com.tta.todolistainew.feature.task.domain.model.Task
import com.tta.todolistainew.feature.task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Implementation of TaskRepository.
 * Handles data operations and maps between data layer entities and domain models.
 */
class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val notificationScheduler: com.tta.todolistainew.feature.task.domain.notification.NotificationScheduler
) : TaskRepository {
    
    // ... (Read methods remain the same) ...

    override fun getTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.toDomainList()
        }
    }
    
    override fun getTasksByType(taskType: TaskType): Flow<List<Task>> {
        return taskDao.getTasksByType(taskType).map { entities ->
            entities.toDomainList()
        }
    }
    
    override fun getTasksByGoalId(goalId: Long): Flow<List<Task>> {
        return taskDao.getTasksByGoalId(goalId).map { entities ->
            entities.toDomainList()
        }
    }
    
    override fun getTaskById(taskId: Long): Flow<Task?> {
        return taskDao.getTaskById(taskId).map { entity ->
            entity?.toDomain()
        }
    }

    override fun getTasksForDate(date: java.time.LocalDate): Flow<List<Task>> {
        val zoneId = java.time.ZoneId.systemDefault()
        val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
        
        return taskDao.getTasksByDateRange(startOfDay, endOfDay).map { entities ->
            entities.toDomainList()
        }
    }
    
    override fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks().map { entities ->
            entities.toDomainList()
        }
    }
    
    override fun getPendingTasks(): Flow<List<Task>> {
        return taskDao.getPendingTasks().map { entities ->
            entities.toDomainList()
        }
    }
    
    override suspend fun addTask(task: Task): Long {
        val id = taskDao.insertTask(task.toEntity())
        notificationScheduler.schedule(task.copy(id = id))
        return id
    }
    
    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
        notificationScheduler.schedule(task)
    }
    
    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
        notificationScheduler.cancel(task)
    }
    
    override suspend fun deleteTaskById(taskId: Long) {
        val task = taskDao.getTaskById(taskId).first()?.toDomain()
        task?.let { notificationScheduler.cancel(it) }
        taskDao.deleteTaskById(taskId)
    }
    
    override suspend fun toggleTaskCompletion(taskId: Long) {
        val task = taskDao.getTaskById(taskId).first()?.toDomain()
        task?.let {
            val updatedTask = it.copy(isCompleted = !it.isCompleted)
            taskDao.updateTask(updatedTask.toEntity())
            
            // Re-schedule or cancel based on new state
            notificationScheduler.schedule(updatedTask) 
        }
    }
    
    override suspend fun deleteCompletedTasks() {
        // ideally we cancel alarms for all completed tasks first, but that's expensive to query all.
        // For now, assuming they are cancelled on completion or just ignored by receiver if checked.
        taskDao.deleteCompletedTasks()
    }
    
    override fun getCompletedCountByType(taskType: TaskType): Flow<Int> {
        return taskDao.getCompletedCountByType(taskType)
    }
    
    override fun getTotalCountByType(taskType: TaskType): Flow<Int> {
        return taskDao.getTotalCountByType(taskType)
    }
    
    override fun getCompletedCountByGoal(goalId: Long): Flow<Int> {
        return taskDao.getCompletedCountByGoal(goalId)
    }
    
    override fun getTotalCountByGoal(goalId: Long): Flow<Int> {
        return taskDao.getTotalCountByGoal(goalId)
    }
}
