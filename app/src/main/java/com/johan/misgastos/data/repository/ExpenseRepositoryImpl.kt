package com.johan.misgastos.data.repository

import com.johan.misgastos.data.local.dao.ExpenseDao
import com.johan.misgastos.data.local.entity.ExpenseEntity
import com.johan.misgastos.domain.model.Expense
import com.johan.misgastos.domain.model.ExpenseDraft
import com.johan.misgastos.domain.repository.ExpenseRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
) : ExpenseRepository {

    override fun observeExpenses(): Flow<List<Expense>> {
        return expenseDao.observeAllWithCategory().map { expenses ->
            expenses.map { it.toDomain() }
        }
    }

    override fun observeExpense(expenseId: Long): Flow<Expense?> {
        return expenseDao.observeByIdWithCategory(expenseId).map { expense ->
            expense?.toDomain()
        }
    }

    override suspend fun saveExpense(draft: ExpenseDraft) {
        val now = System.currentTimeMillis()
        val existing = if (draft.id != null) {
            expenseDao.getById(draft.id)
        } else {
            null
        }

        expenseDao.upsert(
            ExpenseEntity(
                id = draft.id ?: 0L,
                amountInCents = draft.amountInCents,
                title = draft.title.trim(),
                description = draft.description?.trim().takeUnless { it.isNullOrBlank() },
                categoryId = draft.categoryId,
                paymentMethod = draft.paymentMethod.value,
                occurredAt = draft.occurredAt,
                notes = draft.notes?.trim().takeUnless { it.isNullOrBlank() },
                createdAt = existing?.createdAt ?: now,
                updatedAt = now,
            ),
        )
    }

    override suspend fun deleteExpense(expenseId: Long) {
        expenseDao.deleteById(expenseId)
    }
}
