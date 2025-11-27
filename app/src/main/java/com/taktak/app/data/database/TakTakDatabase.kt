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
        Batch::class,
        TastingNote::class,
        JournalEntry::class,
        AlarmItem::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TakTakDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun batchDao(): BatchDao
    abstract fun tastingNoteDao(): TastingNoteDao
    abstract fun journalEntryDao(): JournalEntryDao
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
                        populateDatabase(database.recipeDao())
                    }
                }
            }
        }

        private suspend fun populateDatabase(recipeDao: RecipeDao) {
            // Single-stage makgeolli recipe
            recipeDao.insertRecipe(
                Recipe(
                    name = "Single-Stage Makgeolli (단양주)",
                    description = "A simple, quick makgeolli perfect for beginners. Made with a single fermentation stage, this produces a light, slightly sweet rice wine with moderate alcohol content.",
                    ingredients = """
                        - 2 cups short-grain white rice (멥쌀)
                        - 2 cups sweet/glutinous rice (찹쌀)
                        - 1 cup nuruk (Korean fermentation starter, 누룩)
                        - 6 cups water (spring or filtered)
                        - Optional: 1/4 cup sugar or honey for sweetness
                    """.trimIndent(),
                    instructions = """
                        Day 1: Prepare the Rice
                        1. Rinse both types of rice thoroughly until water runs clear
                        2. Soak rice in cold water for 3-4 hours
                        3. Drain and steam rice for 30-40 minutes until fully cooked
                        4. Spread cooked rice on a clean tray and cool to room temperature (20-25°C)

                        Day 1-2: Mix and Ferment
                        5. In a sanitized fermentation vessel, combine cooled rice, crushed nuruk, and water
                        6. Mix thoroughly with clean hands or sanitized utensil
                        7. Cover with breathable cloth and secure with rubber band
                        8. Store at 20-25°C (68-77°F) for initial fermentation

                        Day 3-7: Active Fermentation
                        9. Stir gently once daily for the first 3 days
                        10. You'll see bubbling and smell sweet, yeasty aromas
                        11. Taste after 5 days - it should be slightly sweet and fizzy

                        Day 7-10: Completion
                        12. When fermentation slows (fewer bubbles), strain through cheesecloth
                        13. Store strained makgeolli in refrigerator
                        14. Consume within 2 weeks for best flavor

                        Notes:
                        - Temperature is critical - too cold slows fermentation, too hot creates off flavors
                        - Single-stage fermentation produces 6-8% alcohol content
                        - The result is milky-white, slightly sweet, and effervescent
                    """.trimIndent(),
                    fermentationTimeDays = 7,
                    category = "Makgeolli - Single Stage"
                )
            )

            // Two-stage makgeolli recipe
            recipeDao.insertRecipe(
                Recipe(
                    name = "Two-Stage Makgeolli (이양주)",
                    description = "Traditional two-stage fermentation for a more refined, balanced makgeolli. The first stage creates a starter (밑술), which is then used to ferment the main batch (덧술). Results in cleaner flavor and higher alcohol content.",
                    ingredients = """
                        Stage 1 - Starter (밑술):
                        - 1 cup short-grain white rice
                        - 1/2 cup nuruk
                        - 1.5 cups water

                        Stage 2 - Main Fermentation (덧술):
                        - 3 cups short-grain white rice
                        - 2 cups sweet/glutinous rice
                        - 1/2 cup nuruk
                        - 6 cups water
                        - All of the starter from Stage 1
                    """.trimIndent(),
                    instructions = """
                        STAGE 1 - Starter (밑술) - Days 1-5:

                        Day 1:
                        1. Rinse 1 cup rice thoroughly and soak for 3-4 hours
                        2. Steam rice for 30 minutes until fully cooked
                        3. Cool rice to room temperature (20-25°C)
                        4. In sanitized container, mix cooled rice, 1/2 cup crushed nuruk, and 1.5 cups water
                        5. Mix well, cover with breathable cloth
                        6. Store at 20-25°C (68-77°F)

                        Days 2-5:
                        7. Stir once daily
                        8. After 3-5 days, starter should smell sweet and alcoholic with visible bubbling
                        9. This is your base starter (밑술) - do not strain yet

                        STAGE 2 - Main Fermentation (덧술) - Days 6-14:

                        Day 6:
                        10. Prepare main batch rice: rinse 3 cups white rice + 2 cups sweet rice
                        11. Soak for 3-4 hours, then steam for 35-40 minutes
                        12. Cool rice completely to room temperature
                        13. Add cooled rice to the starter vessel
                        14. Add 1/2 cup crushed nuruk and 6 cups water
                        15. Mix everything thoroughly
                        16. Cover and return to 20-25°C fermentation area

                        Days 7-10:
                        17. Stir gently once daily
                        18. Strong bubbling and sweet-sour aroma will develop
                        19. Surface may develop white foam - this is normal

                        Days 10-14:
                        20. Fermentation will slow down
                        21. Taste test - should be balanced sweet-tart with good body
                        22. When bubbling stops, strain through fine cheesecloth
                        23. Press rice solids gently to extract liquid
                        24. Refrigerate strained makgeolli

                        Notes:
                        - Two-stage process creates 8-12% alcohol content
                        - More complex flavor profile with better clarity
                        - Can age in refrigerator for 3-4 weeks
                        - The rice sediment (지게미) can be used for cooking or pickling
                    """.trimIndent(),
                    fermentationTimeDays = 14,
                    category = "Makgeolli - Two Stage"
                )
            )

            // Three-stage makgeolli recipe
            recipeDao.insertRecipe(
                Recipe(
                    name = "Three-Stage Makgeolli (삼양주)",
                    description = "Advanced three-stage fermentation for premium, sophisticated makgeolli. Creates the most refined flavor with highest alcohol content and exceptional balance. This traditional method was historically reserved for royalty and special occasions.",
                    ingredients = """
                        Stage 1 - First Starter (밑술):
                        - 1 cup short-grain white rice
                        - 1/3 cup nuruk
                        - 1.5 cups water

                        Stage 2 - Second Build (두번덧술):
                        - 2 cups short-grain white rice
                        - 1 cup sweet/glutinous rice
                        - 1/4 cup nuruk
                        - 3 cups water

                        Stage 3 - Final Build (세번덧술):
                        - 4 cups short-grain white rice
                        - 2 cups sweet/glutinous rice
                        - 1/3 cup nuruk
                        - 7 cups water
                        - Optional: 1/2 cup honey for premium sweetness
                    """.trimIndent(),
                    instructions = """
                        STAGE 1 - First Starter (밑술) - Days 1-5:

                        Day 1:
                        1. Rinse 1 cup rice until water runs clear
                        2. Soak in cold water for 3-4 hours
                        3. Steam rice for 30 minutes
                        4. Spread on clean tray and cool to 20-25°C
                        5. In sanitized fermentation vessel, combine rice, 1/3 cup crushed nuruk, and 1.5 cups water
                        6. Mix thoroughly, cover with breathable cloth
                        7. Ferment at 20-25°C (68-77°F)

                        Days 2-5:
                        8. Stir once daily with clean utensil
                        9. After 4-5 days, should smell sweet and alcoholic

                        STAGE 2 - Second Build - Days 6-12:

                        Day 6:
                        10. Prepare 2 cups white rice + 1 cup sweet rice (rinse, soak 3-4 hours, steam 35 min)
                        11. Cool rice completely to room temperature
                        12. Add cooled rice to Stage 1 starter
                        13. Add 1/4 cup crushed nuruk and 3 cups water
                        14. Mix well, cover, and ferment at 20-25°C

                        Days 7-12:
                        15. Stir daily for first 3 days
                        16. Strong fermentation with bubbling and foam
                        17. Sweet, fruity aroma develops

                        STAGE 3 - Final Build - Days 13-21:

                        Day 13:
                        18. Prepare largest rice batch: 4 cups white + 2 cups sweet rice
                        19. Rinse, soak 3-4 hours, steam 40 minutes
                        20. Cool rice completely - this is critical for success
                        21. Add cooled rice to fermentation vessel (may need larger container)
                        22. Add 1/3 cup crushed nuruk and 7 cups water
                        23. Optional: Add 1/2 cup honey for premium flavor
                        24. Mix everything thoroughly but gently
                        25. Cover and ferment at 20-25°C

                        Days 14-18:
                        26. Stir daily - vigorous bubbling will occur
                        27. Volume increases significantly
                        28. Complex, layered aromas develop

                        Days 19-21:
                        29. Fermentation gradually slows
                        30. Taste test - should be refined, balanced, with pleasant sweetness
                        31. When bubbling stops (around day 21), ready to strain

                        Finishing:
                        32. Strain through multiple layers of fine cheesecloth
                        33. Press rice solids gently - don't force
                        34. Let strained makgeolli settle in refrigerator for 24 hours
                        35. Can carefully decant clear liquid from sediment for premium clarity
                        36. Store refrigerated in sealed bottles

                        Notes:
                        - Three-stage creates 12-15% alcohol content
                        - Most complex, refined flavor profile
                        - Can age 4-6 weeks for peak flavor development
                        - Each stage builds on previous flavors, creating depth
                        - Temperature consistency is crucial throughout all stages
                        - Sanitation is critical - use clean utensils and containers
                        - Traditional method used for Korean royal court makgeolli
                        - The longer process allows better flavor development and smoother finish
                    """.trimIndent(),
                    fermentationTimeDays = 21,
                    category = "Makgeolli - Three Stage"
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
