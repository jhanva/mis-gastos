package com.misgastos.domain.model

enum class ExpenseSortOption(val label: String) {
    NEWEST("Más recientes"),
    OLDEST("Más antiguos"),
    AMOUNT_DESC("Monto mayor"),
    AMOUNT_ASC("Monto menor"),
}

data class ExpenseFilters(
    val searchQuery: String = "",
    val categoryId: Long? = null,
    val startDateMillis: Long? = null,
    val endDateMillis: Long? = null,
    val sortOption: ExpenseSortOption = ExpenseSortOption.NEWEST,
)
