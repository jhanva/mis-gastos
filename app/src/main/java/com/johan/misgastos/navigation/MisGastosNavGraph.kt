package com.johan.misgastos.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.johan.misgastos.domain.model.UserPreferences
import com.johan.misgastos.ui.screens.categories.CategoriesScreen
import com.johan.misgastos.ui.screens.expenseeditor.ExpenseEditorScreen
import com.johan.misgastos.ui.screens.expenses.ExpensesScreen
import com.johan.misgastos.ui.screens.home.HomeScreen
import com.johan.misgastos.ui.screens.settings.SettingsScreen

@Composable
fun MisGastosNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    preferences: UserPreferences,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route,
        modifier = Modifier.padding(innerPadding),
    ) {
        composable(AppDestination.Home.route) {
            HomeScreen(
                preferences = preferences,
                onAddExpense = {
                    navController.navigate(AppDestination.ExpenseEditor.createRoute())
                },
                onExpenseClick = { expenseId ->
                    navController.navigate(AppDestination.ExpenseEditor.createRoute(expenseId))
                },
            )
        }
        composable(AppDestination.Expenses.route) {
            ExpensesScreen(
                preferences = preferences,
                onExpenseClick = { expenseId ->
                    navController.navigate(AppDestination.ExpenseEditor.createRoute(expenseId))
                },
            )
        }
        composable(AppDestination.Categories.route) {
            CategoriesScreen()
        }
        composable(AppDestination.Settings.route) {
            SettingsScreen(preferences = preferences)
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
