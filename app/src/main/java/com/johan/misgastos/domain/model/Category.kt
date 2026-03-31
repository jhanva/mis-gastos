package com.johan.misgastos.domain.model

data class Category(
    val id: Long,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)
