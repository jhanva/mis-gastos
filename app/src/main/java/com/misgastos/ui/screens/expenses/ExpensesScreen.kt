package com.misgastos.ui.screens.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misgastos.domain.model.Category
import com.misgastos.domain.model.Expense
import com.misgastos.domain.model.ExpenseSortOption
import com.misgastos.domain.model.UserPreferences
import com.misgastos.ui.components.AppWidthSizeClass
import com.misgastos.ui.components.contentHorizontalPadding
import com.misgastos.ui.components.ExpenseListItem
import com.misgastos.ui.components.SectionCard
import com.misgastos.ui.components.rememberAppWidthSizeClass
import com.misgastos.utils.epochMillisToLocalDate
import com.misgastos.utils.formatCurrency
import com.misgastos.utils.formatDate
import com.misgastos.utils.showDatePicker
import com.misgastos.utils.startOfDayMillis

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
            onToggleFiltersVisibility = viewModel::toggleFiltersVisibility,
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
            onToggleFiltersVisibility = viewModel::toggleFiltersVisibility,
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
    onToggleFiltersVisibility: () -> Unit,
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
        item(contentType = "filter_toggle") {
            ExpensesFilterToolbar(
                preferences = preferences,
                uiState = uiState,
                onToggleFiltersVisibility = onToggleFiltersVisibility,
                onClearFilters = onClearFilters,
            )
        }
        if (uiState.areFiltersVisible) {
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
    onToggleFiltersVisibility: () -> Unit,
    onClearFilters: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = contentHorizontalPadding(AppWidthSizeClass.EXPANDED),
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
        item(contentType = "filter_toggle") {
            ExpensesFilterToolbar(
                preferences = preferences,
                uiState = uiState,
                onToggleFiltersVisibility = onToggleFiltersVisibility,
                onClearFilters = onClearFilters,
            )
        }
        if (uiState.areFiltersVisible) {
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
private fun ExpensesFilterToolbar(
    preferences: UserPreferences,
    uiState: ExpensesUiState,
    onToggleFiltersVisibility: () -> Unit,
    onClearFilters: () -> Unit,
) {
    val activeFilterCount = remember(uiState.filters, uiState.groupingMode) {
        uiState.activeFilterCount()
    }

    SectionCard(
        title = "Busqueda y filtros",
        subtitle = if (activeFilterCount > 0) {
            "$activeFilterCount ajuste(s) activo(s)."
        } else {
            "Abre el panel solo cuando lo necesites."
        },
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilledTonalButton(onClick = onToggleFiltersVisibility) {
                Text(if (uiState.areFiltersVisible) "Ocultar filtros" else "Abrir filtros")
            }

            if (activeFilterCount > 0) {
                FilterChip(
                    selected = false,
                    onClick = onClearFilters,
                    label = { Text("Limpiar filtros") },
                )
            }
        }

        if (!uiState.areFiltersVisible && activeFilterCount > 0) {
            FlowRow(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                uiState.activeFilterLabels(preferences).forEach { label ->
                    AssistChip(
                        onClick = onToggleFiltersVisibility,
                        label = { Text(label) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
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
    val selectedCategory = uiState.categories.firstOrNull { it.id == uiState.filters.categoryId }
    val categoryLabel = selectedCategory?.let(::categoryFilterLabel) ?: "Todas"
    val (isCategoryMenuExpanded, setCategoryMenuExpanded) = remember { mutableStateOf(false) }

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
        ExposedDropdownMenuBox(
            expanded = isCategoryMenuExpanded,
            onExpandedChange = { setCategoryMenuExpanded(!isCategoryMenuExpanded) },
            modifier = Modifier.padding(top = 8.dp),
        ) {
            OutlinedTextField(
                value = categoryLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoria") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryMenuExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )

            ExposedDropdownMenu(
                expanded = isCategoryMenuExpanded,
                onDismissRequest = { setCategoryMenuExpanded(false) },
            ) {
                DropdownMenuItem(
                    text = { Text("Todas") },
                    onClick = {
                        onCategoryChange(null)
                        setCategoryMenuExpanded(false)
                    },
                )
                uiState.categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(categoryFilterLabel(category)) },
                        onClick = {
                            onCategoryChange(category.id)
                            setCategoryMenuExpanded(false)
                        },
                    )
                }
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

private fun ExpensesUiState.activeFilterCount(): Int {
    var count = 0
    if (filters.searchQuery.isNotBlank()) count += 1
    if (filters.categoryId != null) count += 1
    if (filters.startDateMillis != null) count += 1
    if (filters.endDateMillis != null) count += 1
    if (filters.sortOption != ExpenseSortOption.NEWEST) count += 1
    if (groupingMode != ExpenseGroupingMode.FLAT) count += 1
    return count
}

private fun ExpensesUiState.activeFilterLabels(
    preferences: UserPreferences,
): List<String> {
    val labels = mutableListOf<String>()

    if (filters.searchQuery.isNotBlank()) {
        labels += "Texto: ${filters.searchQuery}"
    }

    filters.categoryId
        ?.let { categoryId -> categories.firstOrNull { it.id == categoryId } }
        ?.let { category -> labels += "Categoria: ${categoryFilterLabel(category)}" }

    filters.startDateMillis?.let { startDateMillis ->
        labels += "Desde ${formatDate(startDateMillis, preferences.datePattern)}"
    }

    filters.endDateMillis?.let { endDateMillis ->
        labels += "Hasta ${formatDate(endDateMillis, preferences.datePattern)}"
    }

    if (filters.sortOption != ExpenseSortOption.NEWEST) {
        labels += "Orden: ${filters.sortOption.label}"
    }

    if (groupingMode != ExpenseGroupingMode.FLAT) {
        labels += "Vista: ${groupingMode.label}"
    }

    return labels
}
