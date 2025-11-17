package com.example.proyectoplatsadj.utils


import android.content.Context
import androidx.work.*
import com.example.proyectoplatsadj.workers.TaskReminderWorker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

object NotificationScheduler {


    fun scheduleTaskReminder(
        context: Context,
        taskId: Int,
        taskTitle: String,
        taskDetail: String?,
        dueDate: String?,
        dueTime: String?,
        difficulty: Int,
        reminderMinutesBefore: Long = 60
    ) {
        if (dueDate == null) return

        try {
            // Parsear fecha y hora
            val date = LocalDate.parse(dueDate)
            val time = if (dueTime != null) {
                try {
                    LocalTime.parse(dueTime)
                } catch (e: Exception) {
                    LocalTime.of(23, 59)
                }
            } else {
                LocalTime.of(23, 59)
            }

            val dueDateTime = LocalDateTime.of(date, time)

            val notificationDateTime = dueDateTime.minusMinutes(reminderMinutesBefore)

            val currentTime = System.currentTimeMillis()
            val notificationTime = notificationDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            if (notificationTime <= currentTime) {
                return
            }

            val delay = notificationTime - currentTime

            val inputData = workDataOf(
                "TASK_ID" to taskId,
                "TASK_TITLE" to taskTitle,
                "TASK_DETAIL" to taskDetail,
                "DUE_DATE" to dueDate,
                "DUE_TIME" to dueTime,
                "DIFFICULTY" to difficulty
            )

            val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("task_reminder_$taskId")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "task_reminder_$taskId",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun scheduleMultipleReminders(
        context: Context,
        taskId: Int,
        taskTitle: String,
        taskDetail: String?,
        dueDate: String?,
        dueTime: String?,
        difficulty: Int
    ) {
        scheduleTaskReminder(
            context, taskId, taskTitle, taskDetail,
            dueDate, dueTime, difficulty, 1440
        )

        scheduleTaskReminder(
            context, taskId + 5000, taskTitle, taskDetail,
            dueDate, dueTime, difficulty, 60
        )
    }


    fun cancelTaskReminder(context: Context, taskId: Int) {
        WorkManager.getInstance(context).cancelAllWorkByTag("task_reminder_$taskId")
    }


    fun cancelAllReminders(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }
}