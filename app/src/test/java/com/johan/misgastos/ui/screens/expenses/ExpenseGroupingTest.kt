package com.johan.misgastos.ui.screens.expenses

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpenseGroupingTest {

    private data class FakeExpense(
        val id: Long,
        val amountInCents: Long,
        val occurredAt: Long,
    )

    @Test
    fun `groupItemsByDay groups by local date and orders newest first by default`() {
        val expenses = listOf(
            expense(id = 1L, amountInCents = 1000L, date = LocalDate.of(2026, 3, 30), time = LocalTime.of(8, 0)),
            expense(id = 2L, amountInCents = 2000L, date = LocalDate.of(2026, 3, 31), time = LocalTime.of(10, 0)),
            expense(id = 3L, amountInCents = 3000L, date = LocalDate.of(2026, 3, 31), time = LocalTime.of(18, 0)),
        )

        val groups = groupItemsByDay(
            items = expenses,
            sortMode = DayGroupingSortMode.NEWEST,
            dateSelector = { it.occurredAt },
            amountSelector = { it.amountInCents },
        )

        assertEquals(2, groups.size)
        assertEquals(LocalDate.of(2026, 3, 31), groups[0].date)
        assertEquals(listOf(3L, 2L), groups[0].items.map { it.id })
        assertEquals(5000L, groups[0].totalAmountInCents)
    }

    @Test
    fun `groupItemsByDay sorts items by amount inside each day when requested`() {
        val expenses = listOf(
            expense(id = 1L, amountInCents = 1500L, date = LocalDate.of(2026, 3, 31), time = LocalTime.of(8, 0)),
            expense(id = 2L, amountInCents = 3200L, date = LocalDate.of(2026, 3, 31), time = LocalTime.of(10, 0)),
            expense(id = 3L, amountInCents = 800L, date = LocalDate.of(2026, 3, 30), time = LocalTime.of(18, 0)),
        )

        val groups = groupItemsByDay(
            items = expenses,
            sortMode = DayGroupingSortMode.AMOUNT_DESC,
            dateSelector = { it.occurredAt },
            amountSelector = { it.amountInCents },
        )

        assertEquals(listOf(2L, 1L), groups[0].items.map { it.id })
    }

    @Test
    fun `groupItemsByDay orders days oldest first when requested`() {
        val expenses = listOf(
            expense(id = 1L, amountInCents = 1100L, date = LocalDate.of(2026, 3, 31), time = LocalTime.of(8, 0)),
            expense(id = 2L, amountInCents = 900L, date = LocalDate.of(2026, 3, 29), time = LocalTime.of(9, 0)),
        )

        val groups = groupItemsByDay(
            items = expenses,
            sortMode = DayGroupingSortMode.OLDEST,
            dateSelector = { it.occurredAt },
            amountSelector = { it.amountInCents },
        )

        assertEquals(listOf(LocalDate.of(2026, 3, 29), LocalDate.of(2026, 3, 31)), groups.map { it.date })
    }

    private fun expense(
        id: Long,
        amountInCents: Long,
        date: LocalDate,
        time: LocalTime,
    ): FakeExpense {
        return FakeExpense(
            id = id,
            amountInCents = amountInCents,
            occurredAt = date.atTime(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        )
    }
}
