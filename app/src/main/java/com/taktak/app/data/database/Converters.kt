package com.taktak.app.data.database

import androidx.room.TypeConverter
import com.taktak.app.data.model.BatchStatus

class Converters {
    @TypeConverter
    fun fromBatchStatus(value: BatchStatus): String {
        return value.name
    }

    @TypeConverter
    fun toBatchStatus(value: String): BatchStatus {
        return BatchStatus.valueOf(value)
    }
}
