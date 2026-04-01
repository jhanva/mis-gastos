package com.misgastos.domain.model

data class Expense(
    val id: Long,
    val amountInCents: Long,
    val title: String,
    val description: String?,
    val category: Category,
    val paymentMethod: PaymentMethod,
    val occurredAt: Long,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
