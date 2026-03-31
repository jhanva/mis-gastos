package com.johan.misgastos.domain.repository

import com.johan.misgastos.domain.model.AppThemeMode
import com.johan.misgastos.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>
    suspend fun setThemeMode(themeMode: AppThemeMode)
    suspend fun setCurrencyCode(currencyCode: String)
    suspend fun setDatePattern(pattern: String)
}
