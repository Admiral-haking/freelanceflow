package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.ClientEntity
import com.example.data.local.entity.InvoiceEntity
import com.example.data.local.entity.InvoiceItemEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.TimeEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY createdAt DESC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    suspend fun getClientById(id: String): ClientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Delete
    suspend fun deleteClient(client: ClientEntity)

    @Query("SELECT COUNT(*) FROM clients")
    suspend fun getClientCount(): Int
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE clientId = :clientId ORDER BY createdAt DESC")
    fun getProjectsByClient(clientId: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    // Tasks
    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY createdAt ASC")
    fun getTasksForProject(projectId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)
}

@Dao
interface TimeEntryDao {
    @Query("SELECT * FROM time_entries ORDER BY startTimeMillis DESC")
    fun getAllTimeEntries(): Flow<List<TimeEntryEntity>>

    @Query("SELECT * FROM time_entries WHERE endTimeMillis IS NULL LIMIT 1")
    fun getActiveTimeEntry(): Flow<TimeEntryEntity?>

    @Query("SELECT * FROM time_entries WHERE endTimeMillis IS NULL LIMIT 1")
    suspend fun getActiveTimeEntryImmediate(): TimeEntryEntity?

    @Query("SELECT * FROM time_entries WHERE projectId = :projectId ORDER BY startTimeMillis DESC")
    fun getTimeEntriesForProject(projectId: String): Flow<List<TimeEntryEntity>>

    @Query("SELECT * FROM time_entries WHERE isBillable = 1 AND invoiceId IS NULL ORDER BY startTimeMillis DESC")
    fun getUnbilledEntries(): Flow<List<TimeEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeEntry(entry: TimeEntryEntity)

    @Update
    suspend fun updateTimeEntry(entry: TimeEntryEntity)

    @Delete
    suspend fun deleteTimeEntry(entry: TimeEntryEntity)

    @Query("UPDATE time_entries SET invoiceId = :invoiceId WHERE id IN (:entryIds)")
    suspend fun markEntriesAsInvoiced(entryIds: List<String>, invoiceId: String)
}

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY issueDateMillis DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceById(id: String): InvoiceEntity?

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId")
    fun getItemsForInvoice(invoiceId: String): Flow<List<InvoiceItemEntity>>

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId")
    suspend fun getItemsForInvoiceImmediate(invoiceId: String): List<InvoiceItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceItems(items: List<InvoiceItemEntity>)

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    @Query("UPDATE invoices SET status = :status WHERE id = :invoiceId")
    suspend fun updateInvoiceStatus(invoiceId: String, status: String)

    @Delete
    suspend fun deleteInvoice(invoice: InvoiceEntity)

    @Transaction
    suspend fun insertInvoiceWithItems(invoice: InvoiceEntity, items: List<InvoiceItemEntity>) {
        insertInvoice(invoice)
        insertInvoiceItems(items)
    }
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paidAtMillis DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE invoiceId = :invoiceId")
    fun getPaymentsForInvoice(invoiceId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)
}
