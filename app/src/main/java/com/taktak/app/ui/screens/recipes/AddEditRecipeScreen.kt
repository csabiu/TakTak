package com.taktak.app.ui.screens.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.taktak.app.data.model.Recipe
import com.taktak.app.data.model.RecipeStage
import com.taktak.app.data.repository.TakTakRepository
import com.taktak.app.ui.components.TakTakTextField
import kotlinx.coroutines.launch

data class StageFormData(
    val stageNumber: Int,
    var riceAmountKg: String = "",
    var waterAmountLiters: String = "",
    var nurukAmountGrams: String = "",
    var daysFromStart: String = "",
    var instructions: String = ""
)

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
    val existingStages = if (recipeId != null) {
        repository.getStagesForRecipe(recipeId).collectAsState(initial = emptyList()).value
    } else emptyList()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var numberOfStages by remember { mutableStateOf("1") }
    var filteringDays by remember { mutableStateOf("7") }
    var category by remember { mutableStateOf("Makgeolli") }
    var stages by remember { mutableStateOf(listOf(StageFormData(1))) }

    // Load existing recipe and stages
    LaunchedEffect(recipe, existingStages) {
        recipe?.let {
            name = it.name
            description = it.description
            numberOfStages = it.numberOfStages.toString()
            filteringDays = it.filteringDays.toString()
            category = it.category

            if (existingStages.isNotEmpty()) {
                stages = existingStages.map { stage ->
                    StageFormData(
                        stageNumber = stage.stageNumber,
                        riceAmountKg = stage.riceAmountKg?.toString() ?: "",
                        waterAmountLiters = stage.waterAmountLiters.toString(),
                        nurukAmountGrams = stage.nurukAmountGrams.toString(),
                        daysFromStart = stage.daysFromStart?.toString() ?: "",
                        instructions = stage.instructions
                    )
                }
            }
        }
    }

    // Update stages list when number of stages changes
    LaunchedEffect(numberOfStages) {
        val numStages = numberOfStages.toIntOrNull() ?: 1
        if (stages.size != numStages) {
            stages = if (numStages > stages.size) {
                // Add new stages
                stages + (stages.size + 1..numStages).map { StageFormData(it) }
            } else {
                // Remove excess stages
                stages.take(numStages)
            }
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
            Text(
                text = "Basic Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = numberOfStages,
                    onValueChange = { numberOfStages = it },
                    label = { Text("Number of Stages") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = filteringDays,
                    onValueChange = { filteringDays = it },
                    label = { Text("Filtering Day") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            TakTakTextField(
                value = category,
                onValueChange = { category = it },
                label = "Category"
            )

            Divider()

            // Stage forms
            stages.forEachIndexed { index, stage ->
                StageForm(
                    stageData = stage,
                    onUpdate = { updated ->
                        stages = stages.toMutableList().apply { set(index, updated) }
                    }
                )
                if (index < stages.size - 1) {
                    Divider()
                }
            }

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
                            val numStages = numberOfStages.toIntOrNull() ?: 1
                            val filterDays = filteringDays.toIntOrNull() ?: 7

                            if (recipeId != null && recipe != null) {
                                // Update existing recipe
                                repository.updateRecipe(
                                    recipe.copy(
                                        name = name,
                                        description = description,
                                        numberOfStages = numStages,
                                        filteringDays = filterDays,
                                        category = category,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )

                                // Delete old stages and insert new ones
                                repository.deleteStagesForRecipe(recipeId)
                                val stagesToInsert = stages.map { stageData ->
                                    RecipeStage(
                                        recipeId = recipeId,
                                        stageNumber = stageData.stageNumber,
                                        riceAmountKg = stageData.riceAmountKg.toDoubleOrNull(),
                                        waterAmountLiters = stageData.waterAmountLiters.toDoubleOrNull() ?: 0.0,
                                        nurukAmountGrams = stageData.nurukAmountGrams.toDoubleOrNull() ?: 0.0,
                                        daysFromStart = if (stageData.stageNumber > 1)
                                            stageData.daysFromStart.toIntOrNull() else null,
                                        instructions = stageData.instructions
                                    )
                                }
                                repository.insertStages(stagesToInsert)
                            } else {
                                // Insert new recipe
                                val newRecipeId = repository.insertRecipe(
                                    Recipe(
                                        name = name,
                                        description = description,
                                        numberOfStages = numStages,
                                        filteringDays = filterDays,
                                        category = category
                                    )
                                )

                                // Insert stages
                                val stagesToInsert = stages.map { stageData ->
                                    RecipeStage(
                                        recipeId = newRecipeId,
                                        stageNumber = stageData.stageNumber,
                                        riceAmountKg = stageData.riceAmountKg.toDoubleOrNull(),
                                        waterAmountLiters = stageData.waterAmountLiters.toDoubleOrNull() ?: 0.0,
                                        nurukAmountGrams = stageData.nurukAmountGrams.toDoubleOrNull() ?: 0.0,
                                        daysFromStart = if (stageData.stageNumber > 1)
                                            stageData.daysFromStart.toIntOrNull() else null,
                                        instructions = stageData.instructions
                                    )
                                }
                                repository.insertStages(stagesToInsert)
                            }
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank() && description.isNotBlank() &&
                             stages.all { it.waterAmountLiters.isNotBlank() && it.nurukAmountGrams.isNotBlank() }
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun StageForm(
    stageData: StageFormData,
    onUpdate: (StageFormData) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Stage ${stageData.stageNumber}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Stage 2+: Days from start (required)
        if (stageData.stageNumber > 1) {
            OutlinedTextField(
                value = stageData.daysFromStart,
                onValueChange = { onUpdate(stageData.copy(daysFromStart = it)) },
                label = { Text("Days from Batch Start") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                placeholder = { Text("e.g., 5") }
            )
        }

        // All stages: Rice amount
        OutlinedTextField(
            value = stageData.riceAmountKg,
            onValueChange = { onUpdate(stageData.copy(riceAmountKg = it)) },
            label = { Text("Rice Amount (kg)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            placeholder = { Text("e.g., 3.0") }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = stageData.waterAmountLiters,
                onValueChange = { onUpdate(stageData.copy(waterAmountLiters = it)) },
                label = { Text("Water (L)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                placeholder = { Text("e.g., 4.5") }
            )

            OutlinedTextField(
                value = stageData.nurukAmountGrams,
                onValueChange = { onUpdate(stageData.copy(nurukAmountGrams = it)) },
                label = { Text("Nuruk (g)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                placeholder = { Text("e.g., 300") }
            )
        }

        TakTakTextField(
            value = stageData.instructions,
            onValueChange = { onUpdate(stageData.copy(instructions = it)) },
            label = "Instructions",
            singleLine = false,
            maxLines = 6
        )
    }
}
