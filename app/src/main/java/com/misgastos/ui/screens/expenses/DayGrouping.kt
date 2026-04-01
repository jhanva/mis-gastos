package com.misgastos.ui.screens.expenses

import com.misgastos.utils.epochMillisToLocalDate
import java.time.LocalDate

enum class DayGroupingSortMode {
    NEWEST,
    OLDEST,
    AMOUNT_DESC,
    AMOUNT_ASC,
}

data class DayGroup<T>(
    val date: LocalDate,
    val items: List<T>,
    val totalAmountInCents: Long,
    val firstItemAt: Long,
)

fun <T> groupItemsByDay(
    items: List<T>,
    sortMode: DayGroupingSortMode,
    dateSelector: (T) -> Long,
    amountSelector: (T) -> Long,
): List<DayGroup<T>> {
    val grouped = items.groupBy { epochMillisToLocalDate(dateSelector(it)) }

    val orderedDates = when (sortMode) {
        DayGroupingSortMode.OLDEST -> grouped.keys.sorted()
        else -> grouped.keys.sortedDescending()
    }

    return orderedDates.map { date ->
        val dayItems = grouped.getValue(date)
        val orderedItems = when (sortMode) {
            DayGroupingSortMode.AMOUNT_DESC -> dayItems.sortedByDescending(amountSelector)
            DayGroupingSortMode.AMOUNT_ASC -> dayItems.sortedBy(amountSelector)
            DayGroupingSortMode.OLDEST -> dayItems.sortedBy(dateSelector)
            DayGroupingSortMode.NEWEST -> dayItems.sortedByDescending(dateSelector)
        }
        DayGroup(
            date = date,
            items = orderedItems,
            totalAmountInCents = orderedItems.sumOf(amountSelector),
            firstItemAt = dateSelector(orderedItems.first()),
        )
    }
}
