package com.taktak.app.alarm

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.taktak.app.data.database.TakTakDatabase
import com.taktak.app.data.model.AlarmItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker that handles alarm triggers and shows notifications
 */
class AlarmWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val alarmId = inputData.getLong("alarmId", -1)
            val batchId = inputData.getLong("batchId", -1)
            val title = inputData.getString("title") ?: "Brew Alarm"
            val description = inputData.getString("description") ?: ""

            if (alarmId == -1L || batchId == -1L) {
                return@withContext Result.failure()
            }

            // Get database instance
            val database = TakTakDatabase.getDatabase(applicationContext)
            val alarmDao = database.alarmDao()
            val batchDao = database.batchDao()

            // Get the alarm from database
            val alarm = alarmDao.getAlarmById(alarmId)
            if (alarm == null || !alarm.isEnabled || alarm.isTriggered) {
                return@withContext Result.success()
            }

            // Get batch info
            val batch = batchDao.getBatchByIdSync(batchId)
            val batchName = batch?.batchName ?: "Unknown Batch"

            // Show notification
            NotificationHelper.showAlarmNotification(applicationContext, alarm, batchName)

            // Mark alarm as triggered
            alarmDao.markAlarmAsTriggered(alarmId)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
