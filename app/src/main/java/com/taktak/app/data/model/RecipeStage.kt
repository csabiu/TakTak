package com.taktak.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a single stage in a multi-stage recipe
 * All stages can include rice amount, stages 2+ include date of action
 */
@Entity(
    tableName = "recipe_stages",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["recipeId"])]
)
data class RecipeStage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val recipeId: Long,

    // Stage number (1, 2, 3, etc.)
    val stageNumber: Int,

    // All stages: Rice amount in Kg (optional)
    val riceAmountKg: Double? = null,

    // All stages: Water amount in Liters
    val waterAmountLiters: Double,

    // All stages: Nuruk amount in grams
    val nurukAmountGrams: Double,

    // Stage 2+: Days from batch start when this stage should be performed
    val daysFromStart: Int? = null,

    // Instructions for this stage
    val instructions: String,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
