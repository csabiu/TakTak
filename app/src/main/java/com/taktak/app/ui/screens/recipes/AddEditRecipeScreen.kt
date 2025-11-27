package com.taktak.app.ui.screens.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.taktak.app.data.model.Recipe
import com.taktak.app.data.repository.TakTakRepository
import com.taktak.app.ui.components.TakTakTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    recipeId: Long?,
    repository: TakTakRepository,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val recipe = if (recipeId != null) {
        repository.getRecipeById(recipeId).collectAsState(initial = null).value
    } else null

    var name by remember { mutableStateOf(recipe?.name ?: "") }
    var description by remember { mutableStateOf(recipe?.description ?: "") }
    var ingredients by remember { mutableStateOf(recipe?.ingredients ?: "") }
    var instructions by remember { mutableStateOf(recipe?.instructions ?: "") }
    var fermentationDays by remember { mutableStateOf(recipe?.fermentationTimeDays?.toString() ?: "7") }
    var category by remember { mutableStateOf(recipe?.category ?: "Makgeolli") }

    LaunchedEffect(recipe) {
        recipe?.let {
            name = it.name
            description = it.description
            ingredients = it.ingredients
            instructions = it.instructions
            fermentationDays = it.fermentationTimeDays.toString()
            category = it.category
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recipeId == null) "Add Recipe" else "Edit Recipe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TakTakTextField(
                value = name,
                onValueChange = { name = it },
                label = "Recipe Name"
            )

            TakTakTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                singleLine = false,
                maxLines = 3
            )

            TakTakTextField(
                value = category,
                onValueChange = { category = it },
                label = "Category"
            )

            TakTakTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = "Ingredients",
                singleLine = false,
                maxLines = 5
            )

            TakTakTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = "Instructions",
                singleLine = false,
                maxLines = 8
            )

            OutlinedTextField(
                value = fermentationDays,
                onValueChange = { fermentationDays = it },
                label = { Text("Fermentation Time (days)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        scope.launch {
                            val days = fermentationDays.toIntOrNull() ?: 7
                            if (recipeId != null && recipe != null) {
                                repository.updateRecipe(
                                    recipe.copy(
                                        name = name,
                                        description = description,
                                        ingredients = ingredients,
                                        instructions = instructions,
                                        fermentationTimeDays = days,
                                        category = category,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )
                            } else {
                                repository.insertRecipe(
                                    Recipe(
                                        name = name,
                                        description = description,
                                        ingredients = ingredients,
                                        instructions = instructions,
                                        fermentationTimeDays = days,
                                        category = category
                                    )
                                )
                            }
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank() && description.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        }
    }
}
