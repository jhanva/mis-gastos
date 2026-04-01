package com.johan.misgastos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subscriptions",
    indices = [Index("billingDay"), Index("name")],
)
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val monthlyAmountInCents: Long,
    val billingDay: Int,
    val paymentMethod: String,
    val createdAt: Long,
    val updatedAt: Long,
)
