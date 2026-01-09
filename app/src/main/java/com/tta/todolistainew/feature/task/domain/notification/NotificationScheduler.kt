package com.tta.todolistainew.feature.task.domain.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.tta.todolistainew.feature.task.domain.model.Task
import com.tta.todolistainew.feature.task.receiver.AlarmReceiver
import android.util.Log

class NotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(task: Task) {
        if (!task.hasNotification) {
            cancel(task)
            return
        }

        if (task.timeNotification == null && task.dueDate == null) return
        if (task.isCompleted || task.isDeleted) {
            cancel(task)
            return
        }

        // Default logic: If timeNotification is null but dueDate exists, set to 5 mins before.
        // BUT: Task model says timeNotification : Long?, so logic should be setting this value.
        // If the repository/usecase sets the timeNotification value properly, we just use it here.
        // If not, we calculate it here. Ideally value should be set in Domain object before reaching here.
        // We will assume timeNotification holds the trigger time.
        
        val triggerTime = task.timeNotification ?: run {
             // Fallback if not set but hasDueDate (should be handled upstream but safe check)
             task.dueDate?.let { it - 5 * 60 * 1000 }
        } ?: return

        if (triggerTime <= System.currentTimeMillis()) return // Don't schedule past events (unless we want immediate trigger, but safer to skip)
        
        // Permission check for Exact Alarm (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w("NotificationScheduler", "Cannot schedule exact alarms. Permission missing.")
                // In a real app, prompt user. For now, we skip.
                return
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TASK_ID", task.id)
            putExtra("TASK_TITLE", task.title)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
            Log.d("NotificationScheduler", "Scheduled alarm for task ${task.id} at $triggerTime")
        } catch (e: SecurityException) {
            Log.e("NotificationScheduler", "Failed to schedule alarm", e)
        }
    }

    fun cancel(task: Task) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
