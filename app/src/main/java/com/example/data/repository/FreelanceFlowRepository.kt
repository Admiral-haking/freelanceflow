package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.DatabasePrepopulator
import com.example.data.local.entity.ClientEntity
import com.example.data.local.entity.InvoiceEntity
import com.example.data.local.entity.InvoiceItemEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.TimeEntryEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FreelanceFlowRepository(private val database: AppDatabase) {

    private val clientDao = database.clientDao()
    private val projectDao = database.projectDao()
    private val timeEntryDao = database.timeEntryDao()
    private val invoiceDao = database.invoiceDao()
    private val paymentDao = database.paymentDao()

    // --- Clients ---
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()

    suspend fun addClient(client: ClientEntity) = clientDao.insertClient(client)
    suspend fun updateClient(client: ClientEntity) = clientDao.updateClient(client)
    suspend fun deleteClient(client: ClientEntity) = clientDao.deleteClient(client)

    // --- Projects ---
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val allTasks: Flow<List<TaskEntity>> = projectDao.getAllTasks()

    suspend fun addProject(project: ProjectEntity) = projectDao.insertProject(project)
    suspend fun updateProject(project: ProjectEntity) = projectDao.updateProject(project)
    suspend fun deleteProject(project: ProjectEntity) = projectDao.deleteProject(project)

    fun getTasksForProject(projectId: String): Flow<List<TaskEntity>> = projectDao.getTasksForProject(projectId)
    suspend fun addTask(task: TaskEntity) = projectDao.insertTask(task)
    suspend fun toggleTaskCompletion(task: TaskEntity) = projectDao.updateTask(task.copy(isCompleted = !task.isCompleted))
    suspend fun deleteTask(task: TaskEntity) = projectDao.deleteTask(task)

    // --- Time Tracking & Timer ---
    val allTimeEntries: Flow<List<TimeEntryEntity>> = timeEntryDao.getAllTimeEntries()
    val activeTimeEntry: Flow<TimeEntryEntity?> = timeEntryDao.getActiveTimeEntry()
    val unbilledEntries: Flow<List<TimeEntryEntity>> = timeEntryDao.getUnbilledEntries()

    suspend fun startTimer(projectId: String?, description: String, isBillable: Boolean = true): TimeEntryEntity {
        // Stop any currently running timer first
        stopActiveTimer()
        val newEntry = TimeEntryEntity(
            projectId = projectId,
            description = description,
            startTimeMillis = System.currentTimeMillis(),
            endTimeMillis = null,
            isBillable = isBillable
        )
        timeEntryDao.insertTimeEntry(newEntry)
        return newEntry
    }

    suspend fun stopActiveTimer() {
        val active = timeEntryDao.getActiveTimeEntryImmediate()
        if (active != null) {
            val now = System.currentTimeMillis()
            val durationSec = ((now - active.startTimeMillis) / 1000).coerceAtLeast(0)
            val updated = active.copy(
                endTimeMillis = now,
                durationSeconds = durationSec
            )
            timeEntryDao.updateTimeEntry(updated)
        }
    }

    suspend fun addManualTimeEntry(
        projectId: String?,
        description: String,
        hours: Double,
        isBillable: Boolean = true
    ) {
        val now = System.currentTimeMillis()
        val durationSec = (hours * 3600).toLong()
        val entry = TimeEntryEntity(
            projectId = projectId,
            description = description,
            startTimeMillis = now - (durationSec * 1000),
            endTimeMillis = now,
            durationSeconds = durationSec,
            isBillable = isBillable
        )
        timeEntryDao.insertTimeEntry(entry)
    }

    suspend fun deleteTimeEntry(entry: TimeEntryEntity) = timeEntryDao.deleteTimeEntry(entry)

    // --- Invoices ---
    val allInvoices: Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()

    fun getInvoiceItems(invoiceId: String): Flow<List<InvoiceItemEntity>> = invoiceDao.getItemsForInvoice(invoiceId)

    suspend fun createInvoice(
        invoice: InvoiceEntity,
        items: List<InvoiceItemEntity>,
        associatedEntryIds: List<String> = emptyList()
    ) {
        invoiceDao.insertInvoiceWithItems(invoice, items)
        if (associatedEntryIds.isNotEmpty()) {
            timeEntryDao.markEntriesAsInvoiced(associatedEntryIds, invoice.id)
        }
    }

    suspend fun markInvoiceAsPaid(
        invoiceId: String,
        gateway: String = "STRIPE",
        ref: String = "MANUAL-${System.currentTimeMillis()}"
    ) {
        val invoice = invoiceDao.getInvoiceById(invoiceId) ?: return
        invoiceDao.updateInvoiceStatus(invoiceId, "PAID")
        val payment = PaymentEntity(
            invoiceId = invoiceId,
            amount = invoice.totalAmount,
            currency = invoice.currency,
            gateway = gateway,
            transactionRef = ref,
            notes = "Marked as paid in FreelanceFlow"
        )
        paymentDao.insertPayment(payment)
    }

    suspend fun deleteInvoice(invoice: InvoiceEntity) = invoiceDao.deleteInvoice(invoice)

    // --- Payments ---
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()

    // --- Reset demo data helper ---
    suspend fun resetDemoData() {
        DatabasePrepopulator.populateInitialData(database)
    }
}
