package com.johan.misgastos.ui.screens.expensedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.johan.misgastos.domain.model.UserPreferences
import com.johan.misgastos.ui.LocalSnackbarController
import com.johan.misgastos.ui.components.SectionCard
import com.johan.misgastos.utils.colorFromHex
import com.johan.misgastos.utils.formatCurrency
import com.johan.misgastos.utils.formatDate
import com.johan.misgastos.utils.formatTime
import com.johan.misgastos.utils.symbolForCategory

@Composable
fun ExpenseDetailScreen(
    preferences: UserPreferences,
    onEdit: (Long) -> Unit,
    onClose: () -> Unit,
    viewModel: ExpenseDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ExpenseDetailEvent.Deleted -> {
                    snackbarController.showMessage("Gasto eliminado")
                    onClose()
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar gasto") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteExpense()
                    },
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.isMissing -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "El gasto ya no existe.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        else -> {
            val expense = checkNotNull(uiState.expense)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Text(
                        text = "Detalle del gasto",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
                item {
                    SectionCard(
                        title = "Resumen",
                        subtitle = expense.title,
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraLarge)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(18.dp)
                                .fillMaxWidth(),
                        ) {
                            Text(
                                text = formatCurrency(expense.amountInCents, preferences.currencyCode),
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                item {
                    SectionCard(
                        title = "Categoría",
                        subtitle = "Organiza el gasto dentro de tu historial.",
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.large)
                                .background(colorFromHex(expense.category.colorHex).copy(alpha = 0.14f))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                        ) {
                            Text(
                                text = "${symbolForCategory(expense.category.iconName)}  ${expense.category.name}",
                                style = MaterialTheme.typography.titleMedium,
                                color = colorFromHex(expense.category.colorHex),
                            )
                        }
                    }
                }
                item {
                    SectionCard(
                        title = "Información",
                        subtitle = "Fecha, hora y forma de pago.",
                    ) {
                        ExpenseDetailLine("Fecha", formatDate(expense.occurredAt, preferences.datePattern))
                        ExpenseDetailLine("Hora", formatTime(expense.occurredAt))
                        ExpenseDetailLine("Pago", expense.paymentMethod.label)
                        if (!expense.description.isNullOrBlank()) {
                            ExpenseDetailLine("Descripción", expense.description)
                        }
                        if (!expense.notes.isNullOrBlank()) {
                            ExpenseDetailLine("Nota", expense.notes)
                        }
                    }
                }
                item {
                    FilledTonalButton(
                        onClick = { onEdit(expense.id) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Editar gasto")
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Eliminar gasto")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseDetailLine(
    label: String,
    value: String,
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
