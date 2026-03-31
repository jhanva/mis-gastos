package com.johan.misgastos.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johan.misgastos.domain.model.Category
import com.johan.misgastos.domain.model.CategoryDraft
import com.johan.misgastos.domain.repository.CategoryDeletionResult
import com.johan.misgastos.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
)

sealed interface CategoriesEvent {
    data class Message(val value: String) : CategoriesEvent
}

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val uiState: StateFlow<CategoriesUiState> =
        categoryRepository.observeCategories()
            .map(::CategoriesUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CategoriesUiState(),
            )

    private val _events = MutableSharedFlow<CategoriesEvent>()
    val events = _events.asSharedFlow()

    fun saveCategory(draft: CategoryDraft) {
        viewModelScope.launch {
            if (draft.name.isBlank()) {
                _events.emit(CategoriesEvent.Message("La categoría necesita un nombre"))
                return@launch
            }

            categoryRepository.saveCategory(draft)
            _events.emit(CategoriesEvent.Message("Categoría guardada"))
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            when (val result = categoryRepository.deleteCategory(categoryId)) {
                CategoryDeletionResult.Deleted -> _events.emit(CategoriesEvent.Message("Categoría eliminada"))
                is CategoryDeletionResult.InUse -> _events.emit(
                    CategoriesEvent.Message(
                        "No se puede eliminar: ${result.linkedExpenses} gasto(s) la usan",
                    ),
                )
            }
        }
    }
}
