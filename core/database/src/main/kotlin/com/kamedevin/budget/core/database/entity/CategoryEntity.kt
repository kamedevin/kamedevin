package com.kamedevin.budget.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kamedevin.budget.core.model.Bucket

/** [bucket] is null only for the built-in Income category. */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val bucket: Bucket?,
    val colorHex: String,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
)
