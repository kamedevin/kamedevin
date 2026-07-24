package com.kamedevin.budget.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kamedevin.budget.core.model.TransactionSource
import com.kamedevin.budget.core.model.TransactionType
import java.time.Instant

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("categoryId"), Index("accountId"), Index("date")],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountCents: Long,
    val type: TransactionType,
    val categoryId: Long,
    val accountId: Long,
    val date: Instant,
    val note: String? = null,
    val source: TransactionSource = TransactionSource.MANUAL,
    val externalId: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
)
