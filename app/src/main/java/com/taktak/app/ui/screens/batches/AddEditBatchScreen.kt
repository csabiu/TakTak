package com.taktak.app.ui.screens.batches

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.taktak.app.TakTakApplication
import com.taktak.app.data.model.AlarmItem
import com.taktak.app.data.model.AlarmType
import com.taktak.app.data.model.Batch
import com.taktak.app.data.model.BatchStatus
import com.taktak.app.data.repository.TakTakRepository
import com.taktak.app.ui.components.TakTakTextField
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBatchScreen(
    batchId: Long?,
    repository: TakTakRepository,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val alarmScheduler = (context.applicationContext as TakTakApplication).alarmScheduler

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
                title = { Text(if (batchId == null) "발효중 추가" else "발효중 수정") },
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
            TakTakTextField(
                value = batchName,
                onValueChange = { batchName = it },
                label = "발효 이름"
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = recipes.find { it.id == selectedRecipeId }?.name ?: "레시피 선택",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("레시피") },
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
                    label = { Text("상태") },
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
                label = "메모",
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
                    Text("취소")
                }

                Button(
                    onClick = {
                        scope.launch {
                            val startDate = batch?.startDate ?: System.currentTimeMillis()
                            val recipe = recipes.find { it.id == selectedRecipeId }
                            val expectedEndDate = batch?.expectedEndDate ?:
                                (startDate + (recipe?.filteringDays ?: 7) * 24 * 60 * 60 * 1000L)

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
                                // Create new batch
                                val newBatchId = repository.insertBatch(
                                    Batch(
                                        batchName = batchName,
                                        recipeId = selectedRecipeId,
                                        startDate = startDate,
                                        expectedEndDate = expectedEndDate,
                                        notes = notes,
                                        status = selectedStatus
                                    )
                                )

                                // Auto-create alarms for stages and filtering
                                if (recipe != null) {
                                    val stages = repository.getStagesForRecipeSync(recipe.id)
                                    val alarmsToCreate = mutableListOf<AlarmItem>()

                                    // Create alarm for each stage 2+ (stage 1 starts immediately)
                                    stages.filter { it.stageNumber > 1 }.forEach { stage ->
                                        stage.daysFromStart?.let { days ->
                                            val scheduledTime = startDate + (days * 24 * 60 * 60 * 1000L)
                                            alarmsToCreate.add(
                                                AlarmItem(
                                                    batchId = newBatchId,
                                                    alarmType = AlarmType.NEXT_STAGE,
                                                    title = "단계 ${stage.stageNumber} - ${batchName}",
                                                    description = "단계 ${stage.stageNumber} 재료 추가: 물 ${stage.waterAmountLiters}L, 누룩 ${stage.nurukAmountGrams}g",
                                                    scheduledTime = Instant.ofEpochMilli(scheduledTime),
                                                    isEnabled = true
                                                )
                                            )
                                        }
                                    }

                                    // Create alarm for filtering day
                                    val filteringTime = startDate + (recipe.filteringDays * 24 * 60 * 60 * 1000L)
                                    alarmsToCreate.add(
                                        AlarmItem(
                                            batchId = newBatchId,
                                            alarmType = AlarmType.FILTER,
                                            title = "거르기 - ${batchName}",
                                            description = "막걸리를 거를 시간입니다",
                                            scheduledTime = Instant.ofEpochMilli(filteringTime),
                                            isEnabled = true
                                        )
                                    )

                                    // Insert all alarms and schedule them
                                    alarmsToCreate.forEach { alarm ->
                                        val alarmId = repository.insertAlarm(alarm)
                                        alarmScheduler.scheduleAlarm(alarm.copy(id = alarmId))
                                    }
                                }
                            }
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = batchName.isNotBlank() && selectedRecipeId > 0
                ) {
                    Text("저장")
                }
            }
        }
    }
}
