package com.johan.misgastos.domain.model

enum class PaymentMethod(val value: String, val label: String) {
    CASH("cash", "Efectivo"),
    CARD("card", "Tarjeta"),
    TRANSFER("transfer", "Transferencia"),
    OTHER("other", "Otro");

    companion object {
        fun fromValue(value: String): PaymentMethod {
            return entries.firstOrNull { it.value == value } ?: CASH
        }
    }
}
