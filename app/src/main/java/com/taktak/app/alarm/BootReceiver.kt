package com.taktak.app.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taktak.app.data.database.TakTakDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Broadcast receiver that reschedules alarms after device reboot
 */
class BootReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scope.launch {
                try {
                    val database = TakTakDatabase.getDatabase(context)
                    val alarmDao = database.alarmDao()

                    // Get all active alarms
                    val activeAlarms = alarmDao.getActiveAlarms().first()

                    // Reschedule them
                    val scheduler = AlarmScheduler(context)
                    scheduler.rescheduleAllAlarms(activeAlarms)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
