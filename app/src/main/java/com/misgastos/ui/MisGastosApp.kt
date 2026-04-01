package com.misgastos.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.misgastos.domain.model.AppThemeMode
import com.misgastos.navigation.AppDestination
import com.misgastos.navigation.MisGastosNavGraph
import com.misgastos.ui.components.AppWidthSizeClass
import com.misgastos.ui.components.rememberAppWidthSizeClass
import com.misgastos.ui.theme.MisGastosTheme

private data class TopLevelDestinationUi(
    val destination: AppDestination,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun MisGastosApp(
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackbarController = remember(snackbarHostState, scope) {
        SnackbarController(snackbarHostState, scope)
    }
    val widthSizeClass = rememberAppWidthSizeClass()

    val topLevelDestinations = remember {
        listOf(
            TopLevelDestinationUi(AppDestination.Home, "Inicio", Icons.Outlined.Home),
            TopLevelDestinationUi(AppDestination.Expenses, "Gastos", Icons.AutoMirrored.Outlined.List),
            TopLevelDestinationUi(AppDestination.Subscriptions, "Plataformas", Icons.Outlined.Add),
            TopLevelDestinationUi(AppDestination.Categories, "Categorias", Icons.Outlined.Edit),
            TopLevelDestinationUi(AppDestination.Settings, "Ajustes", Icons.Outlined.Settings),
        )
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val topLevelRoutes = topLevelDestinations.map { it.destination.route }.toSet()
    val showTopLevelNavigation = currentRoute in topLevelRoutes
    val useNavigationRail = showTopLevelNavigation && widthSizeClass == AppWidthSizeClass.EXPANDED
    val showFab = currentRoute == AppDestination.Home.route || currentRoute == AppDestination.Expenses.route

    val darkTheme = when (appUiState.preferences.themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    MisGastosTheme(darkTheme = darkTheme) {
        CompositionLocalProvider(LocalSnackbarController provides snackbarController) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (showTopLevelNavigation && !useNavigationRail) {
                        NavigationBar {
                            topLevelDestinations.forEach { item ->
                                val selected = currentRoute == item.destination.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(item.destination.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label,
                                        )
                                    },
                                    label = { Text(item.label) },
                                )
                            }
                        }
                    }
                },
                floatingActionButton = {
                    if (showFab) {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(AppDestination.ExpenseEditor.createRoute())
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = "Registrar gasto",
                            )
                        }
                    }
                },
            ) { innerPadding ->
                if (useNavigationRail) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        NavigationRail(
                            modifier = Modifier.padding(innerPadding),
                        ) {
                            topLevelDestinations.forEach { item ->
                                val selected = currentRoute == item.destination.route
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(item.destination.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label,
                                        )
                                    },
                                    label = { Text(item.label) },
                                )
                            }
                        }
                        MisGastosNavGraph(
                            navController = navController,
                            innerPadding = PaddingValues(),
                            preferences = appUiState.preferences,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    MisGastosNavGraph(
                        navController = navController,
                        innerPadding = innerPadding,
                        preferences = appUiState.preferences,
                    )
                }
            }
        }
    }
}
