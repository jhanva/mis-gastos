package com.johan.misgastos.ui.screens.expenses

import com.johan.misgastos.domain.model.Category
import com.johan.misgastos.domain.model.Expense
import com.johan.misgastos.domain.model.ExpenseSortOption
import com.johan.misgastos.domain.model.PaymentMethod
import com.johan.misgastos.utils.toEpochMillis
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpenseGroupingTest {

    private val category = Category(
        id = 1L,
        name = "Alimentación",
        iconName = "food",
        colorHex = "#2E7D32",
        isActive = true,
        createdAt = 1L,
        updatedAt = 1L,
    )

    @Test
    fun `groupExpensesByDay groups by local date and orders newest first by default`() {
        val expenses = listOf(
            expense(id = 1L, amountInCents = 1000L, date = LocalDate.of(2026, 3, 30), time = LocalTime.of(8, 0)),
            expense(id = 2L, amountInCents = 2000L, date = LocalDate.of(2026, 3, 31), time = LocalTime.of(10, 0)),
            expense(id = 3L, amountInCents = 3000L, date = LocalDate.of(2026, 3, 31), time = LocalTime.of(18, 0)),
        )

        val groups = groupExpensesByDay(expenses, ExpenseSortOption.NEWEST)

        assertEquals(2, groups.size)
        assertEquals(LocalDate.of(2026, 3, 31), groups[0].date)
        assertEquals(listOf(3L, 2L), groups[0].expenses.map { it.id })
        assertEquals(5000L, groups[0].totalAmountInCents)
    }

    @Test
    fun `groupExpensesByDay sorts items by amount inside each day when requested`() {
        val expenses = listOf(
            expense(id = 1L, amountInCents = 1500L, date = LocalDate.of(2026, 3, 31), time = LocalTime.of(8, 0)),
            expense(id = 2L, amountInCents = 3200L, date = LocalDate.of(2026, 3, 31), time = LocalTime.of(10, 0)),
            expense(id = 3L, amountInCents = 800L, date = LocalDate.of(2026, 3, 30), time = LocalTime.of(18, 0)),
        )

        val groups = groupExpensesByDay(expenses, ExpenseSortOption.AMOUNT_DESC)

        assertEquals(listOf(2L, 1L), groups[0].expenses.map { it.id })
    }

    private fun expense(
        id: Long,
        amountInCents: Long,
        date: LocalDate,
        time: LocalTime,
    ): Expense {
        return Expense(
            id = id,
            amountInCents = amountInCents,
            title = "Gasto $id",
            description = null,
            category = category,
            paymentMethod = PaymentMethod.CASH,
            occurredAt = toEpochMillis(date, time),
            notes = null,
            createdAt = id,
            updatedAt = id,
        )
    }
}
