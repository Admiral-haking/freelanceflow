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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.InvoiceEntity
import com.example.data.local.entity.InvoiceItemEntity
import com.example.ui.components.AppFormatters
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.viewmodel.FreelanceFlowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    viewModel: FreelanceFlowViewModel,
    modifier: Modifier = Modifier
) {
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("ALL") }
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }
    var paymentDialogInvoice by remember { mutableStateOf<InvoiceEntity?>(null) }
    var previewInvoice by remember { mutableStateOf<InvoiceEntity?>(null) }

    val filteredInvoices = when (selectedFilter) {
        "PAID" -> invoices.filter { it.status == "PAID" }
        "PENDING" -> invoices.filter { it.status in listOf("SENT", "DRAFT", "OVERDUE") }
        else -> invoices
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateInvoiceDialog = true },
                containerColor = BrandPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_create_invoice")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Invoice")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("invoices_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Invoices & Payments",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Track billing, receivables, and gateway checkouts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Filter Chips
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("All (${invoices.size})") }
                    )
                    FilterChip(
                        selected = selectedFilter == "PENDING",
                        onClick = { selectedFilter = "PENDING" },
                        label = { Text("Due / Pending (${invoices.count { it.status != "PAID" }})") }
                    )
                    FilterChip(
                        selected = selectedFilter == "PAID",
                        onClick = { selectedFilter = "PAID" },
                        label = { Text("Paid (${invoices.count { it.status == "PAID" }})") }
                    )
                }
            }

            if (filteredInvoices.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No invoices found in this view",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredInvoices) { invoice ->
                    val client = clients.find { it.id == invoice.clientId }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("invoice_item_${invoice.invoiceNumber}"),
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
                                Column {
                                    Text(
                                        text = invoice.invoiceNumber,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = client?.name ?: "Client",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                StatusBadge(invoice.status)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Issued: ${AppFormatters.formatDate(invoice.issueDateMillis)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Due: ${AppFormatters.formatDate(invoice.dueDateMillis)}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = if (invoice.status != "PAID") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = AppFormatters.formatCurrency(invoice.totalAmount, invoice.currency),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { previewInvoice = invoice },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Preview Receipt")
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (invoice.status != "PAID") {
                                        Button(
                                            onClick = { paymentDialogInvoice = invoice },
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("pay_invoice_${invoice.invoiceNumber}")
                                        ) {
                                            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Register Payment")
                                        }
                                    }

                                    IconButton(onClick = { viewModel.deleteInvoice(invoice) }) {
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
        }
    }

    // Register Payment Gateway Dialog
    paymentDialogInvoice?.let { invoice ->
        var selectedGateway by remember { mutableStateOf("STRIPE") }
        var transactionRef by remember { mutableStateOf("TXN-${System.currentTimeMillis().toString().takeLast(6)}") }

        AlertDialog(
            onDismissRequest = { paymentDialogInvoice = null },
            title = { Text("Record Payment for ${invoice.invoiceNumber}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Amount: ${AppFormatters.formatCurrency(invoice.totalAmount, invoice.currency)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Text("Select Payment Gateway / Method:")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedGateway == "STRIPE",
                            onClick = { selectedGateway = "STRIPE" },
                            label = { Text("Stripe") }
                        )
                        FilterChip(
                            selected = selectedGateway == "ZARINPAL",
                            onClick = { selectedGateway = "ZARINPAL" },
                            label = { Text("زرین‌پال") }
                        )
                        FilterChip(
                            selected = selectedGateway == "MANUAL_CASH",
                            onClick = { selectedGateway = "MANUAL_CASH" },
                            label = { Text("حواله / نقدی") }
                        )
                    }

                    OutlinedTextField(
                        value = transactionRef,
                        onValueChange = { transactionRef = it },
                        label = { Text("Transaction Reference ID") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markInvoicePaid(invoice.id, selectedGateway)
                        paymentDialogInvoice = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirm Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { paymentDialogInvoice = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Invoice Receipt Preview Dialog
    previewInvoice?.let { invoice ->
        val client = clients.find { it.id == invoice.clientId }

        AlertDialog(
            onDismissRequest = { previewInvoice = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(invoice.invoiceNumber, fontWeight = FontWeight.Bold)
                    StatusBadge(invoice.status)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Billed To: ${client?.name ?: "Customer"}", fontWeight = FontWeight.SemiBold)
                    if (!client?.company.isNullOrBlank()) Text("Company: ${client?.company}")
                    if (!client?.email.isNullOrBlank()) Text("Email: ${client?.email}")

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal:")
                        Text(AppFormatters.formatCurrency(invoice.subtotal, invoice.currency))
                    }
                    if (invoice.taxRate > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tax (${invoice.taxRate}%):")
                            Text(AppFormatters.formatCurrency(invoice.taxAmount, invoice.currency))
                        }
                    }
                    if (invoice.discountAmount > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Discount:")
                            Text("-${AppFormatters.formatCurrency(invoice.discountAmount, invoice.currency)}")
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount:", fontWeight = FontWeight.Bold)
                        Text(AppFormatters.formatCurrency(invoice.totalAmount, invoice.currency), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Notes: ${invoice.notes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(onClick = { previewInvoice = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Create New Invoice Dialog
    if (showCreateInvoiceDialog) {
        var selectedClientId by remember { mutableStateOf(clients.firstOrNull()?.id ?: "") }
        var invoiceNo by remember { mutableStateOf("INV-2026-${(100..999).random()}") }
        var lineDescription by remember { mutableStateOf("Development & UI Implementation") }
        var lineHours by remember { mutableStateOf("20") }
        var lineRate by remember { mutableStateOf("75") }
        var taxRateText by remember { mutableStateOf("5") }
        var notes by remember { mutableStateOf("Payment due within 14 days.") }
        var clientDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showCreateInvoiceDialog = false },
            title = { Text("Generate New Invoice") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Client Picker
                    ExposedDropdownMenuBox(
                        expanded = clientDropdownExpanded,
                        onExpandedChange = { clientDropdownExpanded = !clientDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val clientObj = clients.find { it.id == selectedClientId }
                        OutlinedTextField(
                            value = clientObj?.name ?: "Select Client *",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = clientDropdownExpanded,
                            onDismissRequest = { clientDropdownExpanded = false }
                        ) {
                            clients.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text("${c.name} (${c.company})") },
                                    onClick = {
                                        selectedClientId = c.id
                                        clientDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = invoiceNo,
                        onValueChange = { invoiceNo = it },
                        label = { Text("Invoice Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = lineDescription,
                        onValueChange = { lineDescription = it },
                        label = { Text("Primary Deliverable / Service") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = lineHours,
                            onValueChange = { lineHours = it },
                            label = { Text("Hours / Qty") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = lineRate,
                            onValueChange = { lineRate = it },
                            label = { Text("Unit Price ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = taxRateText,
                        onValueChange = { taxRateText = it },
                        label = { Text("Tax Rate (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Terms & Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedClientId.isNotBlank()) {
                            val qty = lineHours.toDoubleOrNull() ?: 1.0
                            val rate = lineRate.toDoubleOrNull() ?: 50.0
                            val tax = taxRateText.toDoubleOrNull() ?: 0.0

                            val item = InvoiceItemEntity(
                                invoiceId = "",
                                description = lineDescription,
                                quantity = qty,
                                unitPrice = rate,
                                totalPrice = qty * rate
                            )

                            viewModel.createInvoice(
                                clientId = selectedClientId,
                                invoiceNumber = invoiceNo,
                                items = listOf(item),
                                taxRate = tax,
                                discountAmount = 0.0,
                                notes = notes
                            )
                            showCreateInvoiceDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Issue Invoice")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateInvoiceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
