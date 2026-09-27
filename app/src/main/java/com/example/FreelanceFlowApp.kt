package com.example

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.ClientsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InvoicesScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TimeTrackerScreen
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandPrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FreelanceFlowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreelanceFlowApp(
    viewModel: FreelanceFlowViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val workspaceName by viewModel.workspaceName.collectAsStateWithLifecycle()
    val activeTimer by viewModel.activeTimeEntry.collectAsStateWithLifecycle()

    // Handle back button on sub-screens to return to Dashboard
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentScreen) {
                            AppScreen.DASHBOARD -> workspaceName
                            AppScreen.TIME_TRACKER -> "Time Tracker"
                            AppScreen.PROJECTS -> "Projects & Tasks"
                            AppScreen.INVOICES -> "Invoices & Billing"
                            AppScreen.CLIENTS -> "Client Directory"
                            AppScreen.ANALYTICS -> "Analytics & Reports"
                            AppScreen.SETTINGS -> "Settings"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    // Analytics Icon
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.ANALYTICS) },
                        modifier = Modifier.testTag("nav_to_analytics")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "Analytics",
                            tint = if (currentScreen == AppScreen.ANALYTICS) BrandPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Settings Icon
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        modifier = Modifier.testTag("nav_to_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (currentScreen == AppScreen.SETTINGS) BrandPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // 1. Dashboard
                NavigationBarItem(
                    selected = currentScreen == AppScreen.DASHBOARD,
                    onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    icon = {
                        Icon(
                            if (currentScreen == AppScreen.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = BrandPrimary.copy(alpha = 0.15f))
                )

                // 2. Time Tracker (with live pulse dot if active!)
                NavigationBarItem(
                    selected = currentScreen == AppScreen.TIME_TRACKER,
                    onClick = { viewModel.navigateTo(AppScreen.TIME_TRACKER) },
                    icon = {
                        Box {
                            Icon(
                                if (currentScreen == AppScreen.TIME_TRACKER) Icons.Filled.Schedule else Icons.Outlined.Schedule,
                                contentDescription = "Time Tracker"
                            )
                            if (activeTimer != null) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(BrandDanger)
                                )
                            }
                        }
                    },
                    label = { Text("Tracker") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = BrandPrimary.copy(alpha = 0.15f))
                )

                // 3. Projects
                NavigationBarItem(
                    selected = currentScreen == AppScreen.PROJECTS,
                    onClick = { viewModel.navigateTo(AppScreen.PROJECTS) },
                    icon = {
                        Icon(
                            if (currentScreen == AppScreen.PROJECTS) Icons.Filled.Folder else Icons.Outlined.Folder,
                            contentDescription = "Projects"
                        )
                    },
                    label = { Text("Projects") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = BrandPrimary.copy(alpha = 0.15f))
                )

                // 4. Invoices
                NavigationBarItem(
                    selected = currentScreen == AppScreen.INVOICES,
                    onClick = { viewModel.navigateTo(AppScreen.INVOICES) },
                    icon = {
                        Icon(
                            if (currentScreen == AppScreen.INVOICES) Icons.Filled.Receipt else Icons.Outlined.Receipt,
                            contentDescription = "Invoices"
                        )
                    },
                    label = { Text("Invoices") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = BrandPrimary.copy(alpha = 0.15f))
                )

                // 5. Clients
                NavigationBarItem(
                    selected = currentScreen == AppScreen.CLIENTS,
                    onClick = { viewModel.navigateTo(AppScreen.CLIENTS) },
                    icon = {
                        Icon(
                            if (currentScreen == AppScreen.CLIENTS) Icons.Filled.People else Icons.Outlined.People,
                            contentDescription = "Clients"
                        )
                    },
                    label = { Text("Clients") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = BrandPrimary.copy(alpha = 0.15f))
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when (currentScreen) {
            AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.TIME_TRACKER -> TimeTrackerScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.PROJECTS -> ProjectsScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.INVOICES -> InvoicesScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.CLIENTS -> ClientsScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.ANALYTICS -> ReportsScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
        }
    }
}
