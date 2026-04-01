package com.misgastos.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misgastos.domain.model.Expense
import com.misgastos.domain.model.UserPreferences
import com.misgastos.ui.components.AppWidthSizeClass
import com.misgastos.ui.components.ExpenseListItem
import com.misgastos.ui.components.SectionCard
import com.misgastos.ui.components.SummaryMetricCard
import com.misgastos.ui.components.contentHorizontalPadding
import com.misgastos.ui.components.rememberAppWidthSizeClass
import com.misgastos.utils.formatCurrency

@Composable
fun HomeScreen(
    preferences: UserPreferences,
    onAddExpense: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dashboard = uiState.dashboard
    val widthSizeClass = rememberAppWidthSizeClass()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = contentHorizontalPadding(widthSizeClass),
            vertical = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (widthSizeClass == AppWidthSizeClass.EXPANDED) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(0.95f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        HomeOverviewSection(
                            preferences = preferences,
                            dashboard = dashboard,
                            onAddExpense = onAddExpense,
                            widthSizeClass = widthSizeClass,
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1.05f),
                    ) {
                        HomeRecentExpensesSection(
                            expenses = dashboard.recentExpenses,
                            preferences = preferences,
                            onExpenseClick = onExpenseClick,
                            modifier = Modifier.heightIn(min = 280.dp),
                        )
                    }
                }
            }
        } else {
            item {
                HomeOverviewSection(
                    preferences = preferences,
                    dashboard = dashboard,
                    onAddExpense = onAddExpense,
                    widthSizeClass = widthSizeClass,
                )
            }
            item {
                HomeRecentExpensesSection(
                    expenses = dashboard.recentExpenses,
                    preferences = preferences,
                    onExpenseClick = onExpenseClick,
                )
            }
        }
    }
}

@Composable
private fun HomeOverviewSection(
    preferences: UserPreferences,
    dashboard: com.misgastos.domain.model.HomeDashboard,
    onAddExpense: () -> Unit,
    widthSizeClass: AppWidthSizeClass,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Mis Gastos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Control rapido y claro de tus movimientos.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FilledTonalButton(onClick = onAddExpense) {
            Text("Registrar gasto")
        }
        HomeMetricsSection(
            todayValue = formatCurrency(dashboard.todayTotalInCents, preferences.currencyCode),
            weekValue = formatCurrency(dashboard.weekTotalInCents, preferences.currencyCode),
            monthValue = formatCurrency(dashboard.monthTotalInCents, preferences.currencyCode),
            widthSizeClass = widthSizeClass,
        )
    }
}

@Composable
private fun HomeMetricsSection(
    todayValue: String,
    weekValue: String,
    monthValue: String,
    widthSizeClass: AppWidthSizeClass,
) {
    if (widthSizeClass == AppWidthSizeClass.COMPACT) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SummaryMetricCard(
                label = "Hoy",
                value = todayValue,
            )
            SummaryMetricCard(
                label = "Esta semana",
                value = weekValue,
            )
            SummaryMetricCard(
                label = "Este mes",
                value = monthValue,
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SummaryMetricCard(
                label = "Hoy",
                value = todayValue,
                modifier = Modifier.weight(1f),
            )
            SummaryMetricCard(
                label = "Esta semana",
                value = weekValue,
                modifier = Modifier.weight(1f),
            )
            SummaryMetricCard(
                label = "Este mes",
                value = monthValue,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HomeRecentExpensesSection(
    expenses: List<Expense>,
    preferences: UserPreferences,
    onExpenseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionCard(
        title = "Ultimos movimientos",
        subtitle = "Se actualizan al instante cuando guardas un gasto.",
        modifier = modifier,
    ) {
        if (expenses.isEmpty()) {
            Text(
                text = "Todavia no hay gastos registrados. Usa el boton flotante para crear el primero.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                expenses.forEach { expense ->
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
