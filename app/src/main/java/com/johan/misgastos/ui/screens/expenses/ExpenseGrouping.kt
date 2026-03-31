package com.johan.misgastos.ui.screens.expenses

import com.johan.misgastos.domain.model.Expense
import com.johan.misgastos.domain.model.ExpenseSortOption
import com.johan.misgastos.utils.epochMillisToLocalDate
import java.time.LocalDate

data class ExpenseDayGroup(
    val date: LocalDate,
    val expenses: List<Expense>,
) {
    val totalAmountInCents: Long = expenses.sumOf { it.amountInCents }
    val firstExpenseAt: Long = expenses.first().occurredAt
}

fun groupExpensesByDay(
    expenses: List<Expense>,
    sortOption: ExpenseSortOption,
): List<ExpenseDayGroup> {
    val grouped = expenses.groupBy { epochMillisToLocalDate(it.occurredAt) }

    val orderedDates = when (sortOption) {
        ExpenseSortOption.OLDEST -> grouped.keys.sorted()
        else -> grouped.keys.sortedDescending()
    }

    return orderedDates.map { date ->
        val dayExpenses = grouped.getValue(date)
        val orderedExpenses = when (sortOption) {
            ExpenseSortOption.AMOUNT_DESC -> dayExpenses.sortedByDescending { it.amountInCents }
            ExpenseSortOption.AMOUNT_ASC -> dayExpenses.sortedBy { it.amountInCents }
            ExpenseSortOption.OLDEST -> dayExpenses.sortedBy { it.occurredAt }
            ExpenseSortOption.NEWEST -> dayExpenses.sortedByDescending { it.occurredAt }
        }
        ExpenseDayGroup(
            date = date,
            expenses = orderedExpenses,
        )
    }
}
