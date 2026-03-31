package com.johan.misgastos.ui.screens.expenseeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johan.misgastos.domain.model.Category
import com.johan.misgastos.domain.model.ExpenseDraft
import com.johan.misgastos.domain.model.PaymentMethod
import com.johan.misgastos.domain.repository.CategoryRepository
import com.johan.misgastos.domain.repository.ExpenseRepository
import com.johan.misgastos.navigation.AppDestination
import com.johan.misgastos.utils.formatAmountInputFromCents
import com.johan.misgastos.utils.parseAmountInputToCents
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExpenseEditorUiState(
    val isLoading: Boolean = true,
    val expenseId: Long? = null,
    val amountInput: String = "",
    val title: String = "",
    val description: String = "",
    val notes: String = "",
    val selectedCategoryId: Long? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val occurredAt: Long = System.currentTimeMillis(),
    val categories: List<Category> = emptyList(),
) {
    val isEditing: Boolean = expenseId != null
}

sealed interface ExpenseEditorEvent {
    data class Message(val value: String) : ExpenseEditorEvent
    data object Saved : ExpenseEditorEvent
    data object Deleted : ExpenseEditorEvent
}

@HiltViewModel
class ExpenseEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository,
) : ViewModel() {

    private val expenseIdArgument = savedStateHandle.get<Long>(AppDestination.ExpenseEditor.ARG_EXPENSE_ID)
        ?.takeIf { it > 0L }

    private val mutableState = MutableStateFlow(
        ExpenseEditorUiState(
            isLoading = expenseIdArgument != null,
            expenseId = expenseIdArgument,
        ),
    )

    val uiState: StateFlow<ExpenseEditorUiState> = mutableState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = mutableState.value,
        )

    private val _events = MutableSharedFlow<ExpenseEditorEvent>()
    val events = _events.asSharedFlow()

    private var initialExpenseLoaded = false

    init {
        viewModelScope.launch {
            categoryRepository.observeCategories(includeInactive = false).collect { categories ->
                mutableState.update { state ->
                    state.copy(
                        categories = categories,
                        selectedCategoryId = state.selectedCategoryId ?: categories.firstOrNull()?.id,
                    )
                }
            }
        }

        if (expenseIdArgument != null) {
            viewModelScope.launch {
                expenseRepository.observeExpense(expenseIdArgument).collect { expense ->
                    if (expense != null && !initialExpenseLoaded) {
                        initialExpenseLoaded = true
                        mutableState.value = ExpenseEditorUiState(
                            isLoading = false,
                            expenseId = expense.id,
                            amountInput = formatAmountInputFromCents(expense.amountInCents),
                            title = expense.title,
                            description = expense.description.orEmpty(),
                            notes = expense.notes.orEmpty(),
                            selectedCategoryId = expense.category.id,
                            paymentMethod = expense.paymentMethod,
                            occurredAt = expense.occurredAt,
                            categories = mutableState.value.categories,
                        )
                    }
                }
            }
        } else {
            mutableState.update { it.copy(isLoading = false) }
        }
    }

    fun updateAmount(value: String) {
        mutableState.update { it.copy(amountInput = value) }
    }

    fun updateTitle(value: String) {
        mutableState.update { it.copy(title = value) }
    }

    fun updateDescription(value: String) {
        mutableState.update { it.copy(description = value) }
    }

    fun updateNotes(value: String) {
        mutableState.update { it.copy(notes = value) }
    }

    fun updateCategory(categoryId: Long) {
        mutableState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun updatePaymentMethod(method: PaymentMethod) {
        mutableState.update { it.copy(paymentMethod = method) }
    }

    fun updateOccurredAt(occurredAt: Long) {
        mutableState.update { it.copy(occurredAt = occurredAt) }
    }

    fun saveExpense() {
        val state = mutableState.value
        val amountInCents = parseAmountInputToCents(state.amountInput)
        val categoryId = state.selectedCategoryId

        viewModelScope.launch {
            when {
                amountInCents == null -> _events.emit(ExpenseEditorEvent.Message("Ingresa un monto válido"))
                categoryId == null -> _events.emit(ExpenseEditorEvent.Message("Selecciona una categoría"))
                state.title.isBlank() -> _events.emit(ExpenseEditorEvent.Message("Agrega un nombre corto para el gasto"))
                else -> {
                    expenseRepository.saveExpense(
                        ExpenseDraft(
                            id = state.expenseId,
                            amountInCents = amountInCents,
                            title = state.title,
                            description = state.description,
                            categoryId = categoryId,
                            paymentMethod = state.paymentMethod,
                            occurredAt = state.occurredAt,
                            notes = state.notes,
                        ),
                    )
                    _events.emit(ExpenseEditorEvent.Saved)
                }
            }
        }
    }

    fun deleteExpense() {
        val expenseId = mutableState.value.expenseId ?: return
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId)
            _events.emit(ExpenseEditorEvent.Deleted)
        }
    }
}
