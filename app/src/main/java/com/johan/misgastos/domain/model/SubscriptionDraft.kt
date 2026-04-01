package com.johan.misgastos.domain.model

data class SubscriptionDraft(
    val id: Long? = null,
    val name: String,
    val monthlyAmountInCents: Long,
    val billingDay: Int,
    val paymentMethod: PaymentMethod,
)
