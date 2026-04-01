package com.misgastos.ui.screens.subscriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misgastos.domain.model.PaymentMethod
import com.misgastos.domain.model.Subscription
import com.misgastos.domain.model.UserPreferences
import com.misgastos.ui.LocalSnackbarController
import com.misgastos.ui.components.AppWidthSizeClass
import com.misgastos.ui.components.SectionCard
import com.misgastos.ui.components.SummaryMetricCard
import com.misgastos.ui.components.contentHorizontalPadding
import com.misgastos.ui.components.rememberAppWidthSizeClass
import com.misgastos.utils.formatAmountInputFromCents
import com.misgastos.utils.formatCurrency

@Composable
fun SubscriptionsScreen(
    preferences: UserPreferences,
    viewModel: SubscriptionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current
    val widthSizeClass = rememberAppWidthSizeClass()
    var editingSubscription by remember { mutableStateOf<Subscription?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SubscriptionsEvent.Message -> snackbarController.showMessage(event.value)
                is SubscriptionsEvent.Saved -> {
                    snackbarController.showMessage(event.value)
                    showDialog = false
                    editingSubscription = null
                }
                is SubscriptionsEvent.Deleted -> {
                    snackbarController.showMessage(event.value)
                    showDialog = false
                    editingSubscription = null
                }
            }
        }
    }

    if (showDialog) {
        SubscriptionEditorDialog(
            subscription = editingSubscription,
            onDismiss = {
                showDialog = false
                editingSubscription = null
            },
            onSave = viewModel::saveSubscription,
            onDelete = viewModel::deleteSubscription,
        )
    }

    if (widthSizeClass == AppWidthSizeClass.EXPANDED) {
        ExpandedSubscriptionsLayout(
            preferences = preferences,
            uiState = uiState,
            onNewSubscription = {
                editingSubscription = null
                showDialog = true
            },
            onEditSubscription = { subscription ->
                editingSubscription = subscription
                showDialog = true
            },
        )
    } else {
        CompactSubscriptionsLayout(
            preferences = preferences,
            uiState = uiState,
            widthSizeClass = widthSizeClass,
            onNewSubscription = {
                editingSubscription = null
                showDialog = true
            },
            onEditSubscription = { subscription ->
                editingSubscription = subscription
                showDialog = true
            },
        )
    }
}

@Composable
private fun CompactSubscriptionsLayout(
    preferences: UserPreferences,
    uiState: SubscriptionsUiState,
    widthSizeClass: AppWidthSizeClass,
    onNewSubscription: () -> Unit,
    onEditSubscription: (Subscription) -> Unit,
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
                text = "Plataformas",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        item(contentType = "management") {
            SubscriptionsManagementSection(
                summary = uiState.summary,
                preferences = preferences,
                onNewSubscription = onNewSubscription,
            )
        }
        subscriptionItems(
            subscriptions = uiState.subscriptions,
            currencyCode = preferences.currencyCode,
            onEditSubscription = onEditSubscription,
        )
    }
}

@Composable
private fun ExpandedSubscriptionsLayout(
    preferences: UserPreferences,
    uiState: SubscriptionsUiState,
    onNewSubscription: () -> Unit,
    onEditSubscription: (Subscription) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = contentHorizontalPadding(AppWidthSizeClass.EXPANDED),
                vertical = 24.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Plataformas",
            style = MaterialTheme.typography.headlineMedium,
        )
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                item(contentType = "management") {
                    SubscriptionsManagementSection(
                        summary = uiState.summary,
                        preferences = preferences,
                        onNewSubscription = onNewSubscription,
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
                subscriptionItems(
                    subscriptions = uiState.subscriptions,
                    currencyCode = preferences.currencyCode,
                    onEditSubscription = onEditSubscription,
                )
            }
        }
    }
}

