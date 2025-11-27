package com.taktak.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Represents an alarm for a brew batch
 */
@Entity(
    tableName = "alarm_items",
    foreignKeys = [
        ForeignKey(
            entity = Batch::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("batchId")]
)
data class AlarmItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val batchId: Long,

    val alarmType: AlarmType,

    val title: String,

    val description: String = "",

    val scheduledTime: Instant,

    val isEnabled: Boolean = true,

    val isTriggered: Boolean = false,

    val createdAt: Instant = Instant.now(),

    val updatedAt: Instant = Instant.now()
)
