package com.taktak.app.data.repository

import com.taktak.app.data.dao.*
import com.taktak.app.data.model.*
import kotlinx.coroutines.flow.Flow

class TakTakRepository(
    private val recipeDao: RecipeDao,
    private val recipeStageDao: RecipeStageDao,
    private val batchDao: BatchDao,
    private val tastingNoteDao: TastingNoteDao,
    private val journalEntryDao: JournalEntryDao,
    private val alarmDao: AlarmDao
) {
    // Recipe operations
    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    fun getRecipeById(id: Long): Flow<Recipe?> = recipeDao.getRecipeById(id)
    suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insertRecipe(recipe)
    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)
    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
    fun searchRecipes(query: String): Flow<List<Recipe>> = recipeDao.searchRecipes(query)

    // Recipe stage operations
    fun getStagesForRecipe(recipeId: Long): Flow<List<RecipeStage>> = recipeStageDao.getStagesForRecipe(recipeId)
    suspend fun getStagesForRecipeSync(recipeId: Long): List<RecipeStage> = recipeStageDao.getStagesForRecipeSync(recipeId)
    suspend fun insertStage(stage: RecipeStage): Long = recipeStageDao.insertStage(stage)
    suspend fun insertStages(stages: List<RecipeStage>) = recipeStageDao.insertStages(stages)
    suspend fun updateStage(stage: RecipeStage) = recipeStageDao.updateStage(stage)
    suspend fun deleteStage(stage: RecipeStage) = recipeStageDao.deleteStage(stage)
    suspend fun deleteStagesForRecipe(recipeId: Long) = recipeStageDao.deleteStagesForRecipe(recipeId)

    // Batch operations
    fun getAllBatches(): Flow<List<Batch>> = batchDao.getAllBatches()
    fun getBatchById(id: Long): Flow<Batch?> = batchDao.getBatchById(id)
    fun getBatchesByRecipe(recipeId: Long): Flow<List<Batch>> = batchDao.getBatchesByRecipe(recipeId)
    fun getBatchesByStatus(status: BatchStatus): Flow<List<Batch>> = batchDao.getBatchesByStatus(status)
    suspend fun insertBatch(batch: Batch): Long = batchDao.insertBatch(batch)
    suspend fun updateBatch(batch: Batch) = batchDao.updateBatch(batch)
    suspend fun deleteBatch(batch: Batch) = batchDao.deleteBatch(batch)

    // Tasting note operations
    fun getAllTastingNotes(): Flow<List<TastingNote>> = tastingNoteDao.getAllTastingNotes()
    fun getTastingNoteById(id: Long): Flow<TastingNote?> = tastingNoteDao.getTastingNoteById(id)
    fun getTastingNotesByBatch(batchId: Long): Flow<List<TastingNote>> = tastingNoteDao.getTastingNotesByBatch(batchId)
    suspend fun insertTastingNote(note: TastingNote): Long = tastingNoteDao.insertTastingNote(note)
    suspend fun updateTastingNote(note: TastingNote) = tastingNoteDao.updateTastingNote(note)
    suspend fun deleteTastingNote(note: TastingNote) = tastingNoteDao.deleteTastingNote(note)

    // Journal entry operations
    fun getAllJournalEntries(): Flow<List<JournalEntry>> = journalEntryDao.getAllJournalEntries()
    fun getJournalEntryById(id: Long): Flow<JournalEntry?> = journalEntryDao.getJournalEntryById(id)
    fun getJournalEntriesByBatch(batchId: Long): Flow<List<JournalEntry>> = journalEntryDao.getJournalEntriesByBatch(batchId)
    suspend fun insertJournalEntry(entry: JournalEntry): Long = journalEntryDao.insertJournalEntry(entry)
    suspend fun updateJournalEntry(entry: JournalEntry) = journalEntryDao.updateJournalEntry(entry)
    suspend fun deleteJournalEntry(entry: JournalEntry) = journalEntryDao.deleteJournalEntry(entry)

    // Alarm operations
    fun getAllAlarms(): Flow<List<AlarmItem>> = alarmDao.getAllAlarms()
    suspend fun getAlarmById(id: Long): AlarmItem? = alarmDao.getAlarmById(id)
    fun getAlarmsByBatch(batchId: Long): Flow<List<AlarmItem>> = alarmDao.getAlarmsByBatch(batchId)
    fun getActiveAlarms(): Flow<List<AlarmItem>> = alarmDao.getActiveAlarms()
    suspend fun insertAlarm(alarm: AlarmItem): Long = alarmDao.insertAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmItem) = alarmDao.updateAlarm(alarm)
    suspend fun deleteAlarm(alarm: AlarmItem) = alarmDao.deleteAlarm(alarm)
    suspend fun deleteAlarmsByBatch(batchId: Long) = alarmDao.deleteAlarmsByBatch(batchId)
    suspend fun markAlarmAsTriggered(alarmId: Long) = alarmDao.markAlarmAsTriggered(alarmId)
}
