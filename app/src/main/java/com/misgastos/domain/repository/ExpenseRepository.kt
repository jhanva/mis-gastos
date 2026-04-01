package com.misgastos.domain.repository

import com.misgastos.domain.model.Expense
import com.misgastos.domain.model.ExpenseDraft
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeExpenses(): Flow<List<Expense>>
    fun observeExpense(expenseId: Long): Flow<Expense?>
    suspend fun saveExpense(draft: ExpenseDraft)
    suspend fun deleteExpense(expenseId: Long)
}
