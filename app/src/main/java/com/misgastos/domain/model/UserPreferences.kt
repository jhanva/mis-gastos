package com.misgastos.domain.model

data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val currencyCode: String = "COP",
    val datePattern: String = "dd/MM/yyyy",
)
