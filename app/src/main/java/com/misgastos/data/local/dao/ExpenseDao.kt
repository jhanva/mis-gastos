package com.misgastos.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.misgastos.data.local.entity.ExpenseEntity
import com.misgastos.data.local.entity.ExpenseWithCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Transaction
    @Query("SELECT * FROM expenses ORDER BY occurredAt DESC")
    fun observeAllWithCategory(): Flow<List<ExpenseWithCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM expenses WHERE id = :expenseId LIMIT 1")
    fun observeByIdWithCategory(expenseId: Long): Flow<ExpenseWithCategoryEntity?>

    @Query("SELECT * FROM expenses WHERE id = :expenseId LIMIT 1")
    suspend fun getById(expenseId: Long): ExpenseEntity?

    @Query("SELECT COUNT(*) FROM expenses WHERE categoryId = :categoryId")
    suspend fun countByCategory(categoryId: Long): Int

    @Upsert
    suspend fun upsert(expense: ExpenseEntity): Long

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteById(expenseId: Long)
}
