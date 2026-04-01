package com.johan.misgastos.ui.screens.home

import com.johan.misgastos.utils.toEpochMillis
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeDashboardCalculatorTest {

    private data class FakeExpense(
        val id: Long,
        val amountInCents: Long,
        val occurredAt: Long,
    )

    @Test
    fun `calculateDashboardMetrics computes totals and keeps recent items`() {
        val today = LocalDate.of(2026, 3, 31)
        val expenses = listOf(
            expense(id = 1L, amountInCents = 3000L, date = today, time = LocalTime.of(18, 0)),
            expense(id = 2L, amountInCents = 1500L, date = today, time = LocalTime.of(9, 30)),
            expense(id = 3L, amountInCents = 2200L, date = LocalDate.of(2026, 3, 15), time = LocalTime.of(12, 0)),
            expense(id = 4L, amountInCents = 800L, date = LocalDate.of(2026, 2, 28), time = LocalTime.of(20, 0)),
        )

        val metrics = calculateDashboardMetrics(
            items = expenses,
            today = today,
            recentLimit = 3,
            dateSelector = { it.occurredAt },
            amountSelector = { it.amountInCents },
        )

        assertEquals(4500L, metrics.todayTotalInCents)
        assertEquals(4500L, metrics.weekTotalInCents)
        assertEquals(6700L, metrics.monthTotalInCents)
        assertEquals(listOf(1L, 2L, 3L), metrics.recentItems.map { it.id })
    }

    @Test
    fun `calculateDashboardMetrics handles empty input`() {
        val metrics = calculateDashboardMetrics<FakeExpense>(
            items = emptyList(),
            today = LocalDate.of(2026, 3, 31),
            dateSelector = { it.occurredAt },
            amountSelector = { it.amountInCents },
        )

        assertEquals(0L, metrics.todayTotalInCents)
        assertEquals(0L, metrics.weekTotalInCents)
        assertEquals(0L, metrics.monthTotalInCents)
        assertEquals(emptyList<FakeExpense>(), metrics.recentItems)
    }

    @Test
    fun `calculateDashboardMetrics separates weekly total from older items in the same month`() {
        val today = LocalDate.of(2026, 3, 31)
        val expenses = listOf(
            expense(id = 1L, amountInCents = 4000L, date = LocalDate.of(2026, 3, 30), time = LocalTime.NOON),
            expense(id = 2L, amountInCents = 2500L, date = LocalDate.of(2026, 3, 23), time = LocalTime.NOON),
        )

        val metrics = calculateDashboardMetrics(
            items = expenses,
            today = today,
            dateSelector = { it.occurredAt },
            amountSelector = { it.amountInCents },
        )

        assertEquals(0L, metrics.todayTotalInCents)
        assertEquals(4000L, metrics.weekTotalInCents)
        assertEquals(6500L, metrics.monthTotalInCents)
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
            occurredAt = toEpochMillis(date, time),
        )
    }
}
