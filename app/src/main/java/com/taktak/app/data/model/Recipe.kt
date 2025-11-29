package com.taktak.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Basic recipe info
    val name: String,
    val description: String,

    // Number of stages in this recipe
    val numberOfStages: Int,

    // Days from batch start when filtering should occur
    val filteringDays: Int,

    // Category for organization
    val category: String = "Makgeolli",

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
