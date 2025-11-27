package com.taktak.app.ui.screens.batches

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taktak.app.data.model.Batch
import com.taktak.app.data.model.BatchStatus
import com.taktak.app.data.repository.TakTakRepository
import com.taktak.app.ui.components.TakTakTextField
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBatchScreen(
    batchId: Long?,
    repository: TakTakRepository,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val batch = if (batchId != null) {
        repository.getBatchById(batchId).collectAsState(initial = null).value
    } else null

    val recipes by repository.getAllRecipes().collectAsState(initial = emptyList())

    var batchName by remember { mutableStateOf(batch?.batchName ?: "") }
    var selectedRecipeId by remember { mutableStateOf(batch?.recipeId ?: (recipes.firstOrNull()?.id ?: 0L)) }
    var notes by remember { mutableStateOf(batch?.notes ?: "") }
    var selectedStatus by remember { mutableStateOf(batch?.status ?: BatchStatus.FERMENTING) }
    var expanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(batch) {
        batch?.let {
            batchName = it.batchName
            selectedRecipeId = it.recipeId
            notes = it.notes
            selectedStatus = it.status
        }
    }

    LaunchedEffect(recipes) {
        if (selectedRecipeId == 0L && recipes.isNotEmpty()) {
            selectedRecipeId = recipes.first().id
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (batchId == null) "Add Batch" else "Edit Batch") },
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
                value = batchName,
                onValueChange = { batchName = it },
                label = "Batch Name"
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = recipes.find { it.id == selectedRecipeId }?.name ?: "Select Recipe",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Recipe") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    recipes.forEach { recipe ->
                        DropdownMenuItem(
                            text = { Text(recipe.name) },
                            onClick = {
                                selectedRecipeId = recipe.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedStatus.name.replace("_", " ").lowercase()
                        .replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    BatchStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.name.replace("_", " ").lowercase()
                                .replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                selectedStatus = status
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            TakTakTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes",
                singleLine = false,
                maxLines = 5
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
                            val startDate = batch?.startDate ?: System.currentTimeMillis()
                            val recipe = recipes.find { it.id == selectedRecipeId }
                            val expectedEndDate = batch?.expectedEndDate ?:
                                (startDate + (recipe?.fermentationTimeDays ?: 7) * 24 * 60 * 60 * 1000L)

                            if (batchId != null && batch != null) {
                                repository.updateBatch(
                                    batch.copy(
                                        batchName = batchName,
                                        recipeId = selectedRecipeId,
                                        notes = notes,
                                        status = selectedStatus,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )
                            } else {
                                repository.insertBatch(
                                    Batch(
                                        batchName = batchName,
                                        recipeId = selectedRecipeId,
                                        startDate = startDate,
                                        expectedEndDate = expectedEndDate,
                                        notes = notes,
                                        status = selectedStatus
                                    )
                                )
                            }
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = batchName.isNotBlank() && selectedRecipeId > 0
                ) {
                    Text("Save")
                }
            }
        }
    }
}
