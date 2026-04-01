package com.misgastos.utils

data class CategoryPreset(
    val name: String,
    val iconName: String,
    val colorHex: String,
)

val DEFAULT_CATEGORY_PRESETS = listOf(
    CategoryPreset("Alimentación", "food", "#2E7D32"),
    CategoryPreset("Transporte", "transport", "#1565C0"),
    CategoryPreset("Hogar", "home", "#6D4C41"),
    CategoryPreset("Salud", "health", "#C62828"),
    CategoryPreset("Compras", "shopping", "#8E24AA"),
    CategoryPreset("Servicios", "services", "#00838F"),
    CategoryPreset("Ocio", "fun", "#EF6C00"),
)
