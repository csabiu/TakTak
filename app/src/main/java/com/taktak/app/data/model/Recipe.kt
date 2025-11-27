package com.taktak.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val riceAmount: String,
    val waterAmount: String,
    val nurukAmount: String,
    val additionalIngredients: String = "",
    val instructions: String,
    val fermentationTimeDays: Int,
    val category: String = "Makgeolli",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
