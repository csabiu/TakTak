package com.taktak.app

import android.app.Application
import com.taktak.app.alarm.AlarmScheduler
import com.taktak.app.alarm.NotificationHelper
import com.taktak.app.data.database.TakTakDatabase
import com.taktak.app.data.repository.TakTakRepository

class TakTakApplication : Application() {
    val database by lazy { TakTakDatabase.getDatabase(this) }
    val repository by lazy {
        TakTakRepository(
            database.recipeDao(),
            database.batchDao(),
            database.tastingNoteDao(),
            database.journalEntryDao(),
            database.alarmDao()
        )
    }
    val alarmScheduler by lazy { AlarmScheduler(this) }

    override fun onCreate() {
        super.onCreate()
        // Create notification channel for alarms
        NotificationHelper.createNotificationChannel(this)
    }
}
