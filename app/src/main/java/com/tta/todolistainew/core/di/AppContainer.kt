package com.tta.todolistainew.core.di

import android.content.Context
import com.tta.todolistainew.feature.task.data.local.AppDatabase
import com.tta.todolistainew.feature.task.data.local.TaskDao
import com.tta.todolistainew.feature.task.data.repository.TaskRepositoryImpl
import com.tta.todolistainew.feature.task.domain.repository.TaskRepository
import com.tta.todolistainew.feature.task.domain.usecase.GetTasksUseCase

/**
 * Manual Dependency Injection container.
 * Provides dependencies throughout the application using constructor injection.
 * This is a simple alternative to Hilt/Dagger for smaller projects.
 */
class AppContainer(context: Context) {
    
    // Database
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }
    
    // DAOs
    private val taskDao: TaskDao by lazy {
        database.taskDao()
    }
    
    // Repositories
    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(taskDao)
    }
    
    // Use Cases
    val getTasksUseCase: GetTasksUseCase by lazy {
        GetTasksUseCase(taskRepository)
    }
}
