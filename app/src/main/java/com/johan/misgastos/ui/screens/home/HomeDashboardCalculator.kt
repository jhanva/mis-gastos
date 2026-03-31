package com.johan.misgastos.ui.screens.home

import com.johan.misgastos.utils.endOfDayMillis
import com.johan.misgastos.utils.monthRangeMillis
import com.johan.misgastos.utils.startOfDayMillis
import java.time.LocalDate

data class DashboardMetrics<T>(
    val todayTotalInCents: Long,
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
    val monthRange = monthRangeMillis(today)

    var todayTotalInCents = 0L
    var monthTotalInCents = 0L

    items.forEach { item ->
        val occurredAt = dateSelector(item)
        val amount = amountSelector(item)

        if (occurredAt in todayStart..todayEnd) {
            todayTotalInCents += amount
        }
        if (occurredAt in monthRange) {
            monthTotalInCents += amount
        }
    }

    return DashboardMetrics(
        todayTotalInCents = todayTotalInCents,
        monthTotalInCents = monthTotalInCents,
        recentItems = items.take(recentLimit),
    )
}
