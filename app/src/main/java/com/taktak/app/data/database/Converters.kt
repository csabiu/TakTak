package com.taktak.app.data.database

import androidx.room.TypeConverter
import com.taktak.app.data.model.AlarmType
import com.taktak.app.data.model.BatchStatus
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromBatchStatus(value: BatchStatus): String {
        return value.name
    }

    @TypeConverter
    fun toBatchStatus(value: String): BatchStatus {
        return BatchStatus.valueOf(value)
    }

    @TypeConverter
    fun fromAlarmType(value: AlarmType): String {
        return value.name
    }

    @TypeConverter
    fun toAlarmType(value: String): AlarmType {
        return AlarmType.valueOf(value)
    }

    @TypeConverter
    fun fromInstant(value: Instant): Long {
        return value.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }
}
