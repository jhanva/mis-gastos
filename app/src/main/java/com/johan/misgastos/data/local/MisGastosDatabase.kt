package com.johan.misgastos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.johan.misgastos.data.local.dao.CategoryDao
import com.johan.misgastos.data.local.dao.ExpenseDao
import com.johan.misgastos.data.local.entity.CategoryEntity
import com.johan.misgastos.data.local.entity.ExpenseEntity

@Database(
    entities = [CategoryEntity::class, ExpenseEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class MisGastosDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
}
