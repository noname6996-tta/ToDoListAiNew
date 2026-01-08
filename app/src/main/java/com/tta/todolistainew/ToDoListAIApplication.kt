package com.tta.todolistainew

import android.app.Application
import com.tta.todolistainew.core.di.AppContainer

/**
 * Application class that initializes the dependency injection container.
 * The AppContainer provides all dependencies needed throughout the app.
 */
class ToDoListAIApplication : Application() {
    
    /**
     * Dependency injection container accessible throughout the app.
     */
    lateinit var appContainer: AppContainer
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize the DI container
        appContainer = AppContainer(this)
    }
}
