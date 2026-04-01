package com.misgastos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("categoryId"), Index("occurredAt")],
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amountInCents: Long,
    val title: String,
    val description: String?,
    val categoryId: Long,
    val paymentMethod: String,
    val occurredAt: Long,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
