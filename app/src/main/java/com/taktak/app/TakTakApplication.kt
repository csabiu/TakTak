package com.taktak.app

import android.app.Application
import com.taktak.app.data.database.TakTakDatabase
import com.taktak.app.data.repository.TakTakRepository

class TakTakApplication : Application() {
    val database by lazy { TakTakDatabase.getDatabase(this) }
    val repository by lazy {
        TakTakRepository(
            database.recipeDao(),
            database.batchDao(),
            database.tastingNoteDao(),
            database.journalEntryDao()
        )
    }
}
