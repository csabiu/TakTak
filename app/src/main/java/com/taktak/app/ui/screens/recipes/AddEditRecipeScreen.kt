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
import com.taktak.app.ui.components.IntegerCounter
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
    var numberOfStages by remember { mutableStateOf(1) }
    var filteringDays by remember { mutableStateOf(7) }
    var category by remember { mutableStateOf("막걸리") }
    var stages by remember { mutableStateOf(listOf(StageFormData(1))) }

    // Load existing recipe and stages
    LaunchedEffect(recipe, existingStages) {
        recipe?.let {
            name = it.name
            description = it.description
            numberOfStages = it.numberOfStages
            filteringDays = it.filteringDays
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
        if (stages.size != numberOfStages) {
            stages = if (numberOfStages > stages.size) {
                // Add new stages
                stages + (stages.size + 1..numberOfStages).map { StageFormData(it) }
            } else {
                // Remove excess stages
                stages.take(numberOfStages)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recipeId == null) "레시피 추가" else "레시피 수정") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
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
                text = "기본 정보",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            TakTakTextField(
                value = name,
                onValueChange = { name = it },
                label = "레시피 이름"
            )

            TakTakTextField(
                value = description,
                onValueChange = { description = it },
                label = "설명",
                singleLine = false,
                maxLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IntegerCounter(
                    value = numberOfStages,
                    onValueChange = { numberOfStages = it },
                    label = "단계 수",
                    minValue = 1,
                    maxValue = 5,
                    modifier = Modifier.weight(1f)
                )

                IntegerCounter(
                    value = filteringDays,
                    onValueChange = { filteringDays = it },
                    label = "거르는 날",
                    minValue = 1,
                    maxValue = 60,
                    modifier = Modifier.weight(1f)
                )
            }

            TakTakTextField(
                value = category,
                onValueChange = { category = it },
                label = "분류"
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
                    Text("취소")
                }

                Button(
                    onClick = {
                        scope.launch {
                            if (recipeId != null && recipe != null) {
                                // Update existing recipe
                                repository.updateRecipe(
                                    recipe.copy(
                                        name = name,
                                        description = description,
                                        numberOfStages = numberOfStages,
                                        filteringDays = filteringDays,
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
                                        numberOfStages = numberOfStages,
                                        filteringDays = filteringDays,
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
                    Text("저장")
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
            text = "${stageData.stageNumber}단계",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Stage 2+: Days from start (required)
        if (stageData.stageNumber > 1) {
            OutlinedTextField(
                value = stageData.daysFromStart,
                onValueChange = { onUpdate(stageData.copy(daysFromStart = it)) },
                label = { Text("발효 시작 후 며칠") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                placeholder = { Text("예: 5") }
            )
        }

        // All stages: Rice amount
        OutlinedTextField(
            value = stageData.riceAmountKg,
            onValueChange = { onUpdate(stageData.copy(riceAmountKg = it)) },
            label = { Text("쌀 양 (kg)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            placeholder = { Text("예: 3.0") }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = stageData.waterAmountLiters,
                onValueChange = { onUpdate(stageData.copy(waterAmountLiters = it)) },
                label = { Text("물 (L)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                placeholder = { Text("예: 4.5") }
            )

            OutlinedTextField(
                value = stageData.nurukAmountGrams,
                onValueChange = { onUpdate(stageData.copy(nurukAmountGrams = it)) },
                label = { Text("누룩 (g)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                placeholder = { Text("예: 300") }
            )
        }

        TakTakTextField(
            value = stageData.instructions,
            onValueChange = { onUpdate(stageData.copy(instructions = it)) },
            label = "만드는 방법",
            singleLine = false,
            maxLines = 6
        )
    }
}
