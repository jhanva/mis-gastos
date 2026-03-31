package com.johan.misgastos.data.repository

import com.johan.misgastos.data.local.entity.CategoryEntity
import com.johan.misgastos.data.local.entity.ExpenseWithCategoryEntity
import com.johan.misgastos.domain.model.Category
import com.johan.misgastos.domain.model.Expense
import com.johan.misgastos.domain.model.PaymentMethod

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun ExpenseWithCategoryEntity.toDomain(): Expense {
    return Expense(
        id = expense.id,
        amountInCents = expense.amountInCents,
        title = expense.title,
        description = expense.description,
        category = category.toDomain(),
        paymentMethod = PaymentMethod.fromValue(expense.paymentMethod),
        occurredAt = expense.occurredAt,
        notes = expense.notes,
        createdAt = expense.createdAt,
        updatedAt = expense.updatedAt,
    )
}
