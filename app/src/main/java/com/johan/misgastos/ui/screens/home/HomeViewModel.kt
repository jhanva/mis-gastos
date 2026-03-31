package com.johan.misgastos.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johan.misgastos.domain.model.HomeDashboard
import com.johan.misgastos.domain.repository.ExpenseRepository
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
                val metrics = calculateDashboardMetrics(
                    items = expenses,
                    dateSelector = { it.occurredAt },
                    amountSelector = { it.amountInCents },
                )

                HomeUiState(
                    dashboard = HomeDashboard(
                        todayTotalInCents = metrics.todayTotalInCents,
                        monthTotalInCents = metrics.monthTotalInCents,
                        recentExpenses = metrics.recentItems,
                    ),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState(),
            )
}
