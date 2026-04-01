package com.johan.misgastos.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

private data class CurrencyFormatKey(
    val currencyCode: String,
    val locale: Locale,
)

private val currencyCache = ConcurrentHashMap<String, Currency>()
private val currencyFormatterPrototypeCache = ConcurrentHashMap<CurrencyFormatKey, NumberFormat>()
private val fallbackCurrency: Currency = Currency.getInstance("COP")

fun parseAmountInputToCents(input: String): Long? {
    val compact = input.trim().replace(" ", "")
    if (compact.isBlank()) return null
    if (!compact.all { it.isDigit() || it == '.' || it == ',' }) return null

    val lastDot = compact.lastIndexOf('.')
    val lastComma = compact.lastIndexOf(',')
    val decimalIndex = maxOf(lastDot, lastComma)
    val digitsAfterLastSeparator = if (decimalIndex >= 0) compact.length - decimalIndex - 1 else 0
    val hasSingleSeparatorType = (lastDot >= 0) xor (lastComma >= 0)

    val sanitized = when {
        decimalIndex < 0 -> compact
        hasSingleSeparatorType && digitsAfterLastSeparator == 3 ->
            compact.replace(".", "").replace(",", "")
        else -> {
            val integerPart = compact.substring(0, decimalIndex)
                .replace(".", "")
                .replace(",", "")
            val decimalPart = compact.substring(decimalIndex + 1)
                .replace(".", "")
                .replace(",", "")
            buildString {
                append(if (integerPart.isBlank()) "0" else integerPart)
                if (decimalPart.isNotEmpty()) {
                    append('.')
                    append(decimalPart)
                }
            }
        }
    }

    val decimalValue = sanitized.toBigDecimalOrNull() ?: return null
    if (decimalValue <= BigDecimal.ZERO) return null

    return decimalValue
        .multiply(BigDecimal(100))
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()
}

fun formatAmountInputFromCents(amountInCents: Long): String {
    val decimal = BigDecimal(amountInCents).divide(BigDecimal(100))
    return decimal.stripTrailingZeros().toPlainString().replace('.', ',')
}

fun formatCurrency(
    amountInCents: Long,
    currencyCode: String,
    locale: Locale = Locale("es", "CO"),
): String {
    val normalizedCurrencyCode = currencyCode.uppercase()
    val formatter = currencyFormatterPrototypeCache.getOrPut(
        CurrencyFormatKey(
            currencyCode = normalizedCurrencyCode,
            locale = locale,
        ),
    ) {
        NumberFormat.getCurrencyInstance(locale).apply {
            currency = currencyCache.getOrPut(normalizedCurrencyCode) {
                runCatching { Currency.getInstance(normalizedCurrencyCode) }.getOrDefault(fallbackCurrency)
            }
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }
    }.clone() as NumberFormat

    val decimal = BigDecimal(amountInCents).divide(BigDecimal(100))
    return formatter.format(decimal)
}
