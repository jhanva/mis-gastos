package com.johan.misgastos.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.johan.misgastos.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY billingDay ASC, name ASC")
    fun observeAll(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE id = :subscriptionId LIMIT 1")
    suspend fun getById(subscriptionId: Long): SubscriptionEntity?

    @Upsert
    suspend fun upsert(subscription: SubscriptionEntity): Long

    @Query("DELETE FROM subscriptions WHERE id = :subscriptionId")
    suspend fun deleteById(subscriptionId: Long)
}
