package com.kamedevin.budget.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kamedevin.budget.core.model.AccountType

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: AccountType,
    val startingBalanceCents: Long,
    val colorHex: String,
    val isArchived: Boolean = false,
    val sortOrder: Int = 0,
)
