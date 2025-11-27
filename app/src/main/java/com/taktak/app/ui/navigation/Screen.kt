package com.taktak.app.ui.navigation

sealed class Screen(val route: String) {
    object Recipes : Screen("recipes")
    object RecipeDetail : Screen("recipe/{recipeId}") {
        fun createRoute(recipeId: Long) = "recipe/$recipeId"
    }
    object AddEditRecipe : Screen("add_edit_recipe?recipeId={recipeId}") {
        fun createRoute(recipeId: Long? = null) = if (recipeId != null) {
            "add_edit_recipe?recipeId=$recipeId"
        } else {
            "add_edit_recipe"
        }
    }

    object Batches : Screen("batches")
    object BatchDetail : Screen("batch/{batchId}") {
        fun createRoute(batchId: Long) = "batch/$batchId"
    }
    object AddEditBatch : Screen("add_edit_batch?batchId={batchId}") {
        fun createRoute(batchId: Long? = null) = if (batchId != null) {
            "add_edit_batch?batchId=$batchId"
        } else {
            "add_edit_batch"
        }
    }

    object TastingNotes : Screen("tasting_notes")
    object AddEditTastingNote : Screen("add_edit_tasting_note?noteId={noteId}&batchId={batchId}") {
        fun createRoute(noteId: Long? = null, batchId: Long? = null): String {
            val params = mutableListOf<String>()
            if (noteId != null) params.add("noteId=$noteId")
            if (batchId != null) params.add("batchId=$batchId")
            return if (params.isNotEmpty()) {
                "add_edit_tasting_note?${params.joinToString("&")}"
            } else {
                "add_edit_tasting_note"
            }
        }
    }

    object Journal : Screen("journal")
    object AddEditJournalEntry : Screen("add_edit_journal?entryId={entryId}") {
        fun createRoute(entryId: Long? = null) = if (entryId != null) {
            "add_edit_journal?entryId=$entryId"
        } else {
            "add_edit_journal"
        }
    }
}
