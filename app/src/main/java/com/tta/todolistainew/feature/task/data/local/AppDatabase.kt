package com.tta.todolistainew.feature.task.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tta.todolistainew.feature.goal.data.local.GoalDao
import com.tta.todolistainew.feature.goal.data.local.GoalEntity

/**
 * Room Database class for the application.
 * Uses singleton pattern to ensure only one instance exists.
 */
@Database(
    entities = [
        TaskEntity::class,
        GoalEntity::class,
        SubTaskEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to TaskDao for database operations.
     */
    abstract fun taskDao(): TaskDao
    
    /**
     * Provides access to GoalDao for database operations.
     */
    abstract fun goalDao(): GoalDao
    
    /**
     * Provides access to SubTaskDao for database operations.
     */
    abstract fun subTaskDao(): SubTaskDao
    
    companion object {
        private const val DATABASE_NAME = "todolist_database"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Gets the singleton instance of the database.
         * Uses double-checked locking for thread safety.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }
}
