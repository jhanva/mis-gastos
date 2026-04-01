package com.johan.misgastos.navigation

sealed class AppDestination(val route: String) {
    data object Home : AppDestination("home")
    data object Expenses : AppDestination("expenses")
    data object Subscriptions : AppDestination("subscriptions")
    data object Categories : AppDestination("categories")
    data object Settings : AppDestination("settings")

    data object ExpenseDetail : AppDestination("expense_detail") {
        const val ARG_EXPENSE_ID = "expenseId"
        val routeWithArgs = "$route/{$ARG_EXPENSE_ID}"

        fun createRoute(expenseId: Long): String = "$route/$expenseId"
    }

    data object ExpenseEditor : AppDestination("expense_editor") {
        const val ARG_EXPENSE_ID = "expenseId"
        val routeWithArgs = "$route?$ARG_EXPENSE_ID={$ARG_EXPENSE_ID}"

        fun createRoute(expenseId: Long? = null): String {
            return if (expenseId == null) route else "$route?$ARG_EXPENSE_ID=$expenseId"
        }
    }
}
