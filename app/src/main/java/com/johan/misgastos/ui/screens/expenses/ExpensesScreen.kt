package com.johan.misgastos.ui.screens.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.johan.misgastos.domain.model.Category
import com.johan.misgastos.domain.model.Expense
import com.johan.misgastos.domain.model.ExpenseSortOption
import com.johan.misgastos.domain.model.UserPreferences
import com.johan.misgastos.ui.components.AppWidthSizeClass
import com.johan.misgastos.ui.components.contentHorizontalPadding
import com.johan.misgastos.ui.components.ExpenseListItem
import com.johan.misgastos.ui.components.SectionCard
import com.johan.misgastos.ui.components.rememberAppWidthSizeClass
import com.johan.misgastos.utils.epochMillisToLocalDate
import com.johan.misgastos.utils.formatCurrency
import com.johan.misgastos.utils.formatDate
import com.johan.misgastos.utils.showDatePicker
import com.johan.misgastos.utils.startOfDayMillis

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpensesScreen(
    preferences: UserPreferences,
    onExpenseClick: (Long) -> Unit,
    viewModel: ExpensesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val widthSizeClass = rememberAppWidthSizeClass()
    val dayGroups = remember(uiState.expenses, uiState.filters.sortOption, uiState.groupingMode) {
        if (uiState.groupingMode == ExpenseGroupingMode.DAY) {
            groupItemsByDay(
                items = uiState.expenses,
                sortMode = uiState.filters.sortOption.toDayGroupingSortMode(),
                dateSelector = { it.occurredAt },
                amountSelector = { it.amountInCents },
            )
        } else {
            emptyList()
        }
    }

    if (widthSizeClass == AppWidthSizeClass.EXPANDED) {
        ExpandedExpensesLayout(
            preferences = preferences,
            uiState = uiState,
            dayGroups = dayGroups,
            context = context,
            onExpenseClick = onExpenseClick,
            onSearchQueryChange = viewModel::updateSearchQuery,
            onCategoryChange = viewModel::updateCategory,
            onStartDateChange = viewModel::updateStartDate,
            onEndDateChange = viewModel::updateEndDate,
            onSortOptionChange = viewModel::updateSortOption,
            onGroupingModeChange = viewModel::updateGroupingMode,
            onClearFilters = viewModel::clearFilters,
        )
    } else {
        CompactExpensesLayout(
            preferences = preferences,
            uiState = uiState,
            dayGroups = dayGroups,
            widthSizeClass = widthSizeClass,
            context = context,
            onExpenseClick = onExpenseClick,
            onSearchQueryChange = viewModel::updateSearchQuery,
            onCategoryChange = viewModel::updateCategory,
            onStartDateChange = viewModel::updateStartDate,
            onEndDateChange = viewModel::updateEndDate,
            onSortOptionChange = viewModel::updateSortOption,
            onGroupingModeChange = viewModel::updateGroupingMode,
            onClearFilters = viewModel::clearFilters,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompactExpensesLayout(
    preferences: UserPreferences,
    uiState: ExpensesUiState,
    dayGroups: List<DayGroup<Expense>>,
    widthSizeClass: AppWidthSizeClass,
    context: android.content.Context,
    onExpenseClick: (Long) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (Long?) -> Unit,
    onStartDateChange: (Long?) -> Unit,
    onEndDateChange: (Long?) -> Unit,
    onSortOptionChange: (ExpenseSortOption) -> Unit,
    onGroupingModeChange: (ExpenseGroupingMode) -> Unit,
    onClearFilters: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = contentHorizontalPadding(widthSizeClass),
            vertical = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(contentType = "screen_header") {
            Text(
                text = "Historial de gastos",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        item(contentType = "filters") {
            ExpensesFiltersSection(
                preferences = preferences,
                uiState = uiState,
                context = context,
                onSearchQueryChange = onSearchQueryChange,
                onCategoryChange = onCategoryChange,
                onStartDateChange = onStartDateChange,
                onEndDateChange = onEndDateChange,
                onSortOptionChange = onSortOptionChange,
                onGroupingModeChange = onGroupingModeChange,
                onClearFilters = onClearFilters,
            )
        }
        expenseResultItems(
            preferences = preferences,
            uiState = uiState,
            dayGroups = dayGroups,
            onExpenseClick = onExpenseClick,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExpandedExpensesLayout(
    preferences: UserPreferences,
    uiState: ExpensesUiState,
    dayGroups: List<DayGroup<Expense>>,
    context: android.content.Context,
    onExpenseClick: (Long) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (Long?) -> Unit,
    onStartDateChange: (Long?) -> Unit,
    onEndDateChange: (Long?) -> Unit,
    onSortOptionChange: (ExpenseSortOption) -> Unit,
    onGroupingModeChange: (ExpenseGroupingMode) -> Unit,
    onClearFilters: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = contentHorizontalPadding(AppWidthSizeClass.EXPANDED), vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Historial de gastos",
            style = MaterialTheme.typography.headlineMedium,
        )
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                item(contentType = "filters") {
                    ExpensesFiltersSection(
                        preferences = preferences,
                        uiState = uiState,
                        context = context,
                        onSearchQueryChange = onSearchQueryChange,
                        onCategoryChange = onCategoryChange,
                        onStartDateChange = onStartDateChange,
                        onEndDateChange = onEndDateChange,
                        onSortOptionChange = onSortOptionChange,
                        onGroupingModeChange = onGroupingModeChange,
                        onClearFilters = onClearFilters,
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                expenseResultItems(
                    preferences = preferences,
                    uiState = uiState,
                    dayGroups = dayGroups,
                    onExpenseClick = onExpenseClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExpensesFiltersSection(
    preferences: UserPreferences,
    uiState: ExpensesUiState,
    context: android.content.Context,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (Long?) -> Unit,
    onStartDateChange: (Long?) -> Unit,
    onEndDateChange: (Long?) -> Unit,
    onSortOptionChange: (ExpenseSortOption) -> Unit,
    onGroupingModeChange: (ExpenseGroupingMode) -> Unit,
    onClearFilters: () -> Unit,
) {
    SectionCard(
        title = "Filtros y busqueda",
        subtitle = "Busca por texto, categoria, fecha o cambia el orden del listado.",
    ) {
        OutlinedTextField(
            value = uiState.filters.searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Buscar") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Text(
            text = "Categoria",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 12.dp),
        )
        FlowRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = uiState.filters.categoryId == null,
                onClick = { onCategoryChange(null) },
                label = { Text("Todas") },
            )
            uiState.categories.forEach { category ->
                FilterChip(
                    selected = uiState.filters.categoryId == category.id,
                    onClick = { onCategoryChange(category.id) },
                    label = { Text(categoryFilterLabel(category)) },
                )
            }
        }

        Text(
            text = "Orden",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 12.dp),
        )
        FlowRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ExpenseSortOption.entries.forEach { option ->
                FilterChip(
                    selected = uiState.filters.sortOption == option,
                    onClick = { onSortOptionChange(option) },
                    label = { Text(option.label) },
                )
            }
        }

        Text(
            text = "Vista",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 12.dp),
        )
        FlowRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ExpenseGroupingMode.entries.forEach { mode ->
                FilterChip(
                    selected = uiState.groupingMode == mode,
                    onClick = { onGroupingModeChange(mode) },
                    label = { Text(mode.label) },
                )
            }
        }

        FlowRow(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AssistChip(
                onClick = {
                    showDatePicker(
                        context = context,
                        initialDate = uiState.filters.startDateMillis?.let(::epochMillisToLocalDate)
                            ?: java.time.LocalDate.now(),
                    ) { date ->
                        onStartDateChange(startOfDayMillis(date))
                    }
                },
                label = {
                    Text(
                        uiState.filters.startDateMillis?.let {
                            "Desde ${formatDate(it, preferences.datePattern)}"
                        } ?: "Fecha inicial",
                    )
                },
            )
            AssistChip(
                onClick = {
                    showDatePicker(
                        context = context,
                        initialDate = uiState.filters.endDateMillis?.let(::epochMillisToLocalDate)
                            ?: java.time.LocalDate.now(),
                    ) { date ->
                        onEndDateChange(startOfDayMillis(date))
                    }
                },
                label = {
                    Text(
                        uiState.filters.endDateMillis?.let {
                            "Hasta ${formatDate(it, preferences.datePattern)}"
                        } ?: "Fecha final",
                    )
                },
            )
            FilterChip(
                selected = false,
                onClick = onClearFilters,
                label = { Text("Limpiar") },
            )
        }
    }
}

private fun LazyListScope.expenseResultItems(
    preferences: UserPreferences,
    uiState: ExpensesUiState,
    dayGroups: List<DayGroup<Expense>>,
    onExpenseClick: (Long) -> Unit,
) {
    if (uiState.expenses.isEmpty()) {
        item(contentType = "empty_state") {
            SectionCard(title = "Sin resultados") {
                Text(
                    text = "No hay gastos que coincidan con los filtros actuales.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    } else {
        when (uiState.groupingMode) {
            ExpenseGroupingMode.FLAT -> {
                items(
                    items = uiState.expenses,
                    key = { it.id },
                    contentType = { "expense" },
                ) { expense ->
                    ExpenseListItem(
                        expense = expense,
                        currencyCode = preferences.currencyCode,
                        datePattern = preferences.datePattern,
                        onClick = { onExpenseClick(expense.id) },
                    )
                }
            }

            ExpenseGroupingMode.DAY -> {
                dayGroups.forEach { group ->
                    item(
                        key = "header-${group.date.toEpochDay()}",
                        contentType = "day_header",
                    ) {
                        SectionCard(
                            title = formatDate(group.firstItemAt, preferences.datePattern),
                            subtitle = "Subtotal ${formatCurrency(group.totalAmountInCents, preferences.currencyCode)}",
                        ) {
                            Text(
                                text = "${group.items.size} movimiento(s)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                    items(
                        items = group.items,
                        key = { it.id },
                        contentType = { "expense" },
                    ) { expense ->
                        ExpenseListItem(
                            expense = expense,
                            currencyCode = preferences.currencyCode,
                            datePattern = preferences.datePattern,
                            onClick = { onExpenseClick(expense.id) },
                        )
                    }
                }
            }
        }
    }
}

private fun ExpenseSortOption.toDayGroupingSortMode(): DayGroupingSortMode {
    return when (this) {
        ExpenseSortOption.NEWEST -> DayGroupingSortMode.NEWEST
        ExpenseSortOption.OLDEST -> DayGroupingSortMode.OLDEST
        ExpenseSortOption.AMOUNT_DESC -> DayGroupingSortMode.AMOUNT_DESC
        ExpenseSortOption.AMOUNT_ASC -> DayGroupingSortMode.AMOUNT_ASC
    }
}

private fun categoryFilterLabel(category: Category): String {
    return if (category.isActive) category.name else "${category.name} (inactiva)"
}
