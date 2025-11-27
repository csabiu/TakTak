package com.taktak.app.data.dao

import androidx.room.*
import com.taktak.app.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries ORDER BY entryDate DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE id = :entryId")
    fun getJournalEntryById(entryId: Long): Flow<JournalEntry?>

    @Query("SELECT * FROM journal_entries WHERE batchId = :batchId ORDER BY entryDate DESC")
    fun getJournalEntriesByBatch(batchId: Long): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry): Long

    @Update
    suspend fun updateJournalEntry(entry: JournalEntry)

    @Delete
    suspend fun deleteJournalEntry(entry: JournalEntry)
}
