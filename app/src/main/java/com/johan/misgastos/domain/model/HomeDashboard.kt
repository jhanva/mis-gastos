package com.johan.misgastos.domain.model

data class HomeDashboard(
    val todayTotalInCents: Long = 0L,
    val monthTotalInCents: Long = 0L,
    val recentExpenses: List<Expense> = emptyList(),
)
