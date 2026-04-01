package com.misgastos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misgastos.domain.model.UserPreferences
import com.misgastos.domain.repository.CategoryRepository
import com.misgastos.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppUiState(
    val preferences: UserPreferences = UserPreferences(),
)

@HiltViewModel
class AppViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val uiState: StateFlow<AppUiState> =
        userPreferencesRepository.preferences
            .map(::AppUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppUiState(),
            )

    init {
        viewModelScope.launch {
            categoryRepository.ensureDefaultCategories()
        }
    }
}
