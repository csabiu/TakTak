package com.taktak.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.taktak.app.data.dao.*
import com.taktak.app.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        Recipe::class,
        RecipeStage::class,
        Batch::class,
        TastingNote::class,
        AlarmItem::class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TakTakDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun recipeStageDao(): RecipeStageDao
    abstract fun batchDao(): BatchDao
    abstract fun tastingNoteDao(): TastingNoteDao
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: TakTakDatabase? = null

        private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private val databaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    applicationScope.launch {
                        populateDatabase(database.recipeDao(), database.recipeStageDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    applicationScope.launch {
                        // Check if recipes table is empty and populate if needed
                        val recipeDao = database.recipeDao()
                        val recipeStageDao = database.recipeStageDao()
                        val recipeCount = recipeDao.getRecipeCount()
                        if (recipeCount == 0) {
                            populateDatabase(recipeDao, recipeStageDao)
                        }
                    }
                }
            }
        }

        private suspend fun populateDatabase(recipeDao: RecipeDao, recipeStageDao: RecipeStageDao) {
            // === RECIPE 1: Basic Single-Stage Makgeolli ===
            val recipe1Id = recipeDao.insertRecipe(
                Recipe(
                    name = "Basic Single-Stage Makgeolli",
                    description = "A simple, quick makgeolli perfect for beginners. Made with a single fermentation stage, this produces a light, slightly sweet rice wine with moderate alcohol content.",
                    numberOfStages = 1,
                    filteringDays = 7,
                    category = "Makgeolli - Single Stage"
                )
            )
            recipeStageDao.insertStage(
                RecipeStage(
                    recipeId = recipe1Id,
                    stageNumber = 1,
                    riceAmountKg = 3.0, // 2kg white rice + 1kg sweet rice
                    waterAmountLiters = 4.5,
                    nurukAmountGrams = 300.0,
                    instructions = """1. Rinse rice thoroughly until water runs clear
2. Soak rice in cold water for 3-4 hours
3. Steam rice for 30-40 minutes until fully cooked
4. Cool rice to room temperature (20-25°C)
5. In sanitized fermentation vessel, combine rice, crushed nuruk, and water
6. Mix thoroughly and cover with breathable cloth
7. Ferment at 20-25°C for 7 days, stirring daily for first 3 days"""
                )
            )

            // === RECIPE 2: Traditional Two-Stage Makgeolli ===
            val recipe2Id = recipeDao.insertRecipe(
                Recipe(
                    name = "Traditional Two-Stage Makgeolli",
                    description = "Traditional two-stage fermentation for a more refined, balanced makgeolli. The first stage creates a starter, which is then used to ferment the main batch. Results in cleaner flavor and higher alcohol content.",
                    numberOfStages = 2,
                    filteringDays = 14,
                    category = "Makgeolli - Two Stage"
                )
            )
            recipeStageDao.insertStages(
                listOf(
                    RecipeStage(
                        recipeId = recipe2Id,
                        stageNumber = 1,
                        riceAmountKg = 0.5,
                        waterAmountLiters = 1.0,
                        nurukAmountGrams = 150.0,
                        instructions = """1. Rinse 500g rice thoroughly and soak for 3-4 hours
2. Steam rice for 30 minutes until fully cooked
3. Cool rice to room temperature (20-25°C)
4. In sanitized container, mix cooled rice, crushed nuruk, and water
5. Mix well, cover with breathable cloth
6. Store at 20-25°C and stir once daily for 4-5 days"""
                    ),
                    RecipeStage(
                        recipeId = recipe2Id,
                        stageNumber = 2,
                        daysFromStart = 5,
                        waterAmountLiters = 4.0,
                        nurukAmountGrams = 150.0,
                        instructions = """1. Prepare 3kg rice (2kg white + 1kg sweet): rinse, soak 3-4 hours, steam 35-40 min
2. Cool rice completely to room temperature
3. Add cooled rice to the starter vessel
4. Add crushed nuruk and water
5. Mix everything thoroughly
6. Cover and ferment at 20-25°C
7. Stir gently once daily for the next 8-9 days"""
                    )
                )
            )

            // === RECIPE 3: Premium Three-Stage Makgeolli ===
            val recipe3Id = recipeDao.insertRecipe(
                Recipe(
                    name = "Premium Three-Stage Makgeolli",
                    description = "Advanced three-stage fermentation for premium, sophisticated makgeolli. Creates the most refined flavor with highest alcohol content and exceptional balance. This traditional method was historically reserved for royalty and special occasions.",
                    numberOfStages = 3,
                    filteringDays = 21,
                    category = "Makgeolli - Three Stage"
                )
            )
            recipeStageDao.insertStages(
                listOf(
                    RecipeStage(
                        recipeId = recipe3Id,
                        stageNumber = 1,
                        riceAmountKg = 0.5,
                        waterAmountLiters = 1.0,
                        nurukAmountGrams = 150.0,
                        instructions = """1. Rinse 500g rice until water runs clear
2. Soak in cold water for 3-4 hours
3. Steam rice for 30 minutes
4. Cool to 20-25°C on clean tray
5. In sanitized vessel, combine rice, crushed nuruk, and water
6. Mix thoroughly, cover with breathable cloth
7. Ferment at 20-25°C, stirring once daily"""
                    ),
                    RecipeStage(
                        recipeId = recipe3Id,
                        stageNumber = 2,
                        daysFromStart = 5,
                        waterAmountLiters = 2.5,
                        nurukAmountGrams = 100.0,
                        instructions = """1. Prepare 2kg rice (1.5kg white + 500g sweet): rinse, soak 3-4 hours, steam 35 min
2. Cool rice completely to room temperature
3. Add cooled rice to Stage 1 starter
4. Add crushed nuruk and water
5. Mix well, cover, and ferment at 20-25°C
6. Stir daily for first 3 days"""
                    ),
                    RecipeStage(
                        recipeId = recipe3Id,
                        stageNumber = 3,
                        daysFromStart = 12,
                        waterAmountLiters = 5.0,
                        nurukAmountGrams = 150.0,
                        instructions = """1. Prepare 4.5kg rice (3kg white + 1.5kg sweet): rinse, soak 3-4 hours, steam 40 min
2. Cool rice completely - critical for success
3. Add cooled rice to fermentation vessel (may need larger container)
4. Add crushed nuruk and water
5. Mix thoroughly but gently
6. Cover and ferment at 20-25°C
7. Stir daily for first 5 days
8. When bubbling stops (around day 21 total), ready to strain"""
                    )
                )
            )
        }

        fun getDatabase(context: Context): TakTakDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TakTakDatabase::class.java,
                    "taktak_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(databaseCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
