package com.johan.misgastos.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.johan.misgastos.domain.model.AppThemeMode
import com.johan.misgastos.domain.model.UserPreferences
import com.johan.misgastos.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore by preferencesDataStore(name = "mis_gastos_preferences")

class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : UserPreferencesRepository {

    override val preferences: Flow<UserPreferences> =
        context.userPreferencesDataStore.data.map(::toUserPreferences)

    override suspend fun setThemeMode(themeMode: AppThemeMode) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_MODE] = themeMode.value
        }
    }

    override suspend fun setDatePattern(pattern: String) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.DATE_PATTERN] = pattern
        }
    }

    private fun toUserPreferences(preferences: Preferences): UserPreferences {
        return UserPreferences(
            themeMode = AppThemeMode.fromValue(
                preferences[PreferencesKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.value,
            ),
            currencyCode = "COP",
            datePattern = preferences[PreferencesKeys.DATE_PATTERN] ?: "dd/MM/yyyy",
        )
    }

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DATE_PATTERN = stringPreferencesKey("date_pattern")
    }
}
