package com.taktak.app.data.dao

import androidx.room.*
import com.taktak.app.data.model.Batch
import com.taktak.app.data.model.BatchStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BatchDao {
    @Query("SELECT * FROM batches ORDER BY startDate DESC")
    fun getAllBatches(): Flow<List<Batch>>

    @Query("SELECT * FROM batches WHERE id = :batchId")
    fun getBatchById(batchId: Long): Flow<Batch?>

    @Query("SELECT * FROM batches WHERE recipeId = :recipeId ORDER BY startDate DESC")
    fun getBatchesByRecipe(recipeId: Long): Flow<List<Batch>>

    @Query("SELECT * FROM batches WHERE status = :status ORDER BY startDate DESC")
    fun getBatchesByStatus(status: BatchStatus): Flow<List<Batch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: Batch): Long

    @Update
    suspend fun updateBatch(batch: Batch)

    @Delete
    suspend fun deleteBatch(batch: Batch)
}
