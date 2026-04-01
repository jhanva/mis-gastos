package com.misgastos.ui.screens.home

import com.misgastos.utils.endOfDayMillis
import com.misgastos.utils.monthRangeMillis
import com.misgastos.utils.startOfDayMillis
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class DashboardMetrics<T>(
    val todayTotalInCents: Long,
    val weekTotalInCents: Long,
    val monthTotalInCents: Long,
    val recentItems: List<T>,
)

fun <T> calculateDashboardMetrics(
    items: List<T>,
    today: LocalDate = LocalDate.now(),
    recentLimit: Int = 6,
    dateSelector: (T) -> Long,
    amountSelector: (T) -> Long,
): DashboardMetrics<T> {
    val todayStart = startOfDayMillis(today)
    val todayEnd = endOfDayMillis(today)
    val weekStart = startOfDayMillis(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    val weekEnd = endOfDayMillis(today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)))
    val monthRange = monthRangeMillis(today)

    var todayTotalInCents = 0L
    var weekTotalInCents = 0L
    var monthTotalInCents = 0L

    items.forEach { item ->
        val occurredAt = dateSelector(item)
        val amount = amountSelector(item)

        if (occurredAt in todayStart..todayEnd) {
            todayTotalInCents += amount
        }
        if (occurredAt in weekStart..weekEnd) {
            weekTotalInCents += amount
        }
        if (occurredAt in monthRange) {
            monthTotalInCents += amount
        }
    }

    return DashboardMetrics(
        todayTotalInCents = todayTotalInCents,
        weekTotalInCents = weekTotalInCents,
        monthTotalInCents = monthTotalInCents,
        recentItems = items.take(recentLimit),
    )
}
