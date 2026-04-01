package com.misgastos.ui.screens.subscriptions

import com.misgastos.domain.model.PaymentMethod
import com.misgastos.domain.model.Subscription
import org.junit.Assert.assertEquals
import org.junit.Test

class SubscriptionSummaryTest {

    @Test
    fun `buildSubscriptionSummary aggregates total debit and credit amounts`() {
        val summary = buildSubscriptionSummary(
            listOf(
                subscription(
                    id = 1L,
                    amountInCents = 3900L,
                    paymentMethod = PaymentMethod.CREDIT_CARD,
                ),
                subscription(
                    id = 2L,
                    amountInCents = 1800L,
                    paymentMethod = PaymentMethod.DEBIT_CARD,
                ),
                subscription(
                    id = 3L,
                    amountInCents = 2400L,
                    paymentMethod = PaymentMethod.CARD,
                ),
            ),
        )

        assertEquals(8100L, summary.totalMonthlyInCents)
        assertEquals(1800L, summary.debitMonthlyInCents)
        assertEquals(3900L, summary.creditMonthlyInCents)
        assertEquals(3, summary.subscriptionCount)
    }

    @Test
    fun `buildSubscriptionSummary handles empty input`() {
        val summary = buildSubscriptionSummary(emptyList())

        assertEquals(0L, summary.totalMonthlyInCents)
        assertEquals(0L, summary.debitMonthlyInCents)
        assertEquals(0L, summary.creditMonthlyInCents)
        assertEquals(0, summary.subscriptionCount)
    }

    private fun subscription(
        id: Long,
        amountInCents: Long,
        paymentMethod: PaymentMethod,
    ): Subscription {
        return Subscription(
            id = id,
            name = "Servicio $id",
            monthlyAmountInCents = amountInCents,
            billingDay = 12,
            paymentMethod = paymentMethod,
            createdAt = 0L,
            updatedAt = 0L,
        )
    }
}
