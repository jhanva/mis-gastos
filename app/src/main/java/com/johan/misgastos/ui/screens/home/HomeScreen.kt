package com.johan.misgastos.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.johan.misgastos.domain.model.UserPreferences
import com.johan.misgastos.ui.components.ExpenseListItem
import com.johan.misgastos.ui.components.SectionCard
import com.johan.misgastos.ui.components.SummaryMetricCard
import com.johan.misgastos.utils.formatCurrency

@Composable
fun HomeScreen(
    preferences: UserPreferences,
    onAddExpense: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dashboard = uiState.dashboard

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Mis gastos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        item {
            Text(
                text = "Control rápido, limpio y 100% offline.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            FilledTonalButton(onClick = onAddExpense) {
                Text("Registrar gasto")
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SummaryMetricCard(
                    label = "Hoy",
                    value = formatCurrency(dashboard.todayTotalInCents, preferences.currencyCode),
                    modifier = Modifier.weight(1f),
                )
                SummaryMetricCard(
                    label = "Este mes",
                    value = formatCurrency(dashboard.monthTotalInCents, preferences.currencyCode),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            SectionCard(
                title = "Últimos movimientos",
                subtitle = "Se actualizan al instante cuando guardas un gasto.",
            ) {
                if (dashboard.recentExpenses.isEmpty()) {
                    Text(
                        text = "Todavía no hay gastos registrados. Usa el botón flotante para crear el primero.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        items(dashboard.recentExpenses, key = { it.id }) { expense ->
            ExpenseListItem(
                expense = expense,
                currencyCode = preferences.currencyCode,
                datePattern = preferences.datePattern,
                onClick = { onExpenseClick(expense.id) },
            )
        }
    }
}
