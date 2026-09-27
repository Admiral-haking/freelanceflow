# FreelanceFlow 💼⏱️

> **The All-in-One Client, Project, Time Tracking, Invoice & Payment Platform for Freelancers & Boutique Agencies.**

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-M3-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-0052CC.svg?style=flat)](#architecture)
[![Room Database](https://img.shields.io/badge/Database-Room%20SQLite%20(Offline--First)-F43F5E.svg?style=flat)](https://developer.android.com/training/data-storage/room)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## 🌟 Overview

**FreelanceFlow** is an open-source, mobile-first SaaS solution designed specifically for independent software engineers, product designers, consultants, and creative studios. It eliminates tool fragmentation by providing a unified workflow:

$$\text{Client CRM} \longrightarrow \text{Projects \& Tasks} \longrightarrow \text{Live Time Tracking} \longrightarrow \text{Automated Invoices} \longrightarrow \text{Payment Gateways}$$

Built with modern **Android Jetpack Compose**, **Clean Architecture**, and an **Offline-First Room SQLite** persistence layer.

---

## ✨ Key Features

### ⏱️ Real-Time Time Tracking & Timesheet
- **Interactive Digital Stopwatch:** Start/stop stopwatch with animated live status banner and second-by-second updates.
- **Manual Time Entry:** Quickly log past hours worked without running a live timer.
- **Billable vs. Non-Billable Flagging:** Ensure every invoiced second is accurately accounted for.
- **Project Association:** Tag sessions to client projects or record general studio tasks.

### 📁 Projects & Task Management
- **Budget & Rate Tracking:** Configure project hourly rates, total budgets, and custom branding colors.
- **Budget Utilization Progress:** Real-time visual progress bars showing money earned vs. client budget cap.
- **Interactive Task Checklist:** Add, complete, and prioritize deliverables per project.

### 🧾 Invoices & Multi-Gateway Billing
- **Instant Invoice Generation:** Create invoices with configurable tax rates, line items, and payment terms.
- **Multi-Gateway Payment Registration:** Record payments from **Stripe**, **زرین‌پال (Zarinpal)**, or **Manual Cash/Wire Transfer** with transaction reference tracking.
- **Dynamic Status Lifecycle:** Manage `DRAFT`, `SENT`, `PAID`, and `OVERDUE` states.
- **Digital Receipt Preview:** View clean invoice summaries directly within the app.

### 👥 Client Relationship Management (CRM)
- **Client Directory:** Store company details, contact emails, phone numbers, and default billing rates.
- **Direct System Intents:** One-tap phone calling and email composition from within the client card.

### 📊 Profitability Analytics & Reports
- **Billable Ratio Indicator:** Measure the percentage of time spent on income-generating activities vs. admin work.
- **Per-Project Earnings:** Breakdown of hours and revenue generated across all active engagements.
- **Executive KPI Cards:** Quick summary of Paid Revenue, Outstanding Invoices, and Total Hours Logged.

### 🌐 Offline-First & Customization
- **100% Offline Capability:** Powered by SQLite with Room ORM. All actions function seamlessly without internet access.
- **Multi-Currency Support:** Switch between **USD ($)**, **EUR (€)**, and **تومان (IRT)**.
- **Workspace Personalization:** Rename studio/agency name and reset sample test data at any time.

---

## 🏗️ Architecture & Technology Stack

FreelanceFlow is architected using **Android Clean Architecture** and **MVVM** principles:

```
[ UI Layer (Jetpack Compose + Material 3) ]
                     │
                     ▼ (StateFlow & UI Events)
         [ ViewModel Layer (MVVM) ]
                     │
                     ▼ (Coroutines & Kotlin Flow)
       [ Repository Pattern (Single Source of Truth) ]
                     │
       ┌─────────────┴─────────────┐
       ▼                           ▼
[ Local Database (Room SQLite) ]   [ Remote API (Retrofit / NestJS) ]
```

### Libraries & Frameworks
| Layer | Technologies |
| :--- | :--- |
| **Language** | Kotlin 2.0+ (100% Coroutines & Flow) |
| **UI Toolkit** | Jetpack Compose (BOM), Material Design 3 (M3) |
| **Architecture** | MVVM, Repository Pattern, Clean Architecture |
| **Local Persistence** | Android Room Database (KSP, Foreign Keys, Compound Indices) |
| **Lifecycle** | AndroidX ViewModel Compose, `collectAsStateWithLifecycle` |
| **Networking Ready** | Retrofit 2, Moshi, OkHttp 3, Coroutine Dispatchers |
| **Testing** | JUnit 4, Robolectric, Compose UI Testing |

---

## 📂 Project Structure

```
app/src/main/java/com/example/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   └── Daos.kt                # ClientDao, ProjectDao, TimeEntryDao, InvoiceDao, PaymentDao
│   │   ├── entity/
│   │   │   └── Entities.kt            # Room entities (7 relational tables with Foreign Keys)
│   │   ├── AppDatabase.kt             # Room Database setup & singleton provider
│   │   └── DatabasePrepopulator.kt    # Realistic sample data preloader
│   └── repository/
│       └── FreelanceFlowRepository.kt # Reactive repository bridging UI and Room
├── ui/
│   ├── components/
│   │   └── CommonUi.kt                # MetricCard, StatusBadge, ActiveTimerBanner, AppFormatters
│   ├── screens/
│   │   ├── DashboardScreen.kt         # Overview KPIs, live timer, recent invoices
│   │   ├── TimeTrackerScreen.kt       # Digital clock, manual entry dialog, timesheet history
│   │   ├── ProjectsScreen.kt          # Projects list, task checklists, budget bars
│   │   ├── InvoicesScreen.kt          # Filter chips, invoice creation, payment recording
│   │   ├── ClientsScreen.kt           # CRM cards, call/email intents, client creation
│   │   ├── ReportsScreen.kt           # Billable time gauge, project financial performance
│   │   └── SettingsScreen.kt          # Workspace branding, currency selector, sync engine
│   ├── theme/
│   │   ├── Color.kt                   # Branded Electric Indigo & Vivid Cyan color palettes
│   │   ├── Theme.kt                   # Material 3 dynamic & dark/light color schemes
│   │   └── Type.kt                    # Typography configuration
│   └── viewmodel/
│       └── FreelanceFlowViewModel.kt  # StateFlow reactive states & user actions
├── FreelanceFlowApp.kt                # TopAppBar, 5-Tab NavigationBar, BackHandler
└── MainActivity.kt                    # Edge-to-edge launcher activity
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Koala / Ladybug or newer recommended)
- **JDK 17** or **JDK 21**
- **Android SDK** API 34+ (Min SDK: 24, Target SDK: 36)

### Clone & Build
```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/freelanceflow.git

# Navigate into the project directory
cd freelanceflow

# Build debug APK using Gradle wrapper
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest
```

---

## 🇮🇷 راهنمای فارسی (فریلنس‌فلو)

پروژه **FreelanceFlow** یک بستر جامع و مدرن برای مدیریت مشتریان، ثبت زمان کاری (تایم‌شیت)، صدور فاکتور و مدیریت پرداخت‌ها برای فریلنسرها، برنامه‌نویسان، طراحان و آژانس‌های کوچک است.

### امکانات شاخص:
1. **استاپ‌واچ زنده و برگه تایم‌شیت:** محاسبه زمان با ثانیه‌شمار زنده، ثبت دستی ساعات کاری گذشته و برچسب‌گذاری درآمدزا بودن (Billable).
2. **مدیریت پروژه و تسک‌ها:** کنترل سقف بودجه، نرخ ساعتی و وظایف مرحله‌ای.
3. **فاکتور و پرداخت چنددرگاهی:** صدور فاکتور رسمی، محاسبه خودکار مالیات و ثبت تسویه با درگاه‌های بین‌المللی (Stripe) و داخلی (زرین‌پال).
4. **دفترچه مشتریان (CRM):** تماس تلفنی و ایمیل مستقیم از داخل برنامه.
5. **معماری ۱۰۰٪ آفلاین:** استفاده از دیتابیس Room با قابلیت همگام‌سازی ابری.
6. **پشتیبانی از ارزهای مختلف:** دلار، یورو و تومان.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
