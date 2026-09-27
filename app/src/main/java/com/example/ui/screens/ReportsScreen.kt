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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.ui.components.AppFormatters
import com.example.ui.components.MetricCard
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandWarning
import com.example.ui.viewmodel.FreelanceFlowViewModel

@Composable
fun ReportsScreen(
    viewModel: FreelanceFlowViewModel,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()
    val timeEntries by viewModel.timeEntries.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()

    val totalHours = timeEntries.sumOf { it.durationSeconds } / 3600.0
    val billableHours = timeEntries.filter { it.isBillable }.sumOf { it.durationSeconds } / 3600.0
    val nonBillableHours = (totalHours - billableHours).coerceAtLeast(0.0)
    val billableRatio = if (totalHours > 0) (billableHours / totalHours).toFloat() else 1f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reports_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Analytics & Profitability",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Understand where your time and revenue come from",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Financial KPIs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Collected",
                    value = AppFormatters.formatCurrency(metrics.totalPaidRevenue, currency),
                    subtitle = "Paid into account",
                    icon = Icons.Default.AttachMoney,
                    accentColor = BrandAccent,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Outstanding",
                    value = AppFormatters.formatCurrency(metrics.pendingRevenue, currency),
                    subtitle = "Unpaid invoices",
                    icon = Icons.Default.Assessment,
                    accentColor = BrandWarning,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Billable vs Non-Billable Hours Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Time Allocation",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${(billableRatio * 100).toInt()}% Billable",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = BrandAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { billableRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = BrandAccent,
                        trackColor = Color.LightGray.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(BrandAccent))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Billable: %.1f hrs".format(billableHours), style = MaterialTheme.typography.bodySmall)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.Gray))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Non-billable: %.1f hrs".format(nonBillableHours), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        // Hours per Project breakdown
        item {
            Text(
                text = "Performance by Project",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(projects) { project ->
            val projectEntries = timeEntries.filter { it.projectId == project.id }
            val projHours = projectEntries.sumOf { it.durationSeconds } / 3600.0
            val estimatedEarnings = projHours * project.hourlyRate
            val budgetUsageRatio = if (project.budgetAmount > 0) (estimatedEarnings / project.budgetAmount).toFloat().coerceIn(0f, 1f) else 0f

            val projectColor = runCatching {
                Color(android.graphics.Color.parseColor(project.colorHex))
            }.getOrDefault(BrandPrimary)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(projectColor))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(project.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Text("%.1f hrs".format(projHours), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { budgetUsageRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = projectColor,
                        trackColor = Color.LightGray.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Earned: ${AppFormatters.formatCurrency(estimatedEarnings, currency)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandAccent
                        )
                        Text(
                            text = "Budget: ${AppFormatters.formatCurrency(project.budgetAmount, currency)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
