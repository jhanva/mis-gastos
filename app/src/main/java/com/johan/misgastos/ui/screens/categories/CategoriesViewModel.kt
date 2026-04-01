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

internal data class CategoryAvailabilityItem(
    val id: Long,
    val isActive: Boolean,
)

internal fun wouldLeaveNoActiveCategories(
    categories: List<CategoryAvailabilityItem>,
    categoryId: Long?,
    targetIsActive: Boolean,
): Boolean {
    val activeCount = categories.count(CategoryAvailabilityItem::isActive)
    val existing = categoryId?.let { editedCategoryId -> categories.firstOrNull { it.id == editedCategoryId } }
    val wasActive = existing?.isActive == true

    return !targetIsActive && when {
        wasActive -> activeCount <= 1
        else -> activeCount == 0
    }
}

internal fun deletingCategoryWouldLeaveNoActive(
    categories: List<CategoryAvailabilityItem>,
    categoryId: Long,
): Boolean {
    val activeCount = categories.count(CategoryAvailabilityItem::isActive)
    val category = categories.firstOrNull { it.id == categoryId } ?: return false
    return category.isActive && activeCount <= 1
}

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
                _events.emit(CategoriesEvent.Message("La categoria necesita un nombre"))
                return@launch
            }
            if (wouldLeaveNoActiveCategories(uiState.value.categories.toAvailabilityItems(), draft.id, draft.isActive)) {
                _events.emit(CategoriesEvent.Message("Debe quedar al menos una categoria activa"))
                return@launch
            }

            categoryRepository.saveCategory(draft)
            _events.emit(CategoriesEvent.Message("Categoria guardada"))
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            if (deletingCategoryWouldLeaveNoActive(uiState.value.categories.toAvailabilityItems(), categoryId)) {
                _events.emit(CategoriesEvent.Message("No puedes eliminar la ultima categoria activa"))
                return@launch
            }

            when (val result = categoryRepository.deleteCategory(categoryId)) {
                CategoryDeletionResult.Deleted -> _events.emit(CategoriesEvent.Message("Categoria eliminada"))
                is CategoryDeletionResult.InUse -> _events.emit(
                    CategoriesEvent.Message(
                        "No se puede eliminar: ${result.linkedExpenses} gasto(s) la usan",
                    ),
                )
            }
        }
    }
}

private fun List<Category>.toAvailabilityItems(): List<CategoryAvailabilityItem> {
    return map { category ->
        CategoryAvailabilityItem(
            id = category.id,
            isActive = category.isActive,
        )
    }
}
