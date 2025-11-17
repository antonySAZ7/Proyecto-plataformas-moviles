package com.example.proyectoplatsadj.workers


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.proyectoplatsadj.utils.NotificationHelper


class TaskReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val taskId = inputData.getInt("TASK_ID", -1)
            val taskTitle = inputData.getString("TASK_TITLE") ?: return Result.failure()
            val taskDetail = inputData.getString("TASK_DETAIL")
            val dueDate = inputData.getString("DUE_DATE")
            val dueTime = inputData.getString("DUE_TIME")
            val difficulty = inputData.getInt("DIFFICULTY", 2)

            if (taskId == -1) {
                return Result.failure()
            }

            NotificationHelper.showTaskReminderNotification(
                applicationContext,
                taskId,
                taskTitle,
                taskDetail,
                dueDate,
                dueTime,
                difficulty
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}


class DailySummaryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val taskCount = inputData.getInt("TASK_COUNT", 0)
            val urgentCount = inputData.getInt("URGENT_COUNT", 0)

            NotificationHelper.showDailySummaryNotification(
                applicationContext,
                taskCount,
                urgentCount
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}