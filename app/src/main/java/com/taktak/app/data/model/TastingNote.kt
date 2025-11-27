package com.taktak.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasting_notes",
    foreignKeys = [
        ForeignKey(
            entity = Batch::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TastingNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val batchId: Long,
    val tastingDate: Long,
    val appearance: String,
    val aroma: String,
    val taste: String,
    val mouthfeel: String,
    val overallRating: Float,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
