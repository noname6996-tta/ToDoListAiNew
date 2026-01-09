package com.tta.todolistainew.core.di

import android.content.Context
import com.tta.todolistainew.feature.goal.data.local.GoalDao
import com.tta.todolistainew.feature.goal.data.repository.GoalRepositoryImpl
import com.tta.todolistainew.feature.goal.domain.repository.GoalRepository
import com.tta.todolistainew.feature.goal.domain.usecase.GetGoalsUseCase
import com.tta.todolistainew.feature.task.data.local.AppDatabase
import com.tta.todolistainew.feature.task.data.local.SubTaskDao
import com.tta.todolistainew.feature.task.data.local.TaskDao
import com.tta.todolistainew.feature.task.data.repository.TaskRepositoryImpl
import com.tta.todolistainew.feature.task.domain.repository.TaskRepository
import com.tta.todolistainew.feature.task.domain.usecase.GetTasksByTypeUseCase
import com.tta.todolistainew.feature.task.domain.usecase.GetTasksUseCase

/**
 * Manual Dependency Injection container.
 * Provides dependencies throughout the application using constructor injection.
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
    
    private val goalDao: GoalDao by lazy {
        database.goalDao()
    }
    
    private val subTaskDao: SubTaskDao by lazy {
        database.subTaskDao()
    }
    
    val notificationScheduler: com.tta.todolistainew.feature.task.domain.notification.NotificationScheduler by lazy {
        com.tta.todolistainew.feature.task.domain.notification.NotificationScheduler(context)
    }

    // Repositories
    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(taskDao, notificationScheduler)
    }
    
    val goalRepository: GoalRepository by lazy {
        GoalRepositoryImpl(goalDao)
    }
    
    // Use Cases
    val getTasksUseCase: GetTasksUseCase by lazy {
        GetTasksUseCase(taskRepository)
    }
    
    val getTasksByTypeUseCase: GetTasksByTypeUseCase by lazy {
        GetTasksByTypeUseCase(taskRepository)
    }
    
    val getGoalsUseCase: GetGoalsUseCase by lazy {
        GetGoalsUseCase(goalRepository)
    }
    
    // Settings
    val settingsRepository: com.tta.todolistainew.feature.settings.data.SettingsRepository by lazy {
        com.tta.todolistainew.feature.settings.data.SettingsRepository(context)
    }
}
