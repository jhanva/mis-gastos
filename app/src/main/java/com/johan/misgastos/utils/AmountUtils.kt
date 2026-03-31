package com.johan.misgastos.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun parseAmountInputToCents(input: String): Long? {
    val sanitized = input.trim()
        .replace(" ", "")
        .replace(".", "")
        .replace(",", ".")

    val decimalValue = sanitized.toBigDecimalOrNull() ?: return null
    if (decimalValue <= BigDecimal.ZERO) return null

    return decimalValue
        .multiply(BigDecimal(100))
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()
}

fun formatAmountInputFromCents(amountInCents: Long): String {
    val decimal = BigDecimal(amountInCents).divide(BigDecimal(100))
    return decimal.stripTrailingZeros().toPlainString()
}

fun formatCurrency(
    amountInCents: Long,
    currencyCode: String,
    locale: Locale = Locale("es", "CO"),
): String {
    val formatter = NumberFormat.getCurrencyInstance(locale)
    val currency = runCatching { Currency.getInstance(currencyCode) }.getOrDefault(Currency.getInstance("COP"))
    formatter.currency = currency
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 0

    val decimal = BigDecimal(amountInCents).divide(BigDecimal(100))
    return formatter.format(decimal)
}
