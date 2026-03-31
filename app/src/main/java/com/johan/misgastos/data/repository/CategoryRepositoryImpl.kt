package com.johan.misgastos.data.repository

import com.johan.misgastos.data.local.dao.CategoryDao
import com.johan.misgastos.data.local.dao.ExpenseDao
import com.johan.misgastos.data.local.entity.CategoryEntity
import com.johan.misgastos.domain.model.CategoryDraft
import com.johan.misgastos.domain.repository.CategoryDeletionResult
import com.johan.misgastos.domain.repository.CategoryRepository
import com.johan.misgastos.utils.DEFAULT_CATEGORY_PRESETS
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao,
) : CategoryRepository {

    override fun observeCategories(includeInactive: Boolean): Flow<List<com.johan.misgastos.domain.model.Category>> {
        val source = if (includeInactive) categoryDao.observeAll() else categoryDao.observeActive()
        return source.map { categories -> categories.map(CategoryEntity::toDomain) }
    }

    override suspend fun saveCategory(draft: CategoryDraft) {
        val now = System.currentTimeMillis()
        val existing = if (draft.id != null) {
            categoryDao.getById(draft.id)
        } else {
            null
        }

        categoryDao.upsert(
            CategoryEntity(
                id = draft.id ?: 0L,
                name = draft.name.trim(),
                iconName = draft.iconName,
                colorHex = draft.colorHex,
                isActive = draft.isActive,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now,
            ),
        )
    }

    override suspend fun deleteCategory(categoryId: Long): CategoryDeletionResult {
        val linkedExpenses = expenseDao.countByCategory(categoryId)
        if (linkedExpenses > 0) {
            return CategoryDeletionResult.InUse(linkedExpenses)
        }

        categoryDao.deleteById(categoryId)
        return CategoryDeletionResult.Deleted
    }

    override suspend fun ensureDefaultCategories() {
        if (categoryDao.count() > 0) return

        val now = System.currentTimeMillis()
        categoryDao.insertAll(
            DEFAULT_CATEGORY_PRESETS.mapIndexed { index, preset ->
                CategoryEntity(
                    name = preset.name,
                    iconName = preset.iconName,
                    colorHex = preset.colorHex,
                    isActive = true,
                    createdAt = now + index,
                    updatedAt = now + index,
                )
            },
        )
    }
}
