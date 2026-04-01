package com.misgastos.domain.repository

import com.misgastos.domain.model.AppThemeMode
import com.misgastos.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>
    suspend fun setThemeMode(themeMode: AppThemeMode)
    suspend fun setDatePattern(pattern: String)
}
