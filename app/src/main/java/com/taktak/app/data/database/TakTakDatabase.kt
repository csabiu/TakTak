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
    version = 3,
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
            // Basic Single-Stage Makgeolli
            recipeDao.insertRecipe(
                Recipe(
                    name = "Basic Single-Stage Makgeolli (단양주)",
                    description = "A simple, quick makgeolli perfect for beginners. Made with a single fermentation stage, this produces a light, slightly sweet rice wine with moderate alcohol content.",
                    riceAmount = "2kg white rice (멥쌀) + 1kg sweet rice (찹쌀)",
                    waterAmount = "4.5L filtered or spring water",
                    nurukAmount = "300g nuruk (누룩)",
                    additionalIngredients = "Optional: 50g sugar or honey for extra sweetness",
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

            // Quick Single-Stage Makgeolli
            recipeDao.insertRecipe(
                Recipe(
                    name = "Quick Single-Stage Makgeolli",
                    description = "An even simpler version for first-time brewers. Uses less rice and a shorter fermentation time for quick results.",
                    riceAmount = "1.5kg short-grain white rice",
                    waterAmount = "3L filtered water",
                    nurukAmount = "200g nuruk",
                    additionalIngredients = "",
                    instructions = """
                        Day 1:
                        1. Rinse rice thoroughly until water runs clear
                        2. Soak in cold water for 2-3 hours
                        3. Steam for 25-30 minutes until cooked through
                        4. Cool rice to room temperature on a clean tray
                        5. In sanitized container, mix rice, crushed nuruk, and water
                        6. Cover with breathable cloth and ferment at 22-25°C

                        Days 2-5:
                        7. Stir once daily
                        8. Strong fermentation with bubbling occurs
                        9. Sweet, yeasty aroma develops

                        Days 5-7:
                        10. Taste daily - when pleasantly sweet and tangy, it's ready
                        11. Strain through cheesecloth
                        12. Refrigerate and consume within 10 days

                        Notes:
                        - Yields about 2-3L of makgeolli
                        - Alcohol content: 5-7%
                        - Best served chilled
                    """.trimIndent(),
                    fermentationTimeDays = 5,
                    category = "Makgeolli - Single Stage"
                )
            )

            // Traditional Two-Stage Makgeolli
            recipeDao.insertRecipe(
                Recipe(
                    name = "Traditional Two-Stage Makgeolli (이양주)",
                    description = "Traditional two-stage fermentation for a more refined, balanced makgeolli. The first stage creates a starter (밑술), which is then used to ferment the main batch (덧술). Results in cleaner flavor and higher alcohol content.",
                    riceAmount = "Stage 1: 500g white rice | Stage 2: 2kg white rice + 1kg sweet rice",
                    waterAmount = "Stage 1: 1L | Stage 2: 4L",
                    nurukAmount = "Stage 1: 150g | Stage 2: 150g",
                    additionalIngredients = "",
                    instructions = """
                        STAGE 1 - Starter (밑술) - Days 1-5:

                        Day 1:
                        1. Rinse 500g rice thoroughly and soak for 3-4 hours
                        2. Steam rice for 30 minutes until fully cooked
                        3. Cool rice to room temperature (20-25°C)
                        4. In sanitized container, mix cooled rice, 150g crushed nuruk, and 1L water
                        5. Mix well, cover with breathable cloth
                        6. Store at 20-25°C (68-77°F)

                        Days 2-5:
                        7. Stir once daily
                        8. After 4-5 days, starter should smell sweet and alcoholic with visible bubbling

                        STAGE 2 - Main Fermentation (덧술) - Days 6-14:

                        Day 6:
                        9. Prepare 2kg white rice + 1kg sweet rice (rinse, soak 3-4 hours, steam 35-40 min)
                        10. Cool rice completely to room temperature
                        11. Add cooled rice to the starter vessel
                        12. Add 150g crushed nuruk and 4L water
                        13. Mix everything thoroughly
                        14. Cover and return to 20-25°C fermentation area

                        Days 7-10:
                        15. Stir gently once daily
                        16. Strong bubbling and sweet-sour aroma will develop
                        17. Surface may develop white foam - this is normal

                        Days 11-14:
                        18. Fermentation will slow down
                        19. Taste test - should be balanced sweet-tart with good body
                        20. When bubbling stops, strain through fine cheesecloth
                        21. Press rice solids gently to extract liquid
                        22. Refrigerate strained makgeolli

                        Notes:
                        - Two-stage process creates 8-12% alcohol content
                        - More complex flavor profile with better clarity
                        - Can age in refrigerator for 3-4 weeks
                    """.trimIndent(),
                    fermentationTimeDays = 14,
                    category = "Makgeolli - Two Stage"
                )
            )

            // Fruit-Infused Two-Stage Makgeolli
            recipeDao.insertRecipe(
                Recipe(
                    name = "Fruit-Infused Two-Stage Makgeolli",
                    description = "A modern twist on traditional two-stage makgeolli with fruit additions for unique flavors. Great for experimenting with seasonal fruits.",
                    riceAmount = "Stage 1: 400g white rice | Stage 2: 1.5kg white rice + 500g sweet rice",
                    waterAmount = "Stage 1: 800ml | Stage 2: 3L",
                    nurukAmount = "Stage 1: 120g | Stage 2: 120g",
                    additionalIngredients = "500g fruit of choice (strawberries, peaches, plums, or apples), cut into small pieces",
                    instructions = """
                        STAGE 1 - Starter (Days 1-5):
                        1. Prepare 400g rice: rinse, soak 3 hours, steam 30 min
                        2. Cool to room temperature
                        3. Mix with 120g crushed nuruk and 800ml water in sanitized container
                        4. Cover and ferment at 22-25°C for 4-5 days, stirring daily

                        STAGE 2 - Main Fermentation with Fruit (Days 6-12):
                        5. Prepare 1.5kg white rice + 500g sweet rice (rinse, soak, steam 35 min)
                        6. Cool rice completely
                        7. Add cooled rice to starter
                        8. Add 120g crushed nuruk and 3L water
                        9. Add 500g prepared fruit (cleaned and cut into small pieces)
                        10. Mix gently but thoroughly
                        11. Cover and ferment at 22-25°C

                        Days 7-9:
                        12. Stir daily - fruit flavors will infuse
                        13. Strong fermentation with fruity aromas

                        Days 10-12:
                        14. Taste test - should have balanced rice and fruit flavors
                        15. Strain through fine mesh or cheesecloth
                        16. Can strain multiple times for clarity
                        17. Refrigerate and consume within 2-3 weeks

                        Fruit Recommendations:
                        - Strawberries: sweet and aromatic
                        - Peaches: smooth and fragrant
                        - Plums (maesil): tart and refreshing
                        - Apples: crisp and clean
                    """.trimIndent(),
                    fermentationTimeDays = 12,
                    category = "Makgeolli - Two Stage"
                )
            )

            // Premium Three-Stage Makgeolli
            recipeDao.insertRecipe(
                Recipe(
                    name = "Premium Three-Stage Makgeolli (삼양주)",
                    description = "Advanced three-stage fermentation for premium, sophisticated makgeolli. Creates the most refined flavor with highest alcohol content and exceptional balance. This traditional method was historically reserved for royalty and special occasions.",
                    riceAmount = "Stage 1: 500g white rice | Stage 2: 1.5kg white rice + 500g sweet rice | Stage 3: 3kg white rice + 1.5kg sweet rice",
                    waterAmount = "Stage 1: 1L | Stage 2: 2.5L | Stage 3: 5L",
                    nurukAmount = "Stage 1: 150g | Stage 2: 100g | Stage 3: 150g",
                    additionalIngredients = "Optional: 200ml honey for Stage 3 for premium sweetness",
                    instructions = """
                        STAGE 1 - First Starter (밑술) - Days 1-5:

                        Day 1:
                        1. Rinse 500g rice until water runs clear
                        2. Soak in cold water for 3-4 hours
                        3. Steam rice for 30 minutes
                        4. Spread on clean tray and cool to 20-25°C
                        5. In sanitized fermentation vessel, combine rice, 150g crushed nuruk, and 1L water
                        6. Mix thoroughly, cover with breathable cloth
                        7. Ferment at 20-25°C (68-77°F)

                        Days 2-5:
                        8. Stir once daily with clean utensil
                        9. After 4-5 days, should smell sweet and alcoholic

                        STAGE 2 - Second Build (두번덧술) - Days 6-12:

                        Day 6:
                        10. Prepare 1.5kg white rice + 500g sweet rice (rinse, soak 3-4 hours, steam 35 min)
                        11. Cool rice completely to room temperature
                        12. Add cooled rice to Stage 1 starter
                        13. Add 100g crushed nuruk and 2.5L water
                        14. Mix well, cover, and ferment at 20-25°C

                        Days 7-12:
                        15. Stir daily for first 3 days
                        16. Strong fermentation with bubbling and foam
                        17. Sweet, fruity aroma develops

                        STAGE 3 - Final Build (세번덧술) - Days 13-21:

                        Day 13:
                        18. Prepare largest rice batch: 3kg white + 1.5kg sweet rice
                        19. Rinse, soak 3-4 hours, steam 40 minutes
                        20. Cool rice completely - this is critical for success
                        21. Add cooled rice to fermentation vessel (may need larger container)
                        22. Add 150g crushed nuruk and 5L water
                        23. Optional: Add 200ml honey for premium flavor
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
                        - Temperature consistency is crucial throughout all stages
                    """.trimIndent(),
                    fermentationTimeDays = 21,
                    category = "Makgeolli - Three Stage"
                )
            )

            // Sweet Rice Three-Stage Makgeolli
            recipeDao.insertRecipe(
                Recipe(
                    name = "Sweet Rice Three-Stage Makgeolli (찹쌀삼양주)",
                    description = "A sweeter, smoother variation of three-stage makgeolli using more glutinous rice. Creates a silky texture and naturally sweeter flavor profile.",
                    riceAmount = "Stage 1: 400g sweet rice | Stage 2: 1kg white rice + 800g sweet rice | Stage 3: 2kg white rice + 2kg sweet rice",
                    waterAmount = "Stage 1: 900ml | Stage 2: 2.5L | Stage 3: 5L",
                    nurukAmount = "Stage 1: 130g | Stage 2: 120g | Stage 3: 150g",
                    additionalIngredients = "",
                    instructions = """
                        STAGE 1 - Sweet Rice Starter (Days 1-5):
                        1. Rinse 400g sweet rice until water runs clear
                        2. Soak for 4-5 hours (sweet rice needs longer soaking)
                        3. Steam for 35 minutes until very soft
                        4. Cool to room temperature (20-25°C)
                        5. Mix with 130g crushed nuruk and 900ml water
                        6. Cover and ferment at 22-25°C
                        7. Stir daily - sweet rice creates thicker consistency

                        STAGE 2 - Second Build (Days 6-11):
                        8. Prepare 1kg white rice + 800g sweet rice
                        9. Rinse, soak (3 hours white, 4-5 hours sweet), steam together 35-40 min
                        10. Cool completely to room temperature
                        11. Add to Stage 1 starter
                        12. Add 120g crushed nuruk and 2.5L water
                        13. Mix thoroughly - mixture will be thick
                        14. Cover and ferment at 22-25°C
                        15. Stir daily for first 4 days

                        STAGE 3 - Final Sweet Build (Days 12-20):
                        16. Prepare 2kg white rice + 2kg sweet rice
                        17. Rinse and soak separately (white 3 hours, sweet 5 hours)
                        18. Steam together for 40 minutes
                        19. Cool rice completely - critical step
                        20. Add to fermentation vessel (use large container)
                        21. Add 150g crushed nuruk and 5L water
                        22. Mix gently but thoroughly
                        23. Cover and ferment at 22-25°C

                        Days 13-17:
                        24. Stir daily - very active fermentation
                        25. Creamy, sweet aroma develops
                        26. Thick, silky texture forms

                        Days 18-20:
                        27. Fermentation slows
                        28. Taste should be smooth, sweet, balanced
                        29. Strain through cheesecloth when bubbling stops
                        30. Refrigerate in sealed containers

                        Notes:
                        - Higher sweet rice ratio creates silkier texture
                        - Natural sweetness, less acidic than standard makgeolli
                        - Alcohol content: 11-14%
                        - Best consumed within 4-5 weeks
                    """.trimIndent(),
                    fermentationTimeDays = 20,
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
