package com.johan.misgastos.ui.screens.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.johan.misgastos.domain.model.ExpenseSortOption
import com.johan.misgastos.domain.model.UserPreferences
import com.johan.misgastos.ui.components.ExpenseListItem
import com.johan.misgastos.ui.components.SectionCard
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Historial de gastos",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        item {
            SectionCard(
                title = "Filtros y búsqueda",
                subtitle = "Busca por texto, categoría, fecha o cambia el orden del listado.",
            ) {
                OutlinedTextField(
                    value = uiState.filters.searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    label = { Text("Buscar") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Text(
                    text = "Categoría",
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
                        onClick = { viewModel.updateCategory(null) },
                        label = { Text("Todas") },
                    )
                    uiState.categories.forEach { category ->
                        FilterChip(
                            selected = uiState.filters.categoryId == category.id,
                            onClick = { viewModel.updateCategory(category.id) },
                            label = { Text(category.name) },
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
                            onClick = { viewModel.updateSortOption(option) },
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
                            onClick = { viewModel.updateGroupingMode(mode) },
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
                                viewModel.updateStartDate(startOfDayMillis(date))
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
                                viewModel.updateEndDate(startOfDayMillis(date))
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
                        onClick = viewModel::clearFilters,
                        label = { Text("Limpiar") },
                    )
                }
            }
        }
        if (uiState.expenses.isEmpty()) {
            item {
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
                    items(uiState.expenses, key = { it.id }) { expense ->
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
                        item(key = "header-${group.date.toEpochDay()}") {
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
                        items(group.items, key = { it.id }) { expense ->
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
}

private fun ExpenseSortOption.toDayGroupingSortMode(): DayGroupingSortMode {
    return when (this) {
        ExpenseSortOption.NEWEST -> DayGroupingSortMode.NEWEST
        ExpenseSortOption.OLDEST -> DayGroupingSortMode.OLDEST
        ExpenseSortOption.AMOUNT_DESC -> DayGroupingSortMode.AMOUNT_DESC
        ExpenseSortOption.AMOUNT_ASC -> DayGroupingSortMode.AMOUNT_ASC
    }
}
