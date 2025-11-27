package com.taktak.app.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.taktak.app.MainActivity
import com.taktak.app.R
import com.taktak.app.data.model.AlarmItem

/**
 * Helper class for creating and displaying notifications for brew alarms
 */
object NotificationHelper {
    private const val CHANNEL_ID = "brew_alarms"
    private const val CHANNEL_NAME = "Brew Alarms"
    private const val CHANNEL_DESCRIPTION = "Notifications for brew batch alarms"

    /**
     * Create notification channel (required for Android 8.0+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Show notification for a brew alarm
     */
    fun showAlarmNotification(context: Context, alarm: AlarmItem, batchName: String) {
        createNotificationChannel(context)

        // Create intent to open the app when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("batchId", alarm.batchId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(alarm.title)
            .setContentText("Batch: $batchName - ${alarm.description}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Batch: $batchName\n\n${alarm.description}")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(alarm.id.toInt(), notification)
    }

    /**
     * Cancel a specific notification
     */
    fun cancelNotification(context: Context, alarmId: Long) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alarmId.toInt())
    }
}
