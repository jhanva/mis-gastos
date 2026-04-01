package com.johan.misgastos.di

import android.content.Context
import androidx.room.Room
import com.johan.misgastos.data.local.MisGastosDatabase
import com.johan.misgastos.data.local.dao.CategoryDao
import com.johan.misgastos.data.local.dao.ExpenseDao
import com.johan.misgastos.data.preferences.UserPreferencesRepositoryImpl
import com.johan.misgastos.data.repository.CategoryRepositoryImpl
import com.johan.misgastos.data.repository.ExpenseRepositoryImpl
import com.johan.misgastos.domain.repository.CategoryRepository
import com.johan.misgastos.domain.repository.ExpenseRepository
import com.johan.misgastos.domain.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): MisGastosDatabase {
        return Room.databaseBuilder(
            context,
            MisGastosDatabase::class.java,
            "mis_gastos.db",
        )
            // Keep user expense history intact; future schema changes must ship explicit migrations.
            .build()
    }

    @Provides
    fun provideCategoryDao(database: MisGastosDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideExpenseDao(database: MisGastosDatabase): ExpenseDao = database.expenseDao()

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        expenseDao: ExpenseDao,
    ): CategoryRepository = CategoryRepositoryImpl(categoryDao, expenseDao)

    @Provides
    @Singleton
    fun provideExpenseRepository(
        expenseDao: ExpenseDao,
    ): ExpenseRepository = ExpenseRepositoryImpl(expenseDao)

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context,
    ): UserPreferencesRepository = UserPreferencesRepositoryImpl(context)
}
