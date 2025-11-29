package com.taktak.app.ui.screens.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taktak.app.data.repository.TakTakRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    repository: TakTakRepository,
    onNavigateBack: () -> Unit,
    onEditRecipe: (Long) -> Unit,
    onStartBatch: (Long) -> Unit
) {
    val recipe by repository.getRecipeById(recipeId).collectAsState(initial = null)
    val stages by repository.getStagesForRecipe(recipeId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    recipe?.let { currentRecipe ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentRecipe.name) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEditRecipe(recipeId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { onStartBatch(recipeId) },
                    icon = { Icon(Icons.Default.Science, contentDescription = null) },
                    text = { Text("Start Batch") }
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
                // Description Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentRecipe.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Recipe Info Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Stages",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "${currentRecipe.numberOfStages}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column {
                            Text(
                                text = "Filtering Day",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "Day ${currentRecipe.filteringDays}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column {
                            Text(
                                text = "Category",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = currentRecipe.category,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                // Stages Cards
                stages.forEach { stage ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Stage ${stage.stageNumber}${if (stage.daysFromStart != null) " - Day ${stage.daysFromStart}" else ""}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Ingredients for this stage
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (stage.riceAmountKg != null) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Rice",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "${stage.riceAmountKg} kg",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Water",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "${stage.waterAmountLiters} L",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Nuruk",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "${stage.nurukAmountGrams} g",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Instructions",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stage.instructions,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Recipe") },
                text = { Text("Are you sure you want to delete this recipe? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                repository.deleteRecipe(currentRecipe)
                                onNavigateBack()
                            }
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
