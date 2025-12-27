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
                            Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEditBatch(batchId) }) {
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
                    onClick = { onAddTastingNote(batchId) },
                    icon = { Icon(Icons.Default.WineBar, contentDescription = null) },
                    text = { Text("시음 추가") }
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
                                text = "상태",
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
                                text = "레시피",
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
                            text = "타임라인",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "시작일",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = dateFormat.format(Date(currentBatch.startDate)),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Column {
                                Text(
                                    text = "예상 종료일",
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
                                text = "알람",
                                style = MaterialTheme.typography.titleSmall
                            )
                            IconButton(onClick = { onAddAlarm(batchId) }) {
                                Icon(Icons.Default.Add, contentDescription = "알람 추가")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (alarms.isEmpty()) {
                            Text(
                                text = "설정된 알람이 없습니다. 중요한 양조 단계를 알림 받으려면 알람을 추가하세요.",
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
                                                text = "발동됨",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        } else if (!alarm.isEnabled) {
                                            Text(
                                                text = "비활성화",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                    IconButton(onClick = { onEditAlarm(alarm.id) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "알람 수정")
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
                                text = "메모",
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
                title = { Text("발효중 삭제") },
                text = { Text("이 발효를 삭제하시겠습니까? 이 작업은 취소할 수 없습니다.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                repository.deleteBatch(currentBatch)
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
