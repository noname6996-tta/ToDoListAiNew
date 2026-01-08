package com.tta.todolistainew.feature.task.data.local

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * Type converters for Room database.
 * Converts non-primitive types to/from database-compatible formats.
 */
class Converters {
    
    // ===== LocalDate Converters =====
    
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
    
    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? {
        return epochDay?.let { LocalDate.ofEpochDay(it) }
    }
    
    // ===== TaskType Converters =====
    
    @TypeConverter
    fun fromTaskType(taskType: TaskType): String {
        return taskType.name
    }
    
    @TypeConverter
    fun toTaskType(value: String): TaskType {
        return TaskType.valueOf(value)
    }
}