@Composable
private fun SubscriptionsManagementSection(
    summary: SubscriptionSummary,
    preferences: UserPreferences,
    onNewSubscription: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SectionCard(
            title = "Cobros mensuales",
            subtitle = "Guarda Netflix, Crunchyroll, ChatGPT y otros cargos recurrentes.",
        ) {
            FilledTonalButton(onClick = onNewSubscription) {
                Text("Nueva plataforma")
            }
        }
        SectionCard(
            title = "Resumen mensual",
            subtitle = if (summary.subscriptionCount > 0) {
                "${summary.subscriptionCount} cobro(s) configurado(s)"
            } else {
                "Todavia no has agregado plataformas"
            },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SummaryMetricCard(
                    label = "Total fijo",
                    value = formatCurrency(summary.totalMonthlyInCents, preferences.currencyCode),
                )
                SummaryMetricCard(
                    label = "Debito",
                    value = formatCurrency(summary.debitMonthlyInCents, preferences.currencyCode),
                )
                SummaryMetricCard(
                    label = "Credito",
                    value = formatCurrency(summary.creditMonthlyInCents, preferences.currencyCode),
                )
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.subscriptionItems(
    subscriptions: List<Subscription>,
    currencyCode: String,
    onEditSubscription: (Subscription) -> Unit,
) {
    if (subscriptions.isEmpty()) {
        item(contentType = "empty_state") {
            SectionCard(title = "Sin plataformas") {
                Text(
                    text = "Agrega tus pagos mensuales para saber cuanto se te va cada mes y en que dia cae cada cobro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    } else {
        items(
            items = subscriptions,
            key = { it.id },
            contentType = { "subscription" },
        ) { subscription ->
            SubscriptionListItemCard(
                subscription = subscription,
                currencyCode = currencyCode,
                onEdit = { onEditSubscription(subscription) },
            )
        }
    }
}

@Composable
private fun SubscriptionListItemCard(
    subscription: Subscription,
    currencyCode: String,
    onEdit: () -> Unit,
) {
    SectionCard(
        title = subscription.name,
        subtitle = "Se cobra el dia ${subscription.billingDay} por ${subscription.paymentMethod.label}",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = formatCurrency(subscription.monthlyAmountInCents, currencyCode),
                style = MaterialTheme.typography.titleLarge,
            )
            TextButton(onClick = onEdit) {
                Text("Editar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubscriptionEditorDialog(
    subscription: Subscription?,
    onDismiss: () -> Unit,
    onSave: (Long?, String, String, String, PaymentMethod) -> Unit,
    onDelete: (Long) -> Unit,
) {
    var name by remember(subscription) { mutableStateOf(subscription?.name.orEmpty()) }
    var amountInput by remember(subscription) {
        mutableStateOf(
            subscription?.let { current ->
                formatAmountInputFromCents(current.monthlyAmountInCents)
            }.orEmpty(),
        )
    }
    var billingDayInput by remember(subscription) {
        mutableStateOf(subscription?.billingDay?.toString().orEmpty())
    }
    var paymentMethod by remember(subscription) {
        mutableStateOf(
            subscription?.paymentMethod?.takeIf { current ->
                current in recurringPaymentMethods()
            } ?: PaymentMethod.CREDIT_CARD,
        )
    }
    var isPaymentMethodMenuExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm && subscription != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar plataforma") },
            text = { Text("Esta accion no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(subscription.id)
                    },
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (subscription == null) "Nueva plataforma" else "Editar plataforma")
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
                item {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Valor mensual") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
                item {
                    OutlinedTextField(
                        value = billingDayInput,
                        onValueChange = { billingDayInput = it },
                        label = { Text("Dia de cobro") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
                item {
                    ExposedDropdownMenuBox(
                        expanded = isPaymentMethodMenuExpanded,
                        onExpandedChange = {
                            isPaymentMethodMenuExpanded = !isPaymentMethodMenuExpanded
                        },
                    ) {
                        OutlinedTextField(
                            value = paymentMethod.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Se paga con") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = isPaymentMethodMenuExpanded,
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        )

                        ExposedDropdownMenu(
                            expanded = isPaymentMethodMenuExpanded,
                            onDismissRequest = { isPaymentMethodMenuExpanded = false },
                        ) {
                            recurringPaymentMethods().forEach { method ->
                                DropdownMenuItem(
                                    text = { Text(method.label) },
                                    onClick = {
                                        paymentMethod = method
                                        isPaymentMethodMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        subscription?.id,
                        name,
                        amountInput,
                        billingDayInput,
                        paymentMethod,
                    )
                },
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (subscription != null) {
                    TextButton(onClick = { showDeleteConfirm = true }) {
                        Text("Eliminar")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        },
    )
}
