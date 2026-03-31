package com.johan.misgastos.domain.model

data class CategoryDraft(
    val id: Long? = null,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isActive: Boolean = true,
)
