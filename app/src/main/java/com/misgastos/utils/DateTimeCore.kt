package com.misgastos.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

private val appZoneId: ZoneId
    get() = ZoneId.systemDefault()

private val dateFormatterCache = ConcurrentHashMap<String, DateTimeFormatter>()
private val dateTimeFormatterCache = ConcurrentHashMap<String, DateTimeFormatter>()
private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun epochMillisToLocalDateTime(epochMillis: Long): LocalDateTime {
    return Instant.ofEpochMilli(epochMillis).atZone(appZoneId).toLocalDateTime()
}

fun epochMillisToLocalDate(epochMillis: Long): LocalDate {
    return epochMillisToLocalDateTime(epochMillis).toLocalDate()
}

fun epochMillisToLocalTime(epochMillis: Long): LocalTime {
    return epochMillisToLocalDateTime(epochMillis).toLocalTime()
}

fun toEpochMillis(date: LocalDate, time: LocalTime): Long {
    return date.atTime(time).atZone(appZoneId).toInstant().toEpochMilli()
}

fun startOfDayMillis(date: LocalDate): Long {
    return date.atStartOfDay(appZoneId).toInstant().toEpochMilli()
}

fun endOfDayMillis(date: LocalDate): Long {
    return date.plusDays(1).atStartOfDay(appZoneId).toInstant().toEpochMilli() - 1
}

fun monthRangeMillis(reference: LocalDate = LocalDate.now()): LongRange {
    val month = YearMonth.from(reference)
    return startOfDayMillis(month.atDay(1))..endOfDayMillis(month.atEndOfMonth())
}

fun formatDate(epochMillis: Long, pattern: String): String {
    val formatter = dateFormatterCache.getOrPut(pattern) {
        DateTimeFormatter.ofPattern(pattern)
    }
    return epochMillisToLocalDate(epochMillis).format(formatter)
}

fun formatTime(epochMillis: Long): String {
    return epochMillisToLocalTime(epochMillis).format(timeFormatter)
}

fun formatDateTime(epochMillis: Long, pattern: String): String {
    val formatter = dateTimeFormatterCache.getOrPut(pattern) {
        DateTimeFormatter.ofPattern("$pattern HH:mm")
    }
    return epochMillisToLocalDateTime(epochMillis).format(formatter)
}
