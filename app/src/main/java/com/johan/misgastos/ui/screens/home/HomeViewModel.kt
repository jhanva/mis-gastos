package com.johan.misgastos.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johan.misgastos.domain.model.HomeDashboard
import com.johan.misgastos.domain.repository.ExpenseRepository
import com.johan.misgastos.utils.endOfDayMillis
import com.johan.misgastos.utils.monthRangeMillis
import com.johan.misgastos.utils.startOfDayMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val dashboard: HomeDashboard = HomeDashboard(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    expenseRepository: ExpenseRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        expenseRepository.observeExpenses()
            .map { expenses ->
                val today = java.time.LocalDate.now()
                val todayStart = startOfDayMillis(today)
                val todayEnd = endOfDayMillis(today)
                val monthRange = monthRangeMillis(today)

                HomeUiState(
                    dashboard = HomeDashboard(
                        todayTotalInCents = expenses
                            .filter { it.occurredAt in todayStart..todayEnd }
                            .sumOf { it.amountInCents },
                        monthTotalInCents = expenses
                            .filter { it.occurredAt in monthRange }
                            .sumOf { it.amountInCents },
                        recentExpenses = expenses.take(6),
                    ),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState(),
            )
}
