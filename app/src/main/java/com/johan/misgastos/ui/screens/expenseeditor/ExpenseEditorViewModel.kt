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
    val hasUnsavedChanges: Boolean = false,
) {
    val isEditing: Boolean = expenseId != null
}

private data class ExpenseEditorSnapshot(
    val amountInput: String,
    val title: String,
    val description: String,
    val notes: String,
    val selectedCategoryId: Long?,
    val paymentMethod: PaymentMethod,
    val occurredAt: Long,
)

private fun ExpenseEditorUiState.toSnapshot(): ExpenseEditorSnapshot {
    return ExpenseEditorSnapshot(
        amountInput = amountInput,
        title = title,
        description = description,
        notes = notes,
        selectedCategoryId = selectedCategoryId,
        paymentMethod = paymentMethod,
        occurredAt = occurredAt,
    )
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
            isLoading = true,
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
    private var initialSnapshot: ExpenseEditorSnapshot? = null

    init {
        viewModelScope.launch {
            categoryRepository.observeCategories(includeInactive = false).collect { categories ->
                var snapshotToInitialize: ExpenseEditorSnapshot? = null
                mutableState.update { state ->
                    val updatedState = state.copy(
                        categories = categories,
                        selectedCategoryId = when {
                            state.selectedCategoryId != null -> state.selectedCategoryId
                            expenseIdArgument == null -> categories.firstOrNull()?.id
                            else -> null
                        },
                    )
                    if (expenseIdArgument == null && initialSnapshot == null) {
                        val initializedState = updatedState.copy(isLoading = false, hasUnsavedChanges = false)
                        snapshotToInitialize = initializedState.toSnapshot()
                        initializedState
                    } else {
                        recalculateDirtyState(updatedState)
                    }
                }

                snapshotToInitialize?.let { initialSnapshot = it }
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
                            hasUnsavedChanges = false,
                        )
                        initialSnapshot = mutableState.value.toSnapshot()
                    }
                }
            }
        }
    }

    fun updateAmount(value: String) {
        mutableState.update { recalculateDirtyState(it.copy(amountInput = value)) }
    }

    fun updateTitle(value: String) {
        mutableState.update { recalculateDirtyState(it.copy(title = value)) }
    }

    fun updateDescription(value: String) {
        mutableState.update { recalculateDirtyState(it.copy(description = value)) }
    }

    fun updateNotes(value: String) {
        mutableState.update { recalculateDirtyState(it.copy(notes = value)) }
    }

    fun updateCategory(categoryId: Long) {
        mutableState.update { recalculateDirtyState(it.copy(selectedCategoryId = categoryId)) }
    }

    fun updatePaymentMethod(method: PaymentMethod) {
        mutableState.update { recalculateDirtyState(it.copy(paymentMethod = method)) }
    }

    fun updateOccurredAt(occurredAt: Long) {
        mutableState.update { recalculateDirtyState(it.copy(occurredAt = occurredAt)) }
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
                    val currentSnapshot = state.toSnapshot()
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
                    initialSnapshot = currentSnapshot
                    mutableState.update { it.copy(hasUnsavedChanges = false) }
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

    private fun recalculateDirtyState(state: ExpenseEditorUiState): ExpenseEditorUiState {
        val dirty = initialSnapshot?.let { snapshot ->
            state.toSnapshot() != snapshot
        } ?: false

        return state.copy(hasUnsavedChanges = dirty)
    }
}
