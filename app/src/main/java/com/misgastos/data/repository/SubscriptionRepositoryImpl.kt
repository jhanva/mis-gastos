package com.misgastos.data.repository

import com.misgastos.data.local.dao.SubscriptionDao
import com.misgastos.data.local.entity.SubscriptionEntity
import com.misgastos.domain.model.Subscription
import com.misgastos.domain.model.SubscriptionDraft
import com.misgastos.domain.repository.SubscriptionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionDao: SubscriptionDao,
) : SubscriptionRepository {

    override fun observeSubscriptions(): Flow<List<Subscription>> {
        return subscriptionDao.observeAll().map { subscriptions ->
            subscriptions.map(SubscriptionEntity::toDomain)
        }
    }

    override suspend fun saveSubscription(draft: SubscriptionDraft) {
        val now = System.currentTimeMillis()
        val existing = if (draft.id != null) {
            subscriptionDao.getById(draft.id)
        } else {
            null
        }

        subscriptionDao.upsert(
            SubscriptionEntity(
                id = draft.id ?: 0L,
                name = draft.name.trim(),
                monthlyAmountInCents = draft.monthlyAmountInCents,
                billingDay = draft.billingDay,
                paymentMethod = draft.paymentMethod.value,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now,
            ),
        )
    }

    override suspend fun deleteSubscription(subscriptionId: Long) {
        subscriptionDao.deleteById(subscriptionId)
    }
}
