package com.johan.misgastos.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.johan.misgastos.domain.model.AppThemeMode
import com.johan.misgastos.domain.model.UserPreferences
import com.johan.misgastos.ui.LocalSnackbarController
import com.johan.misgastos.ui.components.SectionCard

private val datePatternOptions = listOf(
    "dd/MM/yyyy",
    "MM/dd/yyyy",
    "yyyy-MM-dd",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val snackbarController = LocalSnackbarController.current
    var currencyInput by remember(preferences.currencyCode) { mutableStateOf(preferences.currencyCode) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.Message -> snackbarController.showMessage(event.value)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        item {
            SectionCard(
                title = "Tema",
                subtitle = "Elige entre claro, oscuro o seguir el sistema.",
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AppThemeMode.entries.forEach { themeMode ->
                        FilterChip(
                            selected = preferences.themeMode == themeMode,
                            onClick = { viewModel.updateThemeMode(themeMode) },
                            label = { Text(themeMode.label) },
                        )
                    }
                }
            }
        }
        item {
            SectionCard(
                title = "Moneda",
                subtitle = "Usa un código ISO de 3 letras para formatear valores.",
            ) {
                OutlinedTextField(
                    value = currencyInput,
                    onValueChange = { currencyInput = it.uppercase() },
                    label = { Text("Código de moneda") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                FilledTonalButton(
                    onClick = { viewModel.updateCurrencyCode(currencyInput) },
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    Text("Guardar moneda")
                }
            }
        }
        item {
            SectionCard(
                title = "Formato de fecha",
                subtitle = "Se usa en historial, inicio y formularios.",
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    datePatternOptions.forEach { pattern ->
                        FilterChip(
                            selected = preferences.datePattern == pattern,
                            onClick = { viewModel.updateDatePattern(pattern) },
                            label = { Text(pattern) },
                        )
                    }
                }
            }
        }
        item {
            SectionCard(
                title = "Modo offline",
                subtitle = "La app no depende de internet, login ni servicios remotos.",
            ) {
                Text(
                    text = "Todos los gastos, categorías y preferencias se guardan localmente con Room y DataStore.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
