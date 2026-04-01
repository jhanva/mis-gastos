package com.johan.misgastos.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.johan.misgastos.domain.model.AppThemeMode
import com.johan.misgastos.domain.model.UserPreferences
import com.johan.misgastos.ui.LocalSnackbarController
import com.johan.misgastos.ui.components.AppWidthSizeClass
import com.johan.misgastos.ui.components.SectionCard
import com.johan.misgastos.ui.components.contentHorizontalPadding
import com.johan.misgastos.ui.components.rememberAppWidthSizeClass

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
    val widthSizeClass = rememberAppWidthSizeClass()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.Message -> snackbarController.showMessage(event.value)
            }
        }
    }

    if (widthSizeClass == AppWidthSizeClass.EXPANDED) {
        ExpandedSettingsLayout(
            preferences = preferences,
            onThemeModeChange = viewModel::updateThemeMode,
            onDatePatternChange = viewModel::updateDatePattern,
        )
    } else {
        CompactSettingsLayout(
            preferences = preferences,
            widthSizeClass = widthSizeClass,
            onThemeModeChange = viewModel::updateThemeMode,
            onDatePatternChange = viewModel::updateDatePattern,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompactSettingsLayout(
    preferences: UserPreferences,
    widthSizeClass: AppWidthSizeClass,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onDatePatternChange: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = contentHorizontalPadding(widthSizeClass),
            vertical = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        item {
            SettingsThemeSection(
                selectedThemeMode = preferences.themeMode,
                onThemeModeChange = onThemeModeChange,
            )
        }
        item {
            SettingsDateFormatSection(
                selectedPattern = preferences.datePattern,
                onDatePatternChange = onDatePatternChange,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExpandedSettingsLayout(
    preferences: UserPreferences,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onDatePatternChange: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = contentHorizontalPadding(AppWidthSizeClass.EXPANDED),
            vertical = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    SettingsThemeSection(
                        selectedThemeMode = preferences.themeMode,
                        onThemeModeChange = onThemeModeChange,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    SettingsDateFormatSection(
                        selectedPattern = preferences.datePattern,
                        onDatePatternChange = onDatePatternChange,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SettingsThemeSection(
    selectedThemeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
) {
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
                    selected = selectedThemeMode == themeMode,
                    onClick = { onThemeModeChange(themeMode) },
                    label = { Text(themeMode.label) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SettingsDateFormatSection(
    selectedPattern: String,
    onDatePatternChange: (String) -> Unit,
) {
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
                    selected = selectedPattern == pattern,
                    onClick = { onDatePatternChange(pattern) },
                    label = { Text(pattern) },
                )
            }
        }
    }
}
