package com.taktak.app.data.dao

import androidx.room.*
import com.taktak.app.data.model.TastingNote
import kotlinx.coroutines.flow.Flow

@Dao
interface TastingNoteDao {
    @Query("SELECT * FROM tasting_notes ORDER BY tastingDate DESC")
    fun getAllTastingNotes(): Flow<List<TastingNote>>

    @Query("SELECT * FROM tasting_notes WHERE id = :noteId")
    fun getTastingNoteById(noteId: Long): Flow<TastingNote?>

    @Query("SELECT * FROM tasting_notes WHERE batchId = :batchId ORDER BY tastingDate DESC")
    fun getTastingNotesByBatch(batchId: Long): Flow<List<TastingNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTastingNote(note: TastingNote): Long

    @Update
    suspend fun updateTastingNote(note: TastingNote)

    @Delete
    suspend fun deleteTastingNote(note: TastingNote)
}
