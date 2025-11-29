package com.taktak.app.data.dao

import androidx.room.*
import com.taktak.app.data.model.RecipeStage
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeStageDao {
    @Query("SELECT * FROM recipe_stages WHERE recipeId = :recipeId ORDER BY stageNumber ASC")
    fun getStagesForRecipe(recipeId: Long): Flow<List<RecipeStage>>

    @Query("SELECT * FROM recipe_stages WHERE recipeId = :recipeId ORDER BY stageNumber ASC")
    suspend fun getStagesForRecipeSync(recipeId: Long): List<RecipeStage>

    @Query("SELECT * FROM recipe_stages WHERE id = :id")
    suspend fun getStageById(id: Long): RecipeStage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStage(stage: RecipeStage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStages(stages: List<RecipeStage>)

    @Update
    suspend fun updateStage(stage: RecipeStage)

    @Delete
    suspend fun deleteStage(stage: RecipeStage)

    @Query("DELETE FROM recipe_stages WHERE recipeId = :recipeId")
    suspend fun deleteStagesForRecipe(recipeId: Long)

    @Query("SELECT COUNT(*) FROM recipe_stages WHERE recipeId = :recipeId")
    suspend fun getStageCount(recipeId: Long): Int
}
