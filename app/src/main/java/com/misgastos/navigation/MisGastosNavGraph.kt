package com.misgastos.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.misgastos.domain.model.UserPreferences
import com.misgastos.ui.screens.categories.CategoriesScreen
import com.misgastos.ui.screens.expensedetail.ExpenseDetailScreen
import com.misgastos.ui.screens.expenseeditor.ExpenseEditorScreen
import com.misgastos.ui.screens.expenses.ExpensesScreen
import com.misgastos.ui.screens.home.HomeScreen
import com.misgastos.ui.screens.settings.SettingsScreen
import com.misgastos.ui.screens.subscriptions.SubscriptionsScreen
import com.misgastos.ui.components.ResponsiveContentFrame

@Composable
fun MisGastosNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    preferences: UserPreferences,
    modifier: Modifier = Modifier,
) {
    ResponsiveContentFrame(
        modifier = modifier.padding(innerPadding),
    ) {
        NavHost(
            navController = navController,
            startDestination = AppDestination.Home.route,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(AppDestination.Home.route) {
                HomeScreen(
                    preferences = preferences,
                    onAddExpense = {
                        navController.navigate(AppDestination.ExpenseEditor.createRoute())
                    },
                    onExpenseClick = { expenseId ->
                        navController.navigate(AppDestination.ExpenseDetail.createRoute(expenseId))
                    },
                )
            }
            composable(AppDestination.Expenses.route) {
                ExpensesScreen(
                    preferences = preferences,
                    onExpenseClick = { expenseId ->
                        navController.navigate(AppDestination.ExpenseDetail.createRoute(expenseId))
                    },
                )
            }
            composable(AppDestination.Subscriptions.route) {
                SubscriptionsScreen(preferences = preferences)
            }
            composable(AppDestination.Categories.route) {
                CategoriesScreen()
            }
            composable(AppDestination.Settings.route) {
                SettingsScreen(preferences = preferences)
            }
            composable(
                route = AppDestination.ExpenseDetail.routeWithArgs,
                arguments = listOf(
                    navArgument(AppDestination.ExpenseDetail.ARG_EXPENSE_ID) {
                        type = NavType.LongType
                    },
                ),
            ) {
                ExpenseDetailScreen(
                    preferences = preferences,
                    onEdit = { expenseId ->
                        navController.navigate(AppDestination.ExpenseEditor.createRoute(expenseId))
                    },
                    onClose = { navController.popBackStack() },
                )
            }
            composable(
                route = AppDestination.ExpenseEditor.routeWithArgs,
                arguments = listOf(
                    navArgument(AppDestination.ExpenseEditor.ARG_EXPENSE_ID) {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                ),
            ) {
                ExpenseEditorScreen(
                    preferences = preferences,
                    onClose = { navController.popBackStack() },
                )
            }
        }
    }
}
