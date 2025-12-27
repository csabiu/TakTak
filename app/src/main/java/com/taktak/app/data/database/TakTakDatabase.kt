package com.taktak.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
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
    version = 10,
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

        // 마이그레이션 8에서 9로: 기본 레시피를 한국어로 업데이트
        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // numberOfStages를 사용하여 각 레시피 식별 (더 안정적)

                // 레시피 1: 기본 단양주 (numberOfStages = 1)
                database.execSQL("""
                    UPDATE recipes SET
                        name = '기본 단양주',
                        description = '초보자에게 완벽한 간단하고 빠른 막걸리입니다. 한 번의 발효 과정으로 만들어지며, 가볍고 약간 달콤한 쌀술이 됩니다.',
                        category = '막걸리 - 단양주'
                    WHERE numberOfStages = 1 AND filteringDays = 7
                """)

                database.execSQL("""
                    UPDATE recipe_stages SET
                        instructions = '1. 쌀을 물이 맑아질 때까지 깨끗이 씻기
2. 찬물에 3-4시간 불리기
3. 30-40분간 쪄서 완전히 익히기
4. 실온(20-25°C)으로 식히기
5. 소독된 발효 용기에 쌀, 부순 누룩, 물을 넣기
6. 잘 섞고 통기성 있는 천으로 덮기
7. 20-25°C에서 7일간 발효 (처음 3일은 매일 저어주기)'
                    WHERE recipeId IN (SELECT id FROM recipes WHERE numberOfStages = 1 AND filteringDays = 7)
                    AND stageNumber = 1
                """)

                // 레시피 2: 전통 이양주 (numberOfStages = 2)
                database.execSQL("""
                    UPDATE recipes SET
                        name = '전통 이양주',
                        description = '더 세련되고 균형 잡힌 막걸리를 위한 전통 2단 발효법입니다. 첫 단계에서 밑술을 만들고, 이를 사용해 본 발효를 진행합니다. 깨끗한 맛과 높은 도수를 냅니다.',
                        category = '막걸리 - 이양주'
                    WHERE numberOfStages = 2 AND filteringDays = 14
                """)

                database.execSQL("""
                    UPDATE recipe_stages SET
                        instructions = '1. 쌀 500g을 깨끗이 씻고 3-4시간 불리기
2. 30분간 쪄서 완전히 익히기
3. 실온(20-25°C)으로 식히기
4. 소독된 용기에 식힌 쌀, 부순 누룩, 물을 섞기
5. 잘 섞고 통기성 있는 천으로 덮기
6. 20-25°C에서 보관하며 4-5일간 매일 한 번씩 저어주기'
                    WHERE recipeId IN (SELECT id FROM recipes WHERE numberOfStages = 2 AND filteringDays = 14)
                    AND stageNumber = 1
                """)

                database.execSQL("""
                    UPDATE recipe_stages SET
                        instructions = '1. 쌀 3kg (백미 2kg + 찹쌀 1kg) 준비: 씻고, 3-4시간 불리고, 35-40분 찌기
2. 쌀을 완전히 실온으로 식히기
3. 밑술에 식힌 쌀 넣기
4. 부순 누룩과 물 넣기
5. 모든 재료를 잘 섞기
6. 덮고 20-25°C에서 발효
7. 다음 8-9일간 매일 한 번씩 부드럽게 저어주기'
                    WHERE recipeId IN (SELECT id FROM recipes WHERE numberOfStages = 2 AND filteringDays = 14)
                    AND stageNumber = 2
                """)

                // 레시피 3: 프리미엄 삼양주 (numberOfStages = 3)
                database.execSQL("""
                    UPDATE recipes SET
                        name = '프리미엄 삼양주',
                        description = '프리미엄 고급 막걸리를 위한 3단 발효법입니다. 가장 세련된 맛과 높은 도수, 뛰어난 균형을 만듭니다. 이 전통 방식은 역사적으로 왕실과 특별한 행사를 위해 사용되었습니다.',
                        category = '막걸리 - 삼양주'
                    WHERE numberOfStages = 3 AND filteringDays = 21
                """)

                database.execSQL("""
                    UPDATE recipe_stages SET
                        instructions = '1. 쌀 500g을 물이 맑아질 때까지 씻기
2. 찬물에 3-4시간 불리기
3. 30분간 찌기
4. 깨끗한 쟁반에서 20-25°C로 식히기
5. 소독된 용기에 쌀, 부순 누룩, 물을 넣기
6. 잘 섞고 통기성 있는 천으로 덮기
7. 20-25°C에서 발효하며 매일 한 번씩 저어주기'
                    WHERE recipeId IN (SELECT id FROM recipes WHERE numberOfStages = 3 AND filteringDays = 21)
                    AND stageNumber = 1
                """)

                database.execSQL("""
                    UPDATE recipe_stages SET
                        instructions = '1. 쌀 2kg (백미 1.5kg + 찹쌀 500g) 준비: 씻고, 3-4시간 불리고, 35분 찌기
2. 쌀을 완전히 실온으로 식히기
3. 1단계 밑술에 식힌 쌀 넣기
4. 부순 누룩과 물 넣기
5. 잘 섞고 덮어서 20-25°C에서 발효
6. 처음 3일간 매일 저어주기'
                    WHERE recipeId IN (SELECT id FROM recipes WHERE numberOfStages = 3 AND filteringDays = 21)
                    AND stageNumber = 2
                """)

                database.execSQL("""
                    UPDATE recipe_stages SET
                        instructions = '1. 쌀 4.5kg (백미 3kg + 찹쌀 1.5kg) 준비: 씻고, 3-4시간 불리고, 40분 찌기
2. 쌀을 완전히 식히기 - 성공의 핵심
3. 발효 용기에 식힌 쌀 넣기 (더 큰 용기가 필요할 수 있음)
4. 부순 누룩과 물 넣기
5. 부드럽게 잘 섞기
6. 덮고 20-25°C에서 발효
7. 처음 5일간 매일 저어주기
8. 거품이 멈추면 (약 21일째) 거르기'
                    WHERE recipeId IN (SELECT id FROM recipes WHERE numberOfStages = 3 AND filteringDays = 21)
                    AND stageNumber = 3
                """)
            }
        }

        // 마이그레이션 9에서 10으로: 레시피 2와 3을 한국어로 업데이트 (수정된 버전)
        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 레시피 2: 전통 이양주 (numberOfStages = 2)
                database.execSQL("""
                    UPDATE recipes SET
                        name = '전통 이양주',
                        description = '더 세련되고 균형 잡힌 막걸리를 위한 전통 2단 발효법입니다. 첫 단계에서 밑술을 만들고, 이를 사용해 본 발효를 진행합니다. 깨끗한 맛과 높은 도수를 냅니다.',
                        category = '막걸리 - 이양주'
                    WHERE numberOfStages = 2 AND filteringDays = 14
                """)

                database.execSQL("""
                    UPDATE recipe_stages SET
                        instructions = '1. 쌀 500g을 깨끗이 씻고 3-4시간 불리기
2. 30분간 쪄서 완전히 익히기
3. 실온(20-25°C)으로 식히기
4. 소독된 용기에 식힌 쌀, 부순 누룩, 물을 섞기
5. 잘 섞고 통기성 있는 천으로 덮기
6. 20-25°C에서 보관하며 4-5일간 매일 한 번씩 저어주기'
                    WHERE recipeId IN (SELECT id FROM recipes WHERE numberOfStages = 2 AND filteringDays = 14)
                    AND stageNumber = 1
                """)

                database.execSQL("""
                    UPDATE recipe_stages SET
                        instructions = '1. 쌀 3kg (백미 2kg + 찹쌀 1kg) 준비: 씻고, 3-4시간 불리고, 35-40분 찌기
2. 쌀을 완전히 실온으로 식히기
3. 밑술에 식힌 쌀 넣기
4. 부순 누룩과 물 넣기
5. 모든 재료를 잘 섞기
6. 덮고 20-25°C에서 발효
7. 다음 8-9일간 매일 한 번씩 부드럽게 저어주기'
                    WHERE recipeId IN (SELECT id FROM recipes WHERE numberOfStages = 2 AND filteringDays = 14)
                    AND stageNumber = 2
                """)

                // 레시피 3: 프리미엄 삼양주 (numberOfStages = 3)
                database.execSQL("""
                    UPDATE recipes SET
                        name = '프리미엄 삼양주',
                        description = '프리미엄 고급 막걸리를 위한 3단 발효법입니다. 가장 세련된 맛과 높은 도수, 뛰어난 균형을 만듭니다. 이 전통 방식은 역사적으로 왕실과 특별한 행사를 위해 사용되었습니다.',
                        category = '막걸리 - 삼양주'
                    WHERE numberOfStages = 3 AND filteringDays = 21
                """)

                database.execSQL("""
                    UPDATE recipe_stages SET
                        instructions = '1. 쌀 500g을 물이 맑아질 때까지 씻기
2. 찬물에 3-4시간 불리기
3. 30분간 찌기
4. 깨끗한 쟁반에서 20-25°C로 식히기
5. 소독된 용기에 쌀, 부순 누룩, 물을 넣기
6. 잘 섞고 통기성 있는 천으로 덮기
7. 20-25°C에서 발효하며 매일 한 번씩 저어주기'
                    WHERE recipeId IN (SELECT id FROM recipes WHERE numberOfStages = 3 AND filteringDays = 21)
                    AND stageNumber = 1
                """)

                database.execSQL("""
                    UPDATE recipe_stages SET
                        instructions = '1. 쌀 2kg (백미 1.5kg + 찹쌀 500g) 준비: 씻고, 3-4시간 불리고, 35분 찌기
2. 쌀을 완전히 실온으로 식히기
3. 1단계 밑술에 식힌 쌀 넣기
4. 부순 누룩과 물 넣기
5. 잘 섞고 덮어서 20-25°C에서 발효
6. 처음 3일간 매일 저어주기'
                    WHERE recipeId IN (SELECT id FROM recipes WHERE numberOfStages = 3 AND filteringDays = 21)
                    AND stageNumber = 2
                """)

                database.execSQL("""
                    UPDATE recipe_stages SET
                        instructions = '1. 쌀 4.5kg (백미 3kg + 찹쌀 1.5kg) 준비: 씻고, 3-4시간 불리고, 40분 찌기
2. 쌀을 완전히 식히기 - 성공의 핵심
3. 발효 용기에 식힌 쌀 넣기 (더 큰 용기가 필요할 수 있음)
4. 부순 누룩과 물 넣기
5. 부드럽게 잘 섞기
6. 덮고 20-25°C에서 발효
7. 처음 5일간 매일 저어주기
8. 거품이 멈추면 (약 21일째) 거르기'
                    WHERE recipeId IN (SELECT id FROM recipes WHERE numberOfStages = 3 AND filteringDays = 21)
                    AND stageNumber = 3
                """)
            }
        }

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
                        // 레시피 테이블이 비어있으면 기본 레시피로 채우기
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
            // === 레시피 1: 기본 단양주 ===
            val recipe1Id = recipeDao.insertRecipe(
                Recipe(
                    name = "기본 단양주",
                    description = "초보자에게 완벽한 간단하고 빠른 막걸리입니다. 한 번의 발효 과정으로 만들어지며, 가볍고 약간 달콤한 쌀술이 됩니다.",
                    numberOfStages = 1,
                    filteringDays = 7,
                    category = "막걸리 - 단양주"
                )
            )
            recipeStageDao.insertStage(
                RecipeStage(
                    recipeId = recipe1Id,
                    stageNumber = 1,
                    riceAmountKg = 3.0, // 백미 2kg + 찹쌀 1kg
                    waterAmountLiters = 4.5,
                    nurukAmountGrams = 300.0,
                    instructions = """1. 쌀을 물이 맑아질 때까지 깨끗이 씻기
2. 찬물에 3-4시간 불리기
3. 30-40분간 쪄서 완전히 익히기
4. 실온(20-25°C)으로 식히기
5. 소독된 발효 용기에 쌀, 부순 누룩, 물을 넣기
6. 잘 섞고 통기성 있는 천으로 덮기
7. 20-25°C에서 7일간 발효 (처음 3일은 매일 저어주기)"""
                )
            )

            // === 레시피 2: 전통 이양주 ===
            val recipe2Id = recipeDao.insertRecipe(
                Recipe(
                    name = "전통 이양주",
                    description = "더 세련되고 균형 잡힌 막걸리를 위한 전통 2단 발효법입니다. 첫 단계에서 밑술을 만들고, 이를 사용해 본 발효를 진행합니다. 깨끗한 맛과 높은 도수를 냅니다.",
                    numberOfStages = 2,
                    filteringDays = 14,
                    category = "막걸리 - 이양주"
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
                        instructions = """1. 쌀 500g을 깨끗이 씻고 3-4시간 불리기
2. 30분간 쪄서 완전히 익히기
3. 실온(20-25°C)으로 식히기
4. 소독된 용기에 식힌 쌀, 부순 누룩, 물을 섞기
5. 잘 섞고 통기성 있는 천으로 덮기
6. 20-25°C에서 보관하며 4-5일간 매일 한 번씩 저어주기"""
                    ),
                    RecipeStage(
                        recipeId = recipe2Id,
                        stageNumber = 2,
                        daysFromStart = 5,
                        waterAmountLiters = 4.0,
                        nurukAmountGrams = 150.0,
                        instructions = """1. 쌀 3kg (백미 2kg + 찹쌀 1kg) 준비: 씻고, 3-4시간 불리고, 35-40분 찌기
2. 쌀을 완전히 실온으로 식히기
3. 밑술에 식힌 쌀 넣기
4. 부순 누룩과 물 넣기
5. 모든 재료를 잘 섞기
6. 덮고 20-25°C에서 발효
7. 다음 8-9일간 매일 한 번씩 부드럽게 저어주기"""
                    )
                )
            )

            // === 레시피 3: 프리미엄 삼양주 ===
            val recipe3Id = recipeDao.insertRecipe(
                Recipe(
                    name = "프리미엄 삼양주",
                    description = "프리미엄 고급 막걸리를 위한 3단 발효법입니다. 가장 세련된 맛과 높은 도수, 뛰어난 균형을 만듭니다. 이 전통 방식은 역사적으로 왕실과 특별한 행사를 위해 사용되었습니다.",
                    numberOfStages = 3,
                    filteringDays = 21,
                    category = "막걸리 - 삼양주"
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
                        instructions = """1. 쌀 500g을 물이 맑아질 때까지 씻기
2. 찬물에 3-4시간 불리기
3. 30분간 찌기
4. 깨끗한 쟁반에서 20-25°C로 식히기
5. 소독된 용기에 쌀, 부순 누룩, 물을 넣기
6. 잘 섞고 통기성 있는 천으로 덮기
7. 20-25°C에서 발효하며 매일 한 번씩 저어주기"""
                    ),
                    RecipeStage(
                        recipeId = recipe3Id,
                        stageNumber = 2,
                        daysFromStart = 5,
                        waterAmountLiters = 2.5,
                        nurukAmountGrams = 100.0,
                        instructions = """1. 쌀 2kg (백미 1.5kg + 찹쌀 500g) 준비: 씻고, 3-4시간 불리고, 35분 찌기
2. 쌀을 완전히 실온으로 식히기
3. 1단계 밑술에 식힌 쌀 넣기
4. 부순 누룩과 물 넣기
5. 잘 섞고 덮어서 20-25°C에서 발효
6. 처음 3일간 매일 저어주기"""
                    ),
                    RecipeStage(
                        recipeId = recipe3Id,
                        stageNumber = 3,
                        daysFromStart = 12,
                        waterAmountLiters = 5.0,
                        nurukAmountGrams = 150.0,
                        instructions = """1. 쌀 4.5kg (백미 3kg + 찹쌀 1.5kg) 준비: 씻고, 3-4시간 불리고, 40분 찌기
2. 쌀을 완전히 식히기 - 성공의 핵심
3. 발효 용기에 식힌 쌀 넣기 (더 큰 용기가 필요할 수 있음)
4. 부순 누룩과 물 넣기
5. 부드럽게 잘 섞기
6. 덮고 20-25°C에서 발효
7. 처음 5일간 매일 저어주기
8. 거품이 멈추면 (약 21일째) 거르기"""
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
                    .addMigrations(MIGRATION_8_9, MIGRATION_9_10)
                    .fallbackToDestructiveMigration()
                    .addCallback(databaseCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
