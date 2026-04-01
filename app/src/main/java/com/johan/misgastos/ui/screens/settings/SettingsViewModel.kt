package com.johan.misgastos.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johan.misgastos.domain.model.AppThemeMode
import com.johan.misgastos.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface SettingsEvent {
    data class Message(val value: String) : SettingsEvent
}

internal fun isValidCurrencyCode(value: String): Boolean {
    return value.matches(Regex("^[A-Z]{3}$"))
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events = _events.asSharedFlow()

    fun updateThemeMode(themeMode: AppThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeMode(themeMode)
            _events.emit(SettingsEvent.Message("Tema actualizado"))
        }
    }

    fun updateCurrencyCode(currencyCode: String) {
        viewModelScope.launch {
            val sanitized = currencyCode.trim().uppercase()
            if (!isValidCurrencyCode(sanitized)) {
                _events.emit(SettingsEvent.Message("Usa un codigo ISO de 3 letras, por ejemplo COP"))
            } else {
                userPreferencesRepository.setCurrencyCode(sanitized)
                _events.emit(SettingsEvent.Message("Moneda actualizada"))
            }
        }
    }

    fun updateDatePattern(pattern: String) {
        viewModelScope.launch {
            userPreferencesRepository.setDatePattern(pattern)
            _events.emit(SettingsEvent.Message("Formato de fecha actualizado"))
        }
    }
}
