package com.hugodev.horasconamor.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hugodev.horasconamor.R
import com.hugodev.horasconamor.ui.OvertimeViewModel
import com.hugodev.horasconamor.ui.counter.CounterScreen
import com.hugodev.horasconamor.ui.game.PlatformGameScreen
import com.hugodev.horasconamor.ui.history.HistoryScreen
import com.hugodev.horasconamor.ui.settings.SettingsScreen

private object AppRoute {
    const val Home = "home"
    const val History = "history"
    const val Game = "game"
    const val Settings = "settings"
}

private data class TopLevelDestination(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector,
)

private val destinations = listOf(
    TopLevelDestination(AppRoute.Home, R.string.navigation_home, Icons.Filled.Home),
    TopLevelDestination(AppRoute.History, R.string.navigation_history, Icons.Filled.History),
    TopLevelDestination(AppRoute.Game, R.string.navigation_game, Icons.Filled.SportsEsports),
    TopLevelDestination(AppRoute.Settings, R.string.navigation_settings, Icons.Filled.Settings),
)

@Composable
fun AppNavigation(viewModel: OvertimeViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    val label = stringResource(destination.label)
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = label,
                            )
                        },
                        label = { Text(label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Home,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AppRoute.Home) { CounterScreen(viewModel) }
            composable(AppRoute.History) { HistoryScreen(viewModel) }
            composable(AppRoute.Game) { PlatformGameScreen() }
            composable(AppRoute.Settings) { SettingsScreen(viewModel) }
        }
    }
}
