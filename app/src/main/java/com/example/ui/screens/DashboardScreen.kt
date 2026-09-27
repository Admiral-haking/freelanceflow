package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ActiveTimerBanner
import com.example.ui.components.AppFormatters
import com.example.ui.components.MetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandWarning
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FreelanceFlowViewModel

@Composable
fun DashboardScreen(
    viewModel: FreelanceFlowViewModel,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()
    val activeTimer by viewModel.activeTimeEntry.collectAsStateWithLifecycle()
    val liveSeconds by viewModel.liveTimerSeconds.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val recentInvoices by viewModel.invoices.collectAsStateWithLifecycle()
    val recentEntries by viewModel.timeEntries.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()

    val activeProject = projects.find { it.id == activeTimer?.projectId }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Timer Banner (if running)
        if (activeTimer != null) {
            item {
                ActiveTimerBanner(
                    elapsedSeconds = liveSeconds,
                    projectName = activeProject?.name ?: "No project selected",
                    description = activeTimer?.description.orEmpty(),
                    onStopClick = { viewModel.stopTimer() }
                )
            }
        }

        // Quick KPI Metrics Grid (2x2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Paid Revenue",
                    value = AppFormatters.formatCurrency(metrics.totalPaidRevenue, currency),
                    subtitle = "This workspace",
                    icon = Icons.Default.CheckCircle,
                    accentColor = BrandAccent,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Pending / Due",
                    value = AppFormatters.formatCurrency(metrics.pendingRevenue, currency),
                    subtitle = "Awaiting payment",
                    icon = Icons.Default.AttachMoney,
                    accentColor = BrandWarning,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Hours Logged",
                    value = "%.1f hrs".format(metrics.totalHoursLogged),
                    subtitle = "All projects",
                    icon = Icons.Default.Schedule,
                    accentColor = BrandSecondary,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Active Projects",
                    value = "${metrics.activeProjectsCount}",
                    subtitle = "${metrics.clientsCount} Clients",
                    icon = Icons.Default.Folder,
                    accentColor = BrandPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Action Shortcuts Bar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Quick Actions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Start tracking or create invoice",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.TIME_TRACKER) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("quick_start_timer_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Timer")
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.INVOICES) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("quick_new_invoice_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Invoice")
                        }
                    }
                }
            }
        }

        // Recent Invoices Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Invoices",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                TextButton(onClick = { viewModel.navigateTo(AppScreen.INVOICES) }) {
                    Text("View all (${recentInvoices.size})")
                }
            }
        }

        if (recentInvoices.isEmpty()) {
            item {
                Text(
                    text = "No invoices issued yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(recentInvoices.take(3)) { invoice ->
                val client = clients.find { it.id == invoice.clientId }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("invoice_card_${invoice.invoiceNumber}"),
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
                        Column {
                            Text(
                                text = invoice.invoiceNumber,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = client?.name ?: "Client",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Due: " + AppFormatters.formatDate(invoice.dueDateMillis),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = AppFormatters.formatCurrency(invoice.totalAmount, invoice.currency),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            StatusBadge(invoice.status)
                        }
                    }
                }
            }
        }

        // Recent Time Entries Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Time Entries",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                TextButton(onClick = { viewModel.navigateTo(AppScreen.TIME_TRACKER) }) {
                    Text("View all (${recentEntries.size})")
                }
            }
        }

        if (recentEntries.isEmpty()) {
            item {
                Text(
                    text = "No time entries recorded.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(recentEntries.take(3)) { entry ->
                val project = projects.find { it.id == entry.projectId }
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        project?.let {
                                            runCatching { Color(android.graphics.Color.parseColor(it.colorHex)) }.getOrNull()
                                        } ?: BrandPrimary
                                    )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = project?.name ?: "General Task",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = entry.description.ifBlank { "Logged work" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = AppFormatters.formatDurationShort(entry.durationSeconds),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (entry.isBillable) {
                                Text(
                                    text = "Billable",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
