package com.johan.misgastos.utils

import androidx.compose.ui.graphics.Color

data class CategoryIconOption(
    val key: String,
    val label: String,
    val symbol: String,
)

val categoryIconOptions = listOf(
    CategoryIconOption("food", "Alimentacion", "🍽"),
    CategoryIconOption("transport", "Transporte", "🚌"),
    CategoryIconOption("home", "Hogar", "🏠"),
    CategoryIconOption("health", "Salud", "❤️"),
    CategoryIconOption("shopping", "Compras", "🛍"),
    CategoryIconOption("services", "Servicios", "💡"),
    CategoryIconOption("fun", "Ocio", "🎉"),
    CategoryIconOption("other", "Otro", "🧩"),
)

val categoryColorOptions = listOf(
    Color(0xFF2E7D32),
    Color(0xFF1565C0),
    Color(0xFF6D4C41),
    Color(0xFFC62828),
    Color(0xFF8E24AA),
    Color(0xFF00838F),
    Color(0xFFEF6C00),
    Color(0xFF455A64),
)

fun symbolForCategory(iconName: String): String {
    return categoryIconOptions.firstOrNull { it.key == iconName }?.symbol ?: "🧩"
}

fun colorFromHex(colorHex: String): Color {
    return runCatching { Color(android.graphics.Color.parseColor(colorHex)) }.getOrDefault(Color(0xFF455A64))
}
