package com.taktak.app.ui.screens.alarms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.taktak.app.TakTakApplication
import com.taktak.app.data.model.AlarmItem
import com.taktak.app.data.model.AlarmType
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAlarmScreen(
    alarmId: Long?,
    batchId: Long?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as TakTakApplication).repository
    val alarmScheduler = (context.applicationContext as TakTakApplication).alarmScheduler
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var alarmType by remember { mutableStateOf(AlarmType.CUSTOM) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var isEnabled by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expandedAlarmType by remember { mutableStateOf(false) }

    // Load existing alarm if editing
    LaunchedEffect(alarmId) {
        if (alarmId != null && alarmId > 0) {
            repository.getAlarmById(alarmId)?.let { alarm ->
                title = alarm.title
                description = alarm.description
                alarmType = alarm.alarmType
                isEnabled = alarm.isEnabled

                val instant = alarm.scheduledTime
                val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                selectedDate = localDateTime.toLocalDate()
                selectedTime = localDateTime.toLocalTime()
            }
        } else {
            // Set default title based on alarm type
            title = when (alarmType) {
                AlarmType.NEXT_STAGE -> "Add Next Brewing Stage"
                AlarmType.FILTER -> "Filter Brew"
                AlarmType.CHECK_STATUS -> "Check Fermentation Status"
                AlarmType.COMPLETION -> "Fermentation Complete"
                AlarmType.CUSTOM -> ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (alarmId != null && alarmId > 0) "Edit Alarm" else "Add Alarm") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                try {
                                    val scheduledDateTime = LocalDateTime.of(selectedDate, selectedTime)
                                    val scheduledInstant = scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant()

                                    val alarm = if (alarmId != null && alarmId > 0) {
                                        // Update existing alarm
                                        repository.getAlarmById(alarmId)?.copy(
                                            title = title,
                                            description = description,
                                            alarmType = alarmType,
                                            scheduledTime = scheduledInstant,
                                            isEnabled = isEnabled,
                                            updatedAt = Instant.now()
                                        )
                                    } else {
                                        // Create new alarm
                                        AlarmItem(
                                            batchId = batchId ?: 0,
                                            title = title,
                                            description = description,
                                            alarmType = alarmType,
                                            scheduledTime = scheduledInstant,
                                            isEnabled = isEnabled
                                        )
                                    }

                                    alarm?.let {
                                        val savedId = if (alarmId != null && alarmId > 0) {
                                            repository.updateAlarm(it)
                                            alarmId
                                        } else {
                                            repository.insertAlarm(it)
                                        }

                                        // Schedule the alarm if enabled
                                        if (isEnabled) {
                                            val alarmToSchedule = if (alarmId != null && alarmId > 0) {
                                                it
                                            } else {
                                                it.copy(id = savedId)
                                            }
                                            alarmScheduler.scheduleAlarm(alarmToSchedule)
                                        } else {
                                            alarmScheduler.cancelAlarm(savedId)
                                        }

                                        onNavigateBack()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = !isLoading && title.isNotBlank() && batchId != null
                    ) {
                        Icon(Icons.Default.Check, "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alarm Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedAlarmType,
                onExpandedChange = { expandedAlarmType = it }
            ) {
                OutlinedTextField(
                    value = alarmType.name.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Alarm Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAlarmType) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedAlarmType,
                    onDismissRequest = { expandedAlarmType = false }
                ) {
                    AlarmType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name.replace("_", " ")) },
                            onClick = {
                                alarmType = type
                                // Update title based on type
                                title = when (type) {
                                    AlarmType.NEXT_STAGE -> "Add Next Brewing Stage"
                                    AlarmType.FILTER -> "Filter Brew"
                                    AlarmType.CHECK_STATUS -> "Check Fermentation Status"
                                    AlarmType.COMPLETION -> "Fermentation Complete"
                                    AlarmType.CUSTOM -> title.ifBlank { "" }
                                }
                                expandedAlarmType = false
                            }
                        )
                    }
                }
            }

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Alarm Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Date Picker Button
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Date: ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}")
            }

            // Time Picker Button
            OutlinedButton(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Time: ${selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a"))}")
            }

            // Enabled Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Alarm Enabled", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { isEnabled = it }
                )
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Time Picker Dialog
        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(
                initialHour = selectedTime.hour,
                initialMinute = selectedTime.minute
            )
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel")
                    }
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }
    }
}
