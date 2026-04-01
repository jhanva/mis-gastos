package com.johan.misgastos.ui.screens.expenseeditor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.johan.misgastos.domain.model.PaymentMethod
import com.johan.misgastos.domain.model.UserPreferences
import com.johan.misgastos.ui.LocalSnackbarController
import com.johan.misgastos.ui.components.SectionCard
import com.johan.misgastos.utils.epochMillisToLocalDate
import com.johan.misgastos.utils.epochMillisToLocalTime
import com.johan.misgastos.utils.formatDate
import com.johan.misgastos.utils.formatTime
import com.johan.misgastos.utils.showDatePicker
import com.johan.misgastos.utils.showTimePicker
import com.johan.misgastos.utils.toEpochMillis

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpenseEditorScreen(
    preferences: UserPreferences,
    onClose: () -> Unit,
    viewModel: ExpenseEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDiscardChangesDialog by remember { mutableStateOf(false) }

    BackHandler {
        when {
            showDeleteDialog -> showDeleteDialog = false
            showDiscardChangesDialog -> showDiscardChangesDialog = false
            uiState.hasUnsavedChanges -> showDiscardChangesDialog = true
            else -> onClose()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ExpenseEditorEvent.Message -> snackbarController.showMessage(event.value)
                ExpenseEditorEvent.Saved -> {
                    snackbarController.showMessage("Gasto guardado")
                    onClose()
                }
                ExpenseEditorEvent.Deleted -> {
                    snackbarController.showMessage("Gasto eliminado")
                    onClose()
                }
            }
        }
    }

    LaunchedEffect(uiState.isMissing) {
        if (uiState.isMissing) {
            snackbarController.showMessage("El gasto ya no existe")
            onClose()
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

    if (showDiscardChangesDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardChangesDialog = false },
            title = { Text("Descartar cambios") },
            text = { Text("Tienes cambios sin guardar. Si sales ahora, se perderán.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardChangesDialog = false
                        onClose()
                    },
                ) {
                    Text("Salir sin guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardChangesDialog = false }) {
                    Text("Seguir editando")
                }
            },
        )
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = if (uiState.isEditing) "Editar gasto" else "Nuevo gasto",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        item {
            SectionCard(
                title = "Datos principales",
                subtitle = "Registra el gasto con la menor fricción posible.",
            ) {
                OutlinedTextField(
                    value = uiState.amountInput,
                    onValueChange = viewModel::updateAmount,
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text("Nombre corto") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::updateDescription,
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    minLines = 2,
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
                    uiState.categories.forEach { category ->
                        FilterChip(
                            selected = uiState.selectedCategoryId == category.id,
                            onClick = { viewModel.updateCategory(category.id) },
                            label = { Text(category.name) },
                        )
                    }
                }
                Text(
                    text = "Método de pago",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 12.dp),
                )
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PaymentMethod.entries.forEach { method ->
                        FilterChip(
                            selected = uiState.paymentMethod == method,
                            onClick = { viewModel.updatePaymentMethod(method) },
                            label = { Text(method.label) },
                        )
                    }
                }
            }
        }
        item {
            SectionCard(
                title = "Fecha y hora",
                subtitle = "Puedes registrar un gasto pasado o ajustar la hora exacta.",
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AssistChip(
                        onClick = {
                            showDatePicker(
                                context = context,
                                initialDate = epochMillisToLocalDate(uiState.occurredAt),
                            ) { date ->
                                viewModel.updateOccurredAt(
                                    toEpochMillis(date, epochMillisToLocalTime(uiState.occurredAt)),
                                )
                            }
                        },
                        label = { Text(formatDate(uiState.occurredAt, preferences.datePattern)) },
                    )
                    AssistChip(
                        onClick = {
                            showTimePicker(
                                context = context,
                                initialTime = epochMillisToLocalTime(uiState.occurredAt),
                            ) { time ->
                                viewModel.updateOccurredAt(
                                    toEpochMillis(epochMillisToLocalDate(uiState.occurredAt), time),
                                )
                            }
                        },
                        label = { Text(formatTime(uiState.occurredAt)) },
                    )
                }
            }
        }
        item {
            SectionCard(title = "Notas") {
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::updateNotes,
                    label = { Text("Nota opcional") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
            }
        }
        item {
            FilledTonalButton(
                onClick = viewModel::saveExpense,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (uiState.isEditing) "Guardar cambios" else "Guardar gasto")
            }
        }
        if (uiState.isEditing) {
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
