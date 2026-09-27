package com.example.data.local

import com.example.data.local.entity.ClientEntity
import com.example.data.local.entity.InvoiceEntity
import com.example.data.local.entity.InvoiceItemEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.TimeEntryEntity
import java.util.UUID

object DatabasePrepopulator {
    suspend fun populateInitialData(database: AppDatabase) {
        val clientDao = database.clientDao()
        val projectDao = database.projectDao()
        val timeEntryDao = database.timeEntryDao()
        val invoiceDao = database.invoiceDao()
        val paymentDao = database.paymentDao()

        // 1. Initial Clients
        val client1 = ClientEntity(
            id = "c1-" + UUID.randomUUID().toString().take(8),
            name = "Sarah Jenkins",
            company = "Apex Digital Labs",
            email = "s.jenkins@apexdigital.io",
            phone = "+1 (555) 234-8901",
            address = "San Francisco, CA",
            currency = "USD",
            defaultHourlyRate = 85.0
        )
        val client2 = ClientEntity(
            id = "c2-" + UUID.randomUUID().toString().take(8),
            name = "علیرضا رضایی",
            company = "استودیو خلاق پارس",
            email = "a.rezaei@parsdesign.ir",
            phone = "۰۹۱۲۳۴۵۶۷۸۹",
            address = "تهران، سعادت‌آباد",
            currency = "USD",
            defaultHourlyRate = 60.0
        )
        val client3 = ClientEntity(
            id = "c3-" + UUID.randomUUID().toString().take(8),
            name = "Marcus Vance",
            company = "Nordic Fintech AS",
            email = "marcus@nordicfin.no",
            phone = "+47 21 98 76 54",
            address = "Oslo, Norway",
            currency = "EUR",
            defaultHourlyRate = 95.0
        )

        clientDao.insertClient(client1)
        clientDao.insertClient(client2)
        clientDao.insertClient(client3)

        // 2. Initial Projects
        val project1 = ProjectEntity(
            id = "p1-" + UUID.randomUUID().toString().take(8),
            clientId = client1.id,
            name = "Mobile App Design System & Compose UI",
            description = "Complete redesign and Jetpack Compose component library with dark mode",
            colorHex = "#4F46E5",
            hourlyRate = 85.0,
            budgetAmount = 5000.0,
            status = "ACTIVE"
        )
        val project2 = ProjectEntity(
            id = "p2-" + UUID.randomUUID().toString().take(8),
            clientId = client2.id,
            name = "طراحی پرتال مشتریان و داشبورد SaaS",
            description = "پیاده‌سازی پنل مدیریت با معماری فرانت‌اند و اتصال به وب‌هوک پرداخت",
            colorHex = "#06B6D4",
            hourlyRate = 60.0,
            budgetAmount = 3200.0,
            status = "ACTIVE"
        )
        val project3 = ProjectEntity(
            id = "p3-" + UUID.randomUUID().toString().take(8),
            clientId = client3.id,
            name = "OpenBanking API Backend Integration",
            description = "NestJS microservice for payment orchestration and webhook idempotency",
            colorHex = "#10B981",
            hourlyRate = 95.0,
            budgetAmount = 7500.0,
            status = "ACTIVE"
        )

        projectDao.insertProject(project1)
        projectDao.insertProject(project2)
        projectDao.insertProject(project3)

        // 3. Tasks
        val task1 = TaskEntity(
            id = "t1-" + UUID.randomUUID().toString().take(8),
            projectId = project1.id,
            title = "Design Tokens & Typography Spec",
            isCompleted = true,
            estimatedHours = 8.0
        )
        val task2 = TaskEntity(
            id = "t2-" + UUID.randomUUID().toString().take(8),
            projectId = project1.id,
            title = "Navigation Architecture & Deep Links",
            isCompleted = false,
            estimatedHours = 12.0
        )
        val task3 = TaskEntity(
            id = "t3-" + UUID.randomUUID().toString().take(8),
            projectId = project2.id,
            title = "پیاده‌سازی ماژول صدور فاکتور چندارزی",
            isCompleted = false,
            estimatedHours = 15.0
        )

        projectDao.insertTask(task1)
        projectDao.insertTask(task2)
        projectDao.insertTask(task3)

        // 4. Time Entries
        val now = System.currentTimeMillis()
        val entry1 = TimeEntryEntity(
            id = "te1-" + UUID.randomUUID().toString().take(8),
            projectId = project1.id,
            taskId = task1.id,
            description = "Created M3 color schemes and dynamic preview screens",
            startTimeMillis = now - 1000 * 60 * 60 * 5,
            endTimeMillis = now - 1000 * 60 * 60 * 1,
            durationSeconds = 4 * 3600,
            isBillable = true
        )
        val entry2 = TimeEntryEntity(
            id = "te2-" + UUID.randomUUID().toString().take(8),
            projectId = project2.id,
            taskId = task3.id,
            description = "طراحی جدول پایگاه داده و اسکیمای فاکتورها",
            startTimeMillis = now - 1000 * 60 * 60 * 28,
            endTimeMillis = now - 1000 * 60 * 60 * 25,
            durationSeconds = 3 * 3600,
            isBillable = true
        )

        timeEntryDao.insertTimeEntry(entry1)
        timeEntryDao.insertTimeEntry(entry2)

        // 5. Invoices
        val invoice1 = InvoiceEntity(
            id = "inv1-" + UUID.randomUUID().toString().take(8),
            clientId = client1.id,
            invoiceNumber = "INV-2026-001",
            issueDateMillis = now - 1000L * 60 * 60 * 24 * 7,
            dueDateMillis = now + 1000L * 60 * 60 * 24 * 7,
            subtotal = 1700.0,
            taxRate = 10.0,
            taxAmount = 170.0,
            discountAmount = 0.0,
            totalAmount = 1870.0,
            currency = "USD",
            status = "PAID",
            notes = "Sprint 1 deliverables signed off."
        )

        val item1 = InvoiceItemEntity(
            invoiceId = invoice1.id,
            description = "Mobile App Design System Architecture (20 hrs @ $85/hr)",
            quantity = 20.0,
            unitPrice = 85.0,
            totalPrice = 1700.0
        )

        val payment1 = PaymentEntity(
            invoiceId = invoice1.id,
            amount = 1870.0,
            currency = "USD",
            gateway = "STRIPE",
            transactionRef = "ch_3M4oW72eZvKYlo2C1g9Q2",
            notes = "Card payment processed successfully"
        )

        invoiceDao.insertInvoiceWithItems(invoice1, listOf(item1))
        paymentDao.insertPayment(payment1)

        val invoice2 = InvoiceEntity(
            id = "inv2-" + UUID.randomUUID().toString().take(8),
            clientId = client2.id,
            invoiceNumber = "INV-2026-002",
            issueDateMillis = now - 1000L * 60 * 60 * 24 * 2,
            dueDateMillis = now + 1000L * 60 * 60 * 24 * 12,
            subtotal = 900.0,
            taxRate = 0.0,
            taxAmount = 0.0,
            discountAmount = 50.0,
            totalAmount = 850.0,
            currency = "USD",
            status = "SENT",
            notes = "پیش‌پرداخت ۵۰ درصدی طراحی پنل کاربری"
        )

        val item2 = InvoiceItemEntity(
            invoiceId = invoice2.id,
            description = "مرحله ۱: پیاده‌سازی بک‌اند و پنل اعتبارسنجی (۱۵ ساعت)",
            quantity = 15.0,
            unitPrice = 60.0,
            totalPrice = 900.0
        )

        invoiceDao.insertInvoiceWithItems(invoice2, listOf(item2))
    }
}
