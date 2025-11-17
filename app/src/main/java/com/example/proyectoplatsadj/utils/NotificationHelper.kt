package com.example.proyectoplatsadj.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.proyectoplatsadj.app.MainActivity

object NotificationHelper {

    private const val CHANNEL_ID_TASKS = "task_reminders"
    private const val CHANNEL_NAME_TASKS = "Recordatorios de Tareas"
    private const val CHANNEL_DESCRIPTION_TASKS = "Notificaciones sobre tus tareas pendientes"

    private const val CHANNEL_ID_GENERAL = "general_notifications"
    private const val CHANNEL_NAME_GENERAL = "Notificaciones Generales"
    private const val CHANNEL_DESCRIPTION_GENERAL = "Notificaciones generales de la app"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val taskChannel = NotificationChannel(
                CHANNEL_ID_TASKS,
                CHANNEL_NAME_TASKS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION_TASKS
                enableVibration(true)
                enableLights(true)
            }

            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                CHANNEL_NAME_GENERAL,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION_GENERAL
            }

            notificationManager.createNotificationChannel(taskChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showTaskCreatedNotification(
        context: Context,
        taskId: Int,
        taskTitle: String,
        taskDetail: String?
    ) {
        // ✅ Verificar permiso ANTES de intentar mostrar
        if (!hasNotificationPermission(context)) return

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("✅ Tarea creada")
                .setContentText(taskTitle)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$taskTitle${if (taskDetail != null) "\n$taskDetail" else ""}")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            // ✅ Solo llamar notify si tenemos permiso
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) {
                NotificationManagerCompat.from(context).notify(taskId, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showTaskReminderNotification(
        context: Context,
        taskId: Int,
        taskTitle: String,
        taskDetail: String?,
        dueDate: String?,
        dueTime: String?,
        difficulty: Int
    ) {
        if (!hasNotificationPermission(context)) return

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                taskId + 10000,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val difficultyEmoji = when (difficulty) {
                1 -> "🟢"
                2 -> "🟡"
                3 -> "🔴"
                else -> "⚪"
            }

            val timeInfo = if (dueTime != null) " a las $dueTime" else ""

            val notification = NotificationCompat.Builder(context, CHANNEL_ID_TASKS)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("$difficultyEmoji Recordatorio: $taskTitle")
                .setContentText("Vence $dueDate$timeInfo")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("${taskDetail ?: "Sin detalles"}\n\n📅 Fecha: $dueDate$timeInfo")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 200, 500))
                .build()

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) {
                NotificationManagerCompat.from(context).notify(taskId + 10000, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showDailySummaryNotification(
        context: Context,
        taskCount: Int,
        urgentTaskCount: Int
    ) {
        if (!hasNotificationPermission(context)) return

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                99999,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val message = when {
                taskCount == 0 -> "¡No tienes tareas pendientes! 🎉"
                urgentTaskCount > 0 -> "Tienes $taskCount tareas pendientes, $urgentTaskCount son urgentes ⚠️"
                else -> "Tienes $taskCount tareas pendientes para hoy 📋"
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID_TASKS)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🌅 Buenos días")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) {
                NotificationManagerCompat.from(context).notify(99999, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}