package com.taktak.app.alarm

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.taktak.app.data.model.AlarmItem
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * Scheduler for managing brew batch alarms using WorkManager
 */
class AlarmScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule an alarm
     */
    fun scheduleAlarm(alarm: AlarmItem) {
        if (!alarm.isEnabled || alarm.isTriggered) {
            return
        }

        val now = Instant.now()
        val scheduledTime = alarm.scheduledTime

        // Calculate delay
        val delay = Duration.between(now, scheduledTime).toMillis()

        if (delay <= 0) {
            // Alarm time has passed, don't schedule
            return
        }

        // Create input data for the worker
        val inputData = Data.Builder()
            .putLong("alarmId", alarm.id)
            .putLong("batchId", alarm.batchId)
            .putString("title", alarm.title)
            .putString("description", alarm.description)
            .build()

        // Create work request
        val workRequest = OneTimeWorkRequestBuilder<AlarmWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("alarm_${alarm.id}")
            .addTag("batch_${alarm.batchId}")
            .build()

        // Enqueue the work with a unique name to avoid duplicates
        workManager.enqueueUniqueWork(
            "alarm_${alarm.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    /**
     * Cancel a specific alarm
     */
    fun cancelAlarm(alarmId: Long) {
        workManager.cancelUniqueWork("alarm_$alarmId")
    }

    /**
     * Cancel all alarms for a specific batch
     */
    fun cancelBatchAlarms(batchId: Long) {
        workManager.cancelAllWorkByTag("batch_$batchId")
    }

    /**
     * Reschedule all active alarms (used after device reboot)
     */
    suspend fun rescheduleAllAlarms(alarms: List<AlarmItem>) {
        // Cancel all existing work
        workManager.cancelAllWorkByTag("alarm")

        // Schedule all active alarms
        alarms.forEach { alarm ->
            if (alarm.isEnabled && !alarm.isTriggered) {
                scheduleAlarm(alarm)
            }
        }
    }
}
