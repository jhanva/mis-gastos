package com.johan.misgastos.domain.repository

import com.johan.misgastos.domain.model.Category
import com.johan.misgastos.domain.model.CategoryDraft
import kotlinx.coroutines.flow.Flow

sealed interface CategoryDeletionResult {
    data object Deleted : CategoryDeletionResult
    data class InUse(val linkedExpenses: Int) : CategoryDeletionResult
}

interface CategoryRepository {
    fun observeCategories(includeInactive: Boolean = true): Flow<List<Category>>
    suspend fun saveCategory(draft: CategoryDraft)
    suspend fun deleteCategory(categoryId: Long): CategoryDeletionResult
    suspend fun ensureDefaultCategories()
}
