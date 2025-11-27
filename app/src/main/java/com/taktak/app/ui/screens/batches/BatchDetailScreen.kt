package com.taktak.app.ui.screens.batches

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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchDetailScreen(
    batchId: Long,
    repository: TakTakRepository,
    onNavigateBack: () -> Unit,
    onEditBatch: (Long) -> Unit,
    onAddTastingNote: (Long) -> Unit,
    onAddAlarm: (Long) -> Unit = {},
    onEditAlarm: (Long) -> Unit = {}
) {
    val batch by repository.getBatchById(batchId).collectAsState(initial = null)
    val recipe = batch?.let {
        repository.getRecipeById(it.recipeId).collectAsState(initial = null).value
    }
    val alarms by repository.getAlarmsByBatch(batchId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    batch?.let { currentBatch ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentBatch.batchName) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEditBatch(batchId) }) {
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
                    onClick = { onAddTastingNote(batchId) },
                    icon = { Icon(Icons.Default.WineBar, contentDescription = null) },
                    text = { Text("Add Tasting") }
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Status",
                                style = MaterialTheme.typography.labelSmall
                            )
                            BatchStatusChip(status = currentBatch.status)
                        }
                    }
                }

                recipe?.let {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Recipe",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Timeline",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Start Date",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = dateFormat.format(Date(currentBatch.startDate)),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Column {
                                Text(
                                    text = "Expected End",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = dateFormat.format(Date(currentBatch.expectedEndDate)),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Alarms Section
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Alarms",
                                style = MaterialTheme.typography.titleSmall
                            )
                            IconButton(onClick = { onAddAlarm(batchId) }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Alarm")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (alarms.isEmpty()) {
                            Text(
                                text = "No alarms set. Add an alarm to be reminded about important brewing steps.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            alarms.forEach { alarm ->
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = alarm.title,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        if (alarm.description.isNotBlank()) {
                                            Text(
                                                text = alarm.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = dateFormat.format(Date.from(alarm.scheduledTime)),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        if (alarm.isTriggered) {
                                            Text(
                                                text = "Triggered",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        } else if (!alarm.isEnabled) {
                                            Text(
                                                text = "Disabled",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                    IconButton(onClick = { onEditAlarm(alarm.id) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Alarm")
                                    }
                                }
                            }
                        }
                    }
                }

                if (currentBatch.notes.isNotBlank()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentBatch.notes,
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
                title = { Text("Delete Batch") },
                text = { Text("Are you sure you want to delete this batch? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                repository.deleteBatch(currentBatch)
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
