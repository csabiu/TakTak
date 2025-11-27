package com.taktak.app.ui.screens.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taktak.app.data.model.JournalEntry
import com.taktak.app.data.repository.TakTakRepository
import com.taktak.app.ui.components.TakTakTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJournalScreen(
    entryId: Long?,
    repository: TakTakRepository,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val journalEntry = if (entryId != null) {
        repository.getJournalEntryById(entryId).collectAsState(initial = null).value
    } else null

    val batches by repository.getAllBatches().collectAsState(initial = emptyList())

    var title by remember { mutableStateOf(journalEntry?.title ?: "") }
    var content by remember { mutableStateOf(journalEntry?.content ?: "") }
    var tags by remember { mutableStateOf(journalEntry?.tags ?: "") }
    var selectedBatchId by remember { mutableStateOf<Long?>(journalEntry?.batchId) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(journalEntry) {
        journalEntry?.let {
            title = it.title
            content = it.content
            tags = it.tags
            selectedBatchId = it.batchId
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entryId == null) "Add Journal Entry" else "Edit Journal Entry") },
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
                value = title,
                onValueChange = { title = it },
                label = "Title"
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = if (selectedBatchId != null) {
                        batches.find { it.id == selectedBatchId }?.batchName ?: "None"
                    } else {
                        "None"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Related Batch (Optional)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None") },
                        onClick = {
                            selectedBatchId = null
                            expanded = false
                        }
                    )
                    batches.forEach { batch ->
                        DropdownMenuItem(
                            text = { Text(batch.batchName) },
                            onClick = {
                                selectedBatchId = batch.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            TakTakTextField(
                value = content,
                onValueChange = { content = it },
                label = "Content",
                singleLine = false,
                maxLines = 10
            )

            TakTakTextField(
                value = tags,
                onValueChange = { tags = it },
                label = "Tags (comma-separated)"
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
                            if (entryId != null && journalEntry != null) {
                                repository.updateJournalEntry(
                                    journalEntry.copy(
                                        title = title,
                                        content = content,
                                        tags = tags,
                                        batchId = selectedBatchId,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )
                            } else {
                                repository.insertJournalEntry(
                                    JournalEntry(
                                        title = title,
                                        content = content,
                                        tags = tags,
                                        batchId = selectedBatchId,
                                        entryDate = System.currentTimeMillis()
                                    )
                                )
                            }
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotBlank() && content.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        }
    }
}
