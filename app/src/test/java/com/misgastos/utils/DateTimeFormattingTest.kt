package com.misgastos.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class DateTimeFormattingTest {

    @Test
    fun `formatDate uses requested pattern`() {
        val epochMillis = toEpochMillis(
            date = LocalDate.of(2026, 3, 31),
            time = LocalTime.of(14, 5),
        )

        assertEquals("31/03/2026", formatDate(epochMillis, "dd/MM/yyyy"))
    }

    @Test
    fun `formatTime uses 24 hour clock`() {
        val epochMillis = toEpochMillis(
            date = LocalDate.of(2026, 3, 31),
            time = LocalTime.of(14, 5),
        )

        assertEquals("14:05", formatTime(epochMillis))
    }

    @Test
    fun `formatDateTime combines date pattern and time`() {
        val epochMillis = toEpochMillis(
            date = LocalDate.of(2026, 3, 31),
            time = LocalTime.of(14, 5),
        )

        assertEquals("2026-03-31 14:05", formatDateTime(epochMillis, "yyyy-MM-dd"))
    }
}
