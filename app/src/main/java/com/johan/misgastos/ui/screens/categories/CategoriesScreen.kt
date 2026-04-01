package com.johan.misgastos.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.johan.misgastos.domain.model.Category
import com.johan.misgastos.domain.model.CategoryDraft
import com.johan.misgastos.ui.LocalSnackbarController
import com.johan.misgastos.ui.components.contentHorizontalPadding
import com.johan.misgastos.ui.components.SectionCard
import com.johan.misgastos.ui.components.rememberAppWidthSizeClass
import com.johan.misgastos.utils.categoryColorOptions
import com.johan.misgastos.utils.categoryIconOptions
import com.johan.misgastos.utils.colorFromHex
import com.johan.misgastos.utils.symbolForCategory

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current
    val widthSizeClass = rememberAppWidthSizeClass()
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CategoriesEvent.Message -> snackbarController.showMessage(event.value)
                is CategoriesEvent.Saved -> {
                    snackbarController.showMessage(event.value)
                    showDialog = false
                    editingCategory = null
                }
                is CategoriesEvent.Deleted -> {
                    snackbarController.showMessage(event.value)
                    showDialog = false
                    editingCategory = null
                }
            }
        }
    }

    if (showDialog) {
        CategoryEditorDialog(
            category = editingCategory,
            onDismiss = {
                showDialog = false
                editingCategory = null
            },
            onSave = viewModel::saveCategory,
            onDelete = viewModel::deleteCategory,
        )
    }

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
                text = "Categorías",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        item {
            SectionCard(
                title = "Gestión flexible",
                subtitle = "Crea, edita o desactiva categorías sin perder orden visual.",
            ) {
                FilledTonalButton(
                    onClick = {
                        editingCategory = null
                        showDialog = true
                    },
                ) {
                    Text("Nueva categoría")
                }
            }
        }
        items(uiState.categories, key = { it.id }) { category ->
            SectionCard(
                title = category.name,
                subtitle = if (category.isActive) "Activa" else "Inactiva",
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(colorFromHex(category.colorHex).copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = symbolForCategory(category.iconName),
                                style = MaterialTheme.typography.labelMedium,
                                color = colorFromHex(category.colorHex),
                            )
                        }
                        Text(
                            text = if (category.isActive) "Lista para nuevos gastos" else "Oculta al crear gastos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    TextButton(
                        onClick = {
                            editingCategory = category
                            showDialog = true
                        },
                    ) {
                        Text("Editar")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryEditorDialog(
    category: Category?,
    onDismiss: () -> Unit,
    onSave: (CategoryDraft) -> Unit,
    onDelete: (Long) -> Unit,
) {
    var name by remember(category) { mutableStateOf(category?.name.orEmpty()) }
    var selectedIcon by remember(category) { mutableStateOf(category?.iconName ?: categoryIconOptions.first().key) }
    var selectedColor by remember(category) { mutableStateOf(category?.colorHex ?: "#2E7D32") }
    var isActive by remember(category) { mutableStateOf(category?.isActive ?: true) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm && category != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar categoría") },
            text = { Text("Si esta categoría ya tiene gastos asociados, la eliminación será bloqueada.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(category.id)
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
            Text(if (category == null) "Nueva categoría" else "Editar categoría")
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
                    Text(
                        text = "Ícono",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        categoryIconOptions.forEach { option ->
                            FilterChip(
                                selected = selectedIcon == option.key,
                                onClick = { selectedIcon = option.key },
                                label = { Text(option.label) },
                                leadingIcon = {
                                    Text(
                                        text = option.symbol,
                                    )
                                },
                            )
                        }
                    }
                }
                item {
                    Text(
                        text = "Color",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        categoryColorOptions.forEach { color ->
                            val hex = "#%02X%02X%02X".format(
                                (color.red * 255).toInt(),
                                (color.green * 255).toInt(),
                                (color.blue * 255).toInt(),
                            )
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (selectedColor == hex) 2.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape,
                                    )
                                    .clickable { selectedColor = hex },
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Disponible para nuevos gastos")
                        Switch(
                            checked = isActive,
                            onCheckedChange = { isActive = it },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        CategoryDraft(
                            id = category?.id,
                            name = name,
                            iconName = selectedIcon,
                            colorHex = selectedColor,
                            isActive = isActive,
                        ),
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
                if (category != null) {
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
