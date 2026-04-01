package com.misgastos.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.misgastos.domain.model.Category
import com.misgastos.domain.model.CategoryDraft
import com.misgastos.ui.LocalSnackbarController
import com.misgastos.ui.components.AppWidthSizeClass
import com.misgastos.ui.components.SectionCard
import com.misgastos.ui.components.contentHorizontalPadding
import com.misgastos.ui.components.rememberAppWidthSizeClass
import com.misgastos.utils.categoryColorOptions
import com.misgastos.utils.categoryIconOptions
import com.misgastos.utils.colorFromHex
import com.misgastos.utils.symbolForCategory

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

    if (widthSizeClass == AppWidthSizeClass.EXPANDED) {
        ExpandedCategoriesLayout(
            categories = uiState.categories,
            onNewCategory = {
                editingCategory = null
                showDialog = true
            },
            onEditCategory = { category ->
                editingCategory = category
                showDialog = true
            },
        )
    } else {
        CompactCategoriesLayout(
            categories = uiState.categories,
            widthSizeClass = widthSizeClass,
            onNewCategory = {
                editingCategory = null
                showDialog = true
            },
            onEditCategory = { category ->
                editingCategory = category
                showDialog = true
            },
        )
    }
}

@Composable
private fun CompactCategoriesLayout(
    categories: List<Category>,
    widthSizeClass: AppWidthSizeClass,
    onNewCategory: () -> Unit,
    onEditCategory: (Category) -> Unit,
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
                text = "Categorias",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        item(contentType = "toolbar") {
            CategoriesManagementSection(
                categories = categories,
                onNewCategory = onNewCategory,
            )
        }
        items(
            items = categories,
            key = { it.id },
            contentType = { "category" },
        ) { category ->
            CategoryListItemCard(
                category = category,
                onEdit = { onEditCategory(category) },
            )
        }
    }
}

@Composable
private fun ExpandedCategoriesLayout(
    categories: List<Category>,
    onNewCategory: () -> Unit,
    onEditCategory: (Category) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = contentHorizontalPadding(AppWidthSizeClass.EXPANDED), vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Categorias",
            style = MaterialTheme.typography.headlineMedium,
        )
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                item(contentType = "toolbar") {
                    CategoriesManagementSection(
                        categories = categories,
                        onNewCategory = onNewCategory,
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
                items(
                    items = categories,
                    key = { it.id },
                    contentType = { "category" },
                ) { category ->
                    CategoryListItemCard(
                        category = category,
                        onEdit = { onEditCategory(category) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoriesManagementSection(
    categories: List<Category>,
    onNewCategory: () -> Unit,
) {
    val activeCount = categories.count(Category::isActive)
    val inactiveCount = categories.size - activeCount

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SectionCard(
            title = "Gestion flexible",
            subtitle = "Crea, edita o desactiva categorias sin perder orden visual.",
        ) {
            FilledTonalButton(onClick = onNewCategory) {
                Text("Nueva categoria")
            }
        }
        SectionCard(
            title = "Resumen",
            subtitle = "Estado rapido de tu clasificacion actual.",
        ) {
            Text(
                text = "$activeCount activa(s)",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "$inactiveCount inactiva(s)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CategoryListItemCard(
    category: Category,
    onEdit: () -> Unit,
) {
    val categoryColor = remember(category.colorHex) {
        colorFromHex(category.colorHex)
    }

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
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = symbolForCategory(category.iconName),
                        style = MaterialTheme.typography.labelMedium,
                        color = categoryColor,
                    )
                }
                Text(
                    text = if (category.isActive) "Lista para nuevos gastos" else "Oculta al crear gastos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onEdit) {
                Text("Editar")
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
            title = { Text("Eliminar categoria") },
            text = { Text("Si esta categoria ya tiene gastos asociados, la eliminacion sera bloqueada.") },
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
            Text(if (category == null) "Nueva categoria" else "Editar categoria")
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
                        text = "Icono",
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
                                    Text(option.symbol)
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
                            Box(
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
