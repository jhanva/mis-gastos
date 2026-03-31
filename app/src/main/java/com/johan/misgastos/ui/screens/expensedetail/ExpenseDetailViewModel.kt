package com.johan.misgastos.ui.screens.expensedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johan.misgastos.domain.model.Expense
import com.johan.misgastos.domain.repository.ExpenseRepository
import com.johan.misgastos.navigation.AppDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ExpenseDetailUiState(
    val isLoading: Boolean = true,
    val expense: Expense? = null,
) {
    val isMissing: Boolean = !isLoading && expense == null
}

sealed interface ExpenseDetailEvent {
    data object Deleted : ExpenseDetailEvent
}

@HiltViewModel
class ExpenseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val expenseRepository: ExpenseRepository,
) : ViewModel() {

    private val expenseId = checkNotNull(savedStateHandle.get<Long>(AppDestination.ExpenseDetail.ARG_EXPENSE_ID))

    val uiState: StateFlow<ExpenseDetailUiState> =
        expenseRepository.observeExpense(expenseId)
            .map { expense ->
                ExpenseDetailUiState(
                    isLoading = false,
                    expense = expense,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ExpenseDetailUiState(),
            )

    private val _events = MutableSharedFlow<ExpenseDetailEvent>()
    val events = _events.asSharedFlow()

    fun deleteExpense() {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId)
            _events.emit(ExpenseDetailEvent.Deleted)
        }
    }
}
