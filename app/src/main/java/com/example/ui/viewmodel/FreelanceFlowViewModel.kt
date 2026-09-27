package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.ClientEntity
import com.example.data.local.entity.InvoiceEntity
import com.example.data.local.entity.InvoiceItemEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.TimeEntryEntity
import com.example.data.repository.FreelanceFlowRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    DASHBOARD,
    TIME_TRACKER,
    PROJECTS,
    INVOICES,
    CLIENTS,
    ANALYTICS,
    SETTINGS
}

data class DashboardMetrics(
    val totalPaidRevenue: Double = 0.0,
    val pendingRevenue: Double = 0.0,
    val totalHoursLogged: Double = 0.0,
    val activeProjectsCount: Int = 0,
    val clientsCount: Int = 0
)

class FreelanceFlowViewModel(
    private val repository: FreelanceFlowRepository
) : ViewModel() {

    // Current Screen
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Workspace & Currency Settings
    private val _workspaceName = MutableStateFlow("FreelanceFlow Workspace")
    val workspaceName: StateFlow<String> = _workspaceName.asStateFlow()

    private val _currency = MutableStateFlow("USD")
    val currency: StateFlow<String> = _currency.asStateFlow()

    // Repositories StateFlows
    val clients = repository.allClients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val projects = repository.allProjects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tasks = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val timeEntries = repository.allTimeEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeTimeEntry = repository.activeTimeEntry.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val invoices = repository.allInvoices.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active Live Timer Elapsed Seconds
    private val _liveTimerSeconds = MutableStateFlow(0L)
    val liveTimerSeconds: StateFlow<Long> = _liveTimerSeconds.asStateFlow()

    // Calculated Dashboard Metrics
    val dashboardMetrics: StateFlow<DashboardMetrics> = combine(
        invoices,
        timeEntries,
        projects,
        clients
    ) { invList, entryList, projList, clientList ->
        val paid = invList.filter { it.status == "PAID" }.sumOf { it.totalAmount }
        val pending = invList.filter { it.status in listOf("SENT", "DRAFT", "OVERDUE") }.sumOf { it.totalAmount }
        val hours = entryList.sumOf { it.durationSeconds } / 3600.0
        val activeProj = projList.count { it.status == "ACTIVE" }

        DashboardMetrics(
            totalPaidRevenue = paid,
            pendingRevenue = pending,
            totalHoursLogged = hours,
            activeProjectsCount = activeProj,
            clientsCount = clientList.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardMetrics()
    )

    init {
        // Coroutine loop to tick the live timer whenever an active entry exists
        viewModelScope.launch {
            while (isActive) {
                val active = activeTimeEntry.value
                if (active != null) {
                    val elapsed = (System.currentTimeMillis() - active.startTimeMillis) / 1000
                    _liveTimerSeconds.value = elapsed.coerceAtLeast(0)
                } else {
                    _liveTimerSeconds.value = 0L
                }
                delay(1000)
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setWorkspaceName(name: String) {
        _workspaceName.value = name
    }

    fun setCurrency(newCurrency: String) {
        _currency.value = newCurrency
    }

    // --- Timer Actions ---
    fun startTimer(projectId: String?, description: String, isBillable: Boolean = true) {
        viewModelScope.launch {
            repository.startTimer(projectId, description, isBillable)
        }
    }

    fun stopTimer() {
        viewModelScope.launch {
            repository.stopActiveTimer()
            _liveTimerSeconds.value = 0L
        }
    }

    fun addManualTime(projectId: String?, description: String, hours: Double, isBillable: Boolean = true) {
        viewModelScope.launch {
            repository.addManualTimeEntry(projectId, description, hours, isBillable)
        }
    }

    fun deleteTimeEntry(entry: TimeEntryEntity) {
        viewModelScope.launch {
            repository.deleteTimeEntry(entry)
        }
    }

    // --- Client Actions ---
    fun addClient(name: String, company: String, email: String, phone: String, hourlyRate: Double) {
        viewModelScope.launch {
            val client = ClientEntity(
                name = name,
                company = company,
                email = email,
                phone = phone,
                defaultHourlyRate = hourlyRate,
                currency = _currency.value
            )
            repository.addClient(client)
        }
    }

    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.deleteClient(client)
        }
    }

    // --- Project & Task Actions ---
    fun addProject(
        clientId: String,
        name: String,
        description: String,
        colorHex: String,
        hourlyRate: Double,
        budget: Double
    ) {
        viewModelScope.launch {
            val project = ProjectEntity(
                clientId = clientId,
                name = name,
                description = description,
                colorHex = colorHex,
                hourlyRate = hourlyRate,
                budgetAmount = budget
            )
            repository.addProject(project)
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }

    fun addTask(projectId: String, title: String, estimatedHours: Double) {
        viewModelScope.launch {
            val task = TaskEntity(
                projectId = projectId,
                title = title,
                estimatedHours = estimatedHours
            )
            repository.addTask(task)
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    // --- Invoice Actions ---
    fun createInvoice(
        clientId: String,
        invoiceNumber: String,
        items: List<InvoiceItemEntity>,
        taxRate: Double = 0.0,
        discountAmount: Double = 0.0,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val subtotal = items.sumOf { it.totalPrice }
            val taxAmount = subtotal * (taxRate / 100.0)
            val total = (subtotal + taxAmount - discountAmount).coerceAtLeast(0.0)

            val invoice = InvoiceEntity(
                clientId = clientId,
                invoiceNumber = invoiceNumber,
                issueDateMillis = System.currentTimeMillis(),
                dueDateMillis = System.currentTimeMillis() + (14L * 24 * 60 * 60 * 1000), // +14 days
                subtotal = subtotal,
                taxRate = taxRate,
                taxAmount = taxAmount,
                discountAmount = discountAmount,
                totalAmount = total,
                currency = _currency.value,
                status = "SENT",
                notes = notes.ifBlank { "Generated via FreelanceFlow" }
            )

            // Connect items to new invoice ID
            val mappedItems = items.map { it.copy(invoiceId = invoice.id) }
            repository.createInvoice(invoice, mappedItems)
        }
    }

    fun markInvoicePaid(invoiceId: String, gateway: String) {
        viewModelScope.launch {
            repository.markInvoiceAsPaid(invoiceId, gateway)
        }
    }

    fun deleteInvoice(invoice: InvoiceEntity) {
        viewModelScope.launch {
            repository.deleteInvoice(invoice)
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
        }
    }
}
