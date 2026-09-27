package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppFormatters
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.viewmodel.FreelanceFlowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeTrackerScreen(
    viewModel: FreelanceFlowViewModel,
    modifier: Modifier = Modifier
) {
    val activeTimer by viewModel.activeTimeEntry.collectAsStateWithLifecycle()
    val liveSeconds by viewModel.liveTimerSeconds.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val timeEntries by viewModel.timeEntries.collectAsStateWithLifecycle()

    var selectedProjectId by remember { mutableStateOf<String?>(null) }
    var taskDescription by remember { mutableStateOf("") }
    var isBillable by remember { mutableStateOf(true) }
    var isProjectDropdownExpanded by remember { mutableStateOf(false) }

    // Dialog for manual entry
    var showManualDialog by remember { mutableStateOf(false) }
    var manualHours by remember { mutableStateOf("1.5") }
    var manualDescription by remember { mutableStateOf("") }
    var manualProjectId by remember { mutableStateOf<String?>(null) }
    var manualIsBillable by remember { mutableStateOf(true) }

    val activeProject = projects.find { it.id == activeTimer?.projectId }
    val isTimerRunning = activeTimer != null

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("time_tracker_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stopwatch Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stopwatch_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isTimerRunning) "TIMER RUNNING" else "READY TO TRACK",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isTimerRunning) BrandDanger else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Digital Clock Display
                    Text(
                        text = AppFormatters.formatSecondsToTime(if (isTimerRunning) liveSeconds else 0L),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = if (isTimerRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isTimerRunning) {
                        // Project Selector Dropdown
                        ExposedDropdownMenuBox(
                            expanded = isProjectDropdownExpanded,
                            onExpandedChange = { isProjectDropdownExpanded = !isProjectDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val selectedProj = projects.find { it.id == selectedProjectId }
                            OutlinedTextField(
                                value = selectedProj?.name ?: "Select Project (Optional)",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isProjectDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = isProjectDropdownExpanded,
                                onDismissRequest = { isProjectDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("None (General Work)") },
                                    onClick = {
                                        selectedProjectId = null
                                        isProjectDropdownExpanded = false
                                    }
                                )
                                projects.forEach { proj ->
                                    DropdownMenuItem(
                                        text = { Text(proj.name) },
                                        onClick = {
                                            selectedProjectId = proj.id
                                            isProjectDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Description TextField
                        OutlinedTextField(
                            value = taskDescription,
                            onValueChange = { taskDescription = it },
                            label = { Text("What are you working on?") },
                            placeholder = { Text("e.g. Design feedback, API refactoring...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("task_description_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Billable Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Billable to client",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Switch(
                                checked = isBillable,
                                onCheckedChange = { isBillable = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = BrandAccent)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Start Timer Button
                        Button(
                            onClick = {
                                viewModel.startTimer(selectedProjectId, taskDescription, isBillable)
                                taskDescription = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("start_timer_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Timer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    } else {
                        // Timer is running state
                        Text(
                            text = "Project: ${activeProject?.name ?: "General Task"}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!activeTimer?.description.isNullOrBlank()) {
                            Text(
                                text = "\"${activeTimer?.description}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.stopTimer() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("stop_running_timer_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandDanger)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stop & Save Entry", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        // Manual Entry Button Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Timesheet History",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedButton(
                    onClick = { showManualDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("manual_entry_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Manual Entry")
                }
            }
        }

        // Timesheet list
        if (timeEntries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No recorded sessions yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(timeEntries) { entry ->
                val project = projects.find { it.id == entry.projectId }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("time_entry_item_${entry.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(
                                        project?.let {
                                            runCatching { Color(android.graphics.Color.parseColor(it.colorHex)) }.getOrNull()
                                        } ?: BrandPrimary
                                    )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = project?.name ?: "General Task",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = entry.description.ifBlank { "Unspecified activity" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = AppFormatters.formatDate(entry.startTimeMillis),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = AppFormatters.formatDurationShort(entry.durationSeconds),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (entry.isBillable) {
                                    Text(
                                        text = "Billable",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrandAccent
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deleteTimeEntry(entry) },
                                modifier = Modifier.testTag("delete_entry_${entry.id}")
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Manual Time Entry Dialog
    if (showManualDialog) {
        var manualDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text("Log Manual Time") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = manualDropdownExpanded,
                        onExpandedChange = { manualDropdownExpanded = !manualDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val selectedProj = projects.find { it.id == manualProjectId }
                        OutlinedTextField(
                            value = selectedProj?.name ?: "Select Project",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = manualDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = manualDropdownExpanded,
                            onDismissRequest = { manualDropdownExpanded = false }
                        ) {
                            projects.forEach { proj ->
                                DropdownMenuItem(
                                    text = { Text(proj.name) },
                                    onClick = {
                                        manualProjectId = proj.id
                                        manualDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = manualHours,
                        onValueChange = { manualHours = it },
                        label = { Text("Hours worked (e.g. 2.5)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = manualDescription,
                        onValueChange = { manualDescription = it },
                        label = { Text("Task description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Billable entry")
                        Switch(
                            checked = manualIsBillable,
                            onCheckedChange = { manualIsBillable = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hours = manualHours.toDoubleOrNull() ?: 1.0
                        viewModel.addManualTime(manualProjectId, manualDescription, hours, manualIsBillable)
                        showManualDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Save Time")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
