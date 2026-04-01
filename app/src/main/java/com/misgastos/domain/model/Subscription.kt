package com.misgastos.domain.model

data class Subscription(
    val id: Long,
    val name: String,
    val monthlyAmountInCents: Long,
    val billingDay: Int,
    val paymentMethod: PaymentMethod,
    val createdAt: Long,
    val updatedAt: Long,
)
