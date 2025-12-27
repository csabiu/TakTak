package com.taktak.app.ui.screens.tastings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taktak.app.data.model.TastingNote
import com.taktak.app.data.repository.TakTakRepository
import com.taktak.app.ui.components.PhotoPicker
import com.taktak.app.ui.components.TakTakTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTastingNoteScreen(
    noteId: Long?,
    batchId: Long?,
    repository: TakTakRepository,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val tastingNote = if (noteId != null) {
        repository.getTastingNoteById(noteId).collectAsState(initial = null).value
    } else null

    val batches by repository.getAllBatches().collectAsState(initial = emptyList())

    var selectedBatchId by remember { mutableStateOf(batchId ?: tastingNote?.batchId ?: (batches.firstOrNull()?.id ?: 0L)) }
    var appearance by remember { mutableStateOf(tastingNote?.appearance ?: "") }
    var aroma by remember { mutableStateOf(tastingNote?.aroma ?: "") }
    var taste by remember { mutableStateOf(tastingNote?.taste ?: "") }
    var mouthfeel by remember { mutableStateOf(tastingNote?.mouthfeel ?: "") }
    var rating by remember { mutableStateOf(tastingNote?.overallRating ?: 3.0f) }
    var notes by remember { mutableStateOf(tastingNote?.notes ?: "") }
    var photoUris by remember { mutableStateOf(tastingNote?.photoUris ?: emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(tastingNote) {
        tastingNote?.let {
            selectedBatchId = it.batchId
            appearance = it.appearance
            aroma = it.aroma
            taste = it.taste
            mouthfeel = it.mouthfeel
            rating = it.overallRating
            notes = it.notes
            photoUris = it.photoUris
        }
    }

    LaunchedEffect(batches, batchId) {
        if (selectedBatchId == 0L) {
            selectedBatchId = batchId ?: batches.firstOrNull()?.id ?: 0L
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null) "시음 노트 추가" else "시음 노트 수정") },
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
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = batches.find { it.id == selectedBatchId }?.batchName ?: "발효 선택",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("발효중") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
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
                value = appearance,
                onValueChange = { appearance = it },
                label = "외관",
                singleLine = false,
                maxLines = 2
            )

            TakTakTextField(
                value = aroma,
                onValueChange = { aroma = it },
                label = "향",
                singleLine = false,
                maxLines = 2
            )

            TakTakTextField(
                value = taste,
                onValueChange = { taste = it },
                label = "맛",
                singleLine = false,
                maxLines = 3
            )

            TakTakTextField(
                value = mouthfeel,
                onValueChange = { mouthfeel = it },
                label = "질감",
                singleLine = false,
                maxLines = 2
            )

            Column {
                Text(
                    text = "전체 평가: ${String.format("%.1f", rating)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 0f..5f,
                    steps = 9,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0", style = MaterialTheme.typography.labelSmall)
                    Text("5", style = MaterialTheme.typography.labelSmall)
                }
            }

            TakTakTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "추가 메모",
                singleLine = false,
                maxLines = 5
            )

            PhotoPicker(
                photoUris = photoUris,
                onPhotosChanged = { uris ->
                    photoUris = uris
                },
                modifier = Modifier.fillMaxWidth()
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
                            if (noteId != null && tastingNote != null) {
                                repository.updateTastingNote(
                                    tastingNote.copy(
                                        batchId = selectedBatchId,
                                        appearance = appearance,
                                        aroma = aroma,
                                        taste = taste,
                                        mouthfeel = mouthfeel,
                                        overallRating = rating,
                                        notes = notes,
                                        photoUris = photoUris,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )
                            } else {
                                repository.insertTastingNote(
                                    TastingNote(
                                        batchId = selectedBatchId,
                                        tastingDate = System.currentTimeMillis(),
                                        appearance = appearance,
                                        aroma = aroma,
                                        taste = taste,
                                        mouthfeel = mouthfeel,
                                        overallRating = rating,
                                        notes = notes,
                                        photoUris = photoUris
                                    )
                                )
                            }
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedBatchId > 0 && taste.isNotBlank()
                ) {
                    Text("저장")
                }
            }
        }
    }
}
