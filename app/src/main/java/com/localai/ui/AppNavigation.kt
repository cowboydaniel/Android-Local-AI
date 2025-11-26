package com.localai.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.localai.ui.screens.InferenceSessionScreen
import com.localai.ui.screens.ModelManagementScreen
import com.localai.ui.screens.SettingsScreen
import com.localai.ui.screens.home.HomeScreen

private enum class AppDestination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("home", "Home", Icons.Filled.Home),
    MODELS("models", "Models", Icons.Filled.ListAlt),
    SESSION("session", "Session", Icons.Filled.Timeline),
    SETTINGS("settings", "Settings", Icons.Filled.Settings),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidLocalAiApp() {
    val navController = rememberNavController()
    val destinations = AppDestination.values().toList()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = currentDestination?.labelOrDefault() ?: "Android Local AI") })
        },
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    val selected = currentDestination.isTopLevelDestination(destination.route)
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.HOME.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(AppDestination.HOME.route) {
                ScreenContainer(title = "Home", description = "Overview of your on-device AI setup.") {
                    HomeScreen()
                }
            }
            composable(AppDestination.MODELS.route) {
                ScreenContainer(title = "Model management", description = "Browse, download, and organize local models.") {
                    ModelManagementScreen()
                }
            }
            composable(AppDestination.SESSION.route) {
                ScreenContainer(title = "Inference session", description = "Inspect active sessions and dispatch new requests.") {
                    InferenceSessionScreen()
                }
            }
            composable(AppDestination.SETTINGS.route) {
                ScreenContainer(title = "Settings", description = "Configure app preferences and runtime options.") {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
private fun ScreenContainer(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Start
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = description,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start
        )
        content()
    }
}

private fun NavDestination?.labelOrDefault(): String {
    return this?.route?.let { route ->
        AppDestination.values().firstOrNull { it.route == route }?.label
    } ?: "Android Local AI"
}

private fun NavDestination?.isTopLevelDestination(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
