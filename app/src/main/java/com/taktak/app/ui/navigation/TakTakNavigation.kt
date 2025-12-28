package com.taktak.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.taktak.app.data.repository.TakTakRepository
import com.taktak.app.ui.screens.batches.BatchDetailScreen
import com.taktak.app.ui.screens.batches.BatchListScreen
import com.taktak.app.ui.screens.batches.AddEditBatchScreen
import com.taktak.app.ui.screens.recipes.RecipeListScreen
import com.taktak.app.ui.screens.recipes.RecipeDetailScreen
import com.taktak.app.ui.screens.recipes.AddEditRecipeScreen
import com.taktak.app.ui.screens.tastings.TastingNoteListScreen
import com.taktak.app.ui.screens.tastings.AddEditTastingNoteScreen
import com.taktak.app.ui.screens.alarms.AddEditAlarmScreen

@Composable
fun TakTakNavigation(
    navController: NavHostController,
    repository: TakTakRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Recipes.route
    ) {
        // Recipes
        composable(Screen.Recipes.route) {
            RecipeListScreen(
                repository = repository,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onAddRecipe = {
                    navController.navigate(Screen.AddEditRecipe.createRoute())
                }
            )
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: return@composable
            RecipeDetailScreen(
                recipeId = recipeId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() },
                onEditRecipe = { id ->
                    navController.navigate(Screen.AddEditRecipe.createRoute(id))
                },
                onStartBatch = { id ->
                    navController.navigate(Screen.AddEditBatch.createRoute())
                },
                onViewRecipe = { id ->
                    navController.navigate(Screen.RecipeDetail.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.AddEditRecipe.route,
            arguments = listOf(navArgument("recipeId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId")?.takeIf { it != -1L }
            AddEditRecipeScreen(
                recipeId = recipeId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Batches
        composable(Screen.Batches.route) {
            BatchListScreen(
                repository = repository,
                onBatchClick = { batchId ->
                    navController.navigate(Screen.BatchDetail.createRoute(batchId))
                },
                onAddBatch = {
                    navController.navigate(Screen.AddEditBatch.createRoute())
                }
            )
        }

        composable(
            route = Screen.BatchDetail.route,
            arguments = listOf(navArgument("batchId") { type = NavType.LongType })
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getLong("batchId") ?: return@composable
            BatchDetailScreen(
                batchId = batchId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() },
                onEditBatch = { id ->
                    navController.navigate(Screen.AddEditBatch.createRoute(id))
                },
                onAddTastingNote = { id ->
                    navController.navigate(Screen.AddEditTastingNote.createRoute(batchId = id))
                },
                onAddAlarm = { id ->
                    navController.navigate(Screen.AddEditAlarm.createRoute(batchId = id))
                },
                onEditAlarm = { alarmId ->
                    navController.navigate(Screen.AddEditAlarm.createRoute(alarmId = alarmId))
                }
            )
        }

        composable(
            route = Screen.AddEditBatch.route,
            arguments = listOf(navArgument("batchId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getLong("batchId")?.takeIf { it != -1L }
            AddEditBatchScreen(
                batchId = batchId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Tasting Notes
        composable(Screen.TastingNotes.route) {
            TastingNoteListScreen(
                repository = repository,
                onAddTastingNote = {
                    navController.navigate(Screen.AddEditTastingNote.createRoute())
                },
                onEditTastingNote = { noteId ->
                    navController.navigate(Screen.AddEditTastingNote.createRoute(noteId = noteId))
                }
            )
        }

        composable(
            route = Screen.AddEditTastingNote.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument("batchId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId")?.takeIf { it != -1L }
            val batchId = backStackEntry.arguments?.getLong("batchId")?.takeIf { it != -1L }
            AddEditTastingNoteScreen(
                noteId = noteId,
                batchId = batchId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Alarms
        composable(
            route = Screen.AddEditAlarm.route,
            arguments = listOf(
                navArgument("alarmId") {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument("batchId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val alarmId = backStackEntry.arguments?.getLong("alarmId")?.takeIf { it != -1L }
            val batchId = backStackEntry.arguments?.getLong("batchId")?.takeIf { it != -1L }
            AddEditAlarmScreen(
                alarmId = alarmId,
                batchId = batchId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
