package com.johan.misgastos.domain.model

enum class AppThemeMode(val value: String, val label: String) {
    SYSTEM("system", "Seguir sistema"),
    LIGHT("light", "Claro"),
    DARK("dark", "Oscuro");

    companion object {
        fun fromValue(value: String): AppThemeMode {
            return entries.firstOrNull { it.value == value } ?: SYSTEM
        }
    }
}
