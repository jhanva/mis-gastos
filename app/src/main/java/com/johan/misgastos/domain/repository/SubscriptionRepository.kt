package com.johan.misgastos.domain.repository

import com.johan.misgastos.domain.model.Subscription
import com.johan.misgastos.domain.model.SubscriptionDraft
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun observeSubscriptions(): Flow<List<Subscription>>
    suspend fun saveSubscription(draft: SubscriptionDraft)
    suspend fun deleteSubscription(subscriptionId: Long)
}
