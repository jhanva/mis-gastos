package com.misgastos.ui.screens.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misgastos.domain.model.Category
import com.misgastos.domain.model.Expense
import com.misgastos.domain.model.ExpenseFilters
import com.misgastos.domain.model.ExpenseSortOption
import com.misgastos.domain.repository.CategoryRepository
import com.misgastos.domain.repository.ExpenseRepository
import com.misgastos.utils.endOfDayMillis
import com.misgastos.utils.epochMillisToLocalDate
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
    val areFiltersVisible: Boolean = false,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    expenseRepository: ExpenseRepository,
    categoryRepository: CategoryRepository,
) : ViewModel() {

    private val searchQuery = MutableStateFlow(savedStateHandle[KEY_SEARCH_QUERY] ?: "")
    private val otherFilters = MutableStateFlow(
        ExpenseFilters(
            categoryId = savedStateHandle[KEY_CATEGORY_ID],
            startDateMillis = savedStateHandle[KEY_START_DATE_MILLIS],
            endDateMillis = savedStateHandle[KEY_END_DATE_MILLIS],
            sortOption = savedStateHandle.get<String>(KEY_SORT_OPTION)
                ?.let(::expenseSortOptionFromName)
                ?: ExpenseSortOption.NEWEST,
        ),
    )
    private val groupingMode = MutableStateFlow(
        savedStateHandle.get<String>(KEY_GROUPING_MODE)
            ?.let(::expenseGroupingModeFromName)
            ?: ExpenseGroupingMode.FLAT,
    )
    private val areFiltersVisible = MutableStateFlow(savedStateHandle[KEY_ARE_FILTERS_VISIBLE] ?: false)

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
            areFiltersVisible,
        ) { expenses, categories, currentFilters, currentGroupingMode, currentAreFiltersVisible ->
            ExpensesUiState(
                expenses = expenses,
                categories = categories,
                filters = currentFilters,
                groupingMode = currentGroupingMode,
                areFiltersVisible = currentAreFiltersVisible,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExpensesUiState(),
        )

    fun updateSearchQuery(value: String) {
        searchQuery.value = value
        savedStateHandle[KEY_SEARCH_QUERY] = value
    }

    fun updateCategory(categoryId: Long?) {
        otherFilters.updateAndPersist { it.copy(categoryId = categoryId) }
    }

    fun updateStartDate(dateMillis: Long?) {
        otherFilters.updateAndPersist { it.copy(startDateMillis = dateMillis) }
    }

    fun updateEndDate(dateMillis: Long?) {
        otherFilters.updateAndPersist { it.copy(endDateMillis = dateMillis) }
    }

    fun updateSortOption(sortOption: ExpenseSortOption) {
        otherFilters.updateAndPersist { it.copy(sortOption = sortOption) }
    }

    fun updateGroupingMode(mode: ExpenseGroupingMode) {
        groupingMode.value = mode
        savedStateHandle[KEY_GROUPING_MODE] = mode.name
    }

    fun toggleFiltersVisibility() {
        val nextValue = !areFiltersVisible.value
        areFiltersVisible.value = nextValue
        savedStateHandle[KEY_ARE_FILTERS_VISIBLE] = nextValue
    }

    fun clearFilters() {
        searchQuery.value = ""
        savedStateHandle[KEY_SEARCH_QUERY] = ""
        otherFilters.value = ExpenseFilters()
        persistFilters(otherFilters.value)
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

    private fun MutableStateFlow<ExpenseFilters>.updateAndPersist(
        transform: (ExpenseFilters) -> ExpenseFilters,
    ) {
        update { currentFilters ->
            transform(currentFilters).also(::persistFilters)
        }
    }

    private fun persistFilters(filters: ExpenseFilters) {
        savedStateHandle[KEY_CATEGORY_ID] = filters.categoryId
        savedStateHandle[KEY_START_DATE_MILLIS] = filters.startDateMillis
        savedStateHandle[KEY_END_DATE_MILLIS] = filters.endDateMillis
        savedStateHandle[KEY_SORT_OPTION] = filters.sortOption.name
    }

    private companion object {
        const val KEY_SEARCH_QUERY = "expenses_search_query"
        const val KEY_CATEGORY_ID = "expenses_category_id"
        const val KEY_START_DATE_MILLIS = "expenses_start_date_millis"
        const val KEY_END_DATE_MILLIS = "expenses_end_date_millis"
        const val KEY_SORT_OPTION = "expenses_sort_option"
        const val KEY_GROUPING_MODE = "expenses_grouping_mode"
        const val KEY_ARE_FILTERS_VISIBLE = "expenses_are_filters_visible"

        fun expenseSortOptionFromName(name: String): ExpenseSortOption {
            return ExpenseSortOption.entries.firstOrNull { it.name == name } ?: ExpenseSortOption.NEWEST
        }

        fun expenseGroupingModeFromName(name: String): ExpenseGroupingMode {
            return ExpenseGroupingMode.entries.firstOrNull { it.name == name } ?: ExpenseGroupingMode.FLAT
        }
    }
}
