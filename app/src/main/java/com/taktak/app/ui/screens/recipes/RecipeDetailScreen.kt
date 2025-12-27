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
                            Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEditRecipe(recipeId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "수정")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "삭제")
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
                    text = { Text("발효 시작") }
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
                            text = "설명",
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
                                text = "단계",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "${currentRecipe.numberOfStages}단",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column {
                            Text(
                                text = "거르는 날",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "${currentRecipe.filteringDays}일",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column {
                            Text(
                                text = "분류",
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
                                text = "${stage.stageNumber}단계${if (stage.daysFromStart != null) " - ${stage.daysFromStart}일" else ""}",
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
                                            text = "쌀",
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
                                        text = "물",
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
                                        text = "누룩",
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
                                text = "만드는 방법",
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
                title = { Text("레시피 삭제") },
                text = { Text("이 레시피를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                repository.deleteRecipe(currentRecipe)
                                onNavigateBack()
                            }
                        }
                    ) {
                        Text("삭제")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}
