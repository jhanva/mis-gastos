package com.johan.misgastos.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johan.misgastos.domain.model.Category
import com.johan.misgastos.domain.model.Expense
import com.johan.misgastos.domain.model.ExpenseFilters
import com.johan.misgastos.domain.model.ExpenseSortOption
import com.johan.misgastos.domain.repository.CategoryRepository
import com.johan.misgastos.domain.repository.ExpenseRepository
import com.johan.misgastos.utils.endOfDayMillis
import com.johan.misgastos.utils.epochMillisToLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class ExpenseGroupingMode(val label: String) {
    FLAT("Lista"),
    DAY("Por dia"),
}

data class ExpensesUiState(
    val expenses: List<Expense> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filters: ExpenseFilters = ExpenseFilters(),
    val groupingMode: ExpenseGroupingMode = ExpenseGroupingMode.FLAT,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class ExpensesViewModel @Inject constructor(
    expenseRepository: ExpenseRepository,
    categoryRepository: CategoryRepository,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val otherFilters = MutableStateFlow(ExpenseFilters())
    private val groupingMode = MutableStateFlow(ExpenseGroupingMode.FLAT)

    private val visibleFilters =
        combine(searchQuery, otherFilters) { currentSearchQuery, currentOtherFilters ->
            currentOtherFilters.copy(searchQuery = currentSearchQuery)
        }

    private val appliedFilters =
        combine(
            searchQuery.debounce(250).distinctUntilChanged(),
            otherFilters,
        ) { debouncedSearchQuery, currentOtherFilters ->
            currentOtherFilters.copy(searchQuery = debouncedSearchQuery)
        }.distinctUntilChanged()

    private val filteredExpenses =
        combine(
            expenseRepository.observeExpenses(),
            appliedFilters,
        ) { expenses, currentFilters ->
            applyFilters(expenses, currentFilters)
        }

    val uiState: StateFlow<ExpensesUiState> =
        combine(
            filteredExpenses,
            categoryRepository.observeCategories(includeInactive = true),
            visibleFilters,
            groupingMode,
        ) { expenses, categories, currentFilters, currentGroupingMode ->
            ExpensesUiState(
                expenses = expenses,
                categories = categories,
                filters = currentFilters,
                groupingMode = currentGroupingMode,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExpensesUiState(),
        )

    fun updateSearchQuery(value: String) {
        searchQuery.value = value
    }

    fun updateCategory(categoryId: Long?) {
        otherFilters.update { it.copy(categoryId = categoryId) }
    }

    fun updateStartDate(dateMillis: Long?) {
        otherFilters.update { it.copy(startDateMillis = dateMillis) }
    }

    fun updateEndDate(dateMillis: Long?) {
        otherFilters.update { it.copy(endDateMillis = dateMillis) }
    }

    fun updateSortOption(sortOption: ExpenseSortOption) {
        otherFilters.update { it.copy(sortOption = sortOption) }
    }

    fun updateGroupingMode(mode: ExpenseGroupingMode) {
        groupingMode.value = mode
    }

    fun clearFilters() {
        searchQuery.value = ""
        otherFilters.value = ExpenseFilters()
    }

    private fun applyFilters(expenses: List<Expense>, filters: ExpenseFilters): List<Expense> {
        val query = filters.searchQuery.trim().lowercase()

        val filtered = expenses.filter { expense ->
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                listOfNotNull(
                    expense.title,
                    expense.description,
                    expense.notes,
                    expense.category.name,
                    expense.paymentMethod.label,
                ).any { it.lowercase().contains(query) }
            }

            val matchesCategory = filters.categoryId == null || expense.category.id == filters.categoryId
            val matchesStart = filters.startDateMillis?.let { expense.occurredAt >= it } ?: true
            val matchesEnd = filters.endDateMillis?.let {
                expense.occurredAt <= endOfDayMillis(epochMillisToLocalDate(it))
            } ?: true

            matchesQuery && matchesCategory && matchesStart && matchesEnd
        }

        return when (filters.sortOption) {
            ExpenseSortOption.NEWEST -> filtered.sortedByDescending { it.occurredAt }
            ExpenseSortOption.OLDEST -> filtered.sortedBy { it.occurredAt }
            ExpenseSortOption.AMOUNT_DESC -> filtered.sortedByDescending { it.amountInCents }
            ExpenseSortOption.AMOUNT_ASC -> filtered.sortedBy { it.amountInCents }
        }
    }
}
