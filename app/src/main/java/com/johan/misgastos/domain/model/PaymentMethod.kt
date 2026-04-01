package com.johan.misgastos.domain.model

enum class PaymentMethod(val value: String, val label: String) {
    CASH("cash", "Efectivo"),
    DEBIT_CARD("debit_card", "Tarjeta debito"),
    CREDIT_CARD("credit_card", "Tarjeta credito"),
    TRANSFER("transfer", "Transferencia"),
    OTHER("other", "Otro"),
    CARD("card", "Tarjeta");

    companion object {
        val selectableEntries: List<PaymentMethod> = listOf(
            CASH,
            DEBIT_CARD,
            CREDIT_CARD,
            TRANSFER,
            OTHER,
        )

        fun fromValue(value: String): PaymentMethod {
            return entries.firstOrNull { it.value == value } ?: CASH
        }
    }
}
