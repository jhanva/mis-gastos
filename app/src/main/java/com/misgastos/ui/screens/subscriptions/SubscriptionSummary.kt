package com.misgastos.ui.screens.subscriptions

import com.misgastos.domain.model.PaymentMethod
import com.misgastos.domain.model.Subscription

data class SubscriptionSummary(
    val totalMonthlyInCents: Long = 0,
    val debitMonthlyInCents: Long = 0,
    val creditMonthlyInCents: Long = 0,
    val subscriptionCount: Int = 0,
)

internal fun buildSubscriptionSummary(
    subscriptions: List<Subscription>,
): SubscriptionSummary {
    return SubscriptionSummary(
        totalMonthlyInCents = subscriptions.sumOf(Subscription::monthlyAmountInCents),
        debitMonthlyInCents = subscriptions
            .filter { it.paymentMethod == PaymentMethod.DEBIT_CARD }
            .sumOf(Subscription::monthlyAmountInCents),
        creditMonthlyInCents = subscriptions
            .filter { it.paymentMethod == PaymentMethod.CREDIT_CARD }
            .sumOf(Subscription::monthlyAmountInCents),
        subscriptionCount = subscriptions.size,
    )
}
