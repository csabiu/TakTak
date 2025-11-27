package com.taktak.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.taktak.app.data.dao.*
import com.taktak.app.data.model.*

@Database(
    entities = [
        Recipe::class,
        Batch::class,
        TastingNote::class,
        JournalEntry::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TakTakDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun batchDao(): BatchDao
    abstract fun tastingNoteDao(): TastingNoteDao
    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        @Volatile
        private var INSTANCE: TakTakDatabase? = null

        fun getDatabase(context: Context): TakTakDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TakTakDatabase::class.java,
                    "taktak_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
