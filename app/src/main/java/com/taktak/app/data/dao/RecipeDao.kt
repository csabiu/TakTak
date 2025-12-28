package com.taktak.app.data.dao

import androidx.room.*
import com.taktak.app.data.model.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: Long): Flow<Recipe?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<Recipe>>

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipeCount(): Int

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeByIdSync(recipeId: Long): Recipe?
}
