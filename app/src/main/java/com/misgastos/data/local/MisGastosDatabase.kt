package com.misgastos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.misgastos.data.local.dao.CategoryDao
import com.misgastos.data.local.dao.ExpenseDao
import com.misgastos.data.local.dao.SubscriptionDao
import com.misgastos.data.local.entity.CategoryEntity
import com.misgastos.data.local.entity.ExpenseEntity
import com.misgastos.data.local.entity.SubscriptionEntity

@Database(
    entities = [CategoryEntity::class, ExpenseEntity::class, SubscriptionEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class MisGastosDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun subscriptionDao(): SubscriptionDao
}
