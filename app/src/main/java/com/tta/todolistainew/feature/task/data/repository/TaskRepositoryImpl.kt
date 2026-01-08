package com.tta.todolistainew.feature.task.data.repository

import com.tta.todolistainew.feature.task.data.local.TaskDao
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
    private val taskDao: TaskDao
) : TaskRepository {
    
    override fun getTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.toDomainList()
        }
    }
    
    override fun getTaskById(taskId: Long): Flow<Task?> {
        return taskDao.getTaskById(taskId).map { entity ->
            entity?.toDomain()
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
        return taskDao.insertTask(task.toEntity())
    }
    
    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }
    
    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }
    
    override suspend fun deleteTaskById(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }
    
    override suspend fun toggleTaskCompletion(taskId: Long) {
        val task = taskDao.getTaskById(taskId).first()
        task?.let {
            val updatedTask = it.copy(isCompleted = !it.isCompleted)
            taskDao.updateTask(updatedTask)
        }
    }
    
    override suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }
}
