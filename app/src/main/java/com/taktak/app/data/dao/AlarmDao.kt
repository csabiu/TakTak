package com.taktak.app.data.dao

import androidx.room.*
import com.taktak.app.data.model.AlarmItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarm_items ORDER BY scheduledTime ASC")
    fun getAllAlarms(): Flow<List<AlarmItem>>

    @Query("SELECT * FROM alarm_items WHERE id = :id")
    suspend fun getAlarmById(id: Long): AlarmItem?

    @Query("SELECT * FROM alarm_items WHERE batchId = :batchId ORDER BY scheduledTime ASC")
    fun getAlarmsByBatch(batchId: Long): Flow<List<AlarmItem>>

    @Query("SELECT * FROM alarm_items WHERE isEnabled = 1 AND isTriggered = 0 ORDER BY scheduledTime ASC")
    fun getActiveAlarms(): Flow<List<AlarmItem>>

    @Query("SELECT * FROM alarm_items WHERE scheduledTime <= :currentTime AND isEnabled = 1 AND isTriggered = 0")
    suspend fun getTriggeredAlarms(currentTime: Long): List<AlarmItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmItem): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmItem)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmItem)

    @Query("DELETE FROM alarm_items WHERE batchId = :batchId")
    suspend fun deleteAlarmsByBatch(batchId: Long)

    @Query("UPDATE alarm_items SET isTriggered = 1 WHERE id = :alarmId")
    suspend fun markAlarmAsTriggered(alarmId: Long)
}
