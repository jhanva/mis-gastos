package com.johan.misgastos.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.johan.misgastos.data.local.MisGastosDatabase
import com.johan.misgastos.data.local.dao.CategoryDao
import com.johan.misgastos.data.local.dao.ExpenseDao
import com.johan.misgastos.data.local.dao.SubscriptionDao
import com.johan.misgastos.data.preferences.UserPreferencesRepositoryImpl
import com.johan.misgastos.data.repository.CategoryRepositoryImpl
import com.johan.misgastos.data.repository.ExpenseRepositoryImpl
import com.johan.misgastos.data.repository.SubscriptionRepositoryImpl
import com.johan.misgastos.domain.repository.CategoryRepository
import com.johan.misgastos.domain.repository.ExpenseRepository
import com.johan.misgastos.domain.repository.SubscriptionRepository
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

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `subscriptions` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `monthlyAmountInCents` INTEGER NOT NULL,
                    `billingDay` INTEGER NOT NULL,
                    `paymentMethod` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_subscriptions_billingDay` ON `subscriptions` (`billingDay`)",
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_subscriptions_name` ON `subscriptions` (`name`)",
            )
        }
    }

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
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideCategoryDao(database: MisGastosDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideExpenseDao(database: MisGastosDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideSubscriptionDao(database: MisGastosDatabase): SubscriptionDao = database.subscriptionDao()

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
    fun provideSubscriptionRepository(
        subscriptionDao: SubscriptionDao,
    ): SubscriptionRepository = SubscriptionRepositoryImpl(subscriptionDao)

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context,
    ): UserPreferencesRepository = UserPreferencesRepositoryImpl(context)
}
