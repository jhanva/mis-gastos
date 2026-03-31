package com.johan.misgastos.domain.repository

import com.johan.misgastos.domain.model.Expense
import com.johan.misgastos.domain.model.ExpenseDraft
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeExpenses(): Flow<List<Expense>>
    fun observeExpense(expenseId: Long): Flow<Expense?>
    suspend fun saveExpense(draft: ExpenseDraft)
    suspend fun deleteExpense(expenseId: Long)
}
