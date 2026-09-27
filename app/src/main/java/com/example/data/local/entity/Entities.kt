package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "clients",
    indices = [Index(value = ["workspaceId"])]
)
data class ClientEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val workspaceId: String = "ws-default",
    val name: String,
    val company: String = "",
    val email: String,
    val phone: String = "",
    val address: String = "",
    val currency: String = "USD",
    val defaultHourlyRate: Double = 50.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "projects",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["workspaceId"]),
        Index(value = ["clientId"])
    ]
)
data class ProjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val workspaceId: String = "ws-default",
    val clientId: String,
    val name: String,
    val description: String = "",
    val colorHex: String = "#4F46E5",
    val hourlyRate: Double = 65.0,
    val budgetAmount: Double = 2500.0,
    val status: String = "ACTIVE", // ACTIVE, COMPLETED, ON_HOLD
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val title: String,
    val isCompleted: Boolean = false,
    val estimatedHours: Double = 10.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "time_entries",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["workspaceId"]),
        Index(value = ["projectId"]),
        Index(value = ["taskId"]),
        Index(value = ["invoiceId"])
    ]
)
data class TimeEntryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val workspaceId: String = "ws-default",
    val projectId: String?,
    val taskId: String? = null,
    val description: String = "",
    val startTimeMillis: Long,
    val endTimeMillis: Long? = null, // null indicates currently running timer
    val durationSeconds: Long = 0,
    val isBillable: Boolean = true,
    val invoiceId: String? = null, // null if not yet invoiced
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["workspaceId"]),
        Index(value = ["clientId"]),
        Index(value = ["invoiceNumber"])
    ]
)
data class InvoiceEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val workspaceId: String = "ws-default",
    val clientId: String,
    val invoiceNumber: String,
    val issueDateMillis: Long,
    val dueDateMillis: Long,
    val subtotal: Double,
    val taxRate: Double = 0.0,
    val taxAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double,
    val currency: String = "USD",
    val status: String = "DRAFT", // DRAFT, SENT, PAID, OVERDUE
    val notes: String = "Thank you for your business!",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoiceId"])]
)
data class InvoiceItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val invoiceId: String,
    val description: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalPrice: Double
)

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoiceId"])]
)
data class PaymentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val invoiceId: String,
    val amount: Double,
    val currency: String = "USD",
    val gateway: String = "STRIPE", // STRIPE, ZARINPAL, MANUAL_CASH, BANK_TRANSFER
    val transactionRef: String = "",
    val notes: String = "",
    val paidAtMillis: Long = System.currentTimeMillis()
)
