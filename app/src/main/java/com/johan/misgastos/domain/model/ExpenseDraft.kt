package com.johan.misgastos.domain.model

data class ExpenseDraft(
    val id: Long? = null,
    val amountInCents: Long,
    val title: String,
    val description: String?,
    val categoryId: Long,
    val paymentMethod: PaymentMethod,
    val occurredAt: Long,
    val notes: String?,
)
