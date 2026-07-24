package com.kamedevin.budget.core.domain.repository

import com.kamedevin.budget.core.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun add(category: Category): Long
    suspend fun update(category: Category)
    suspend fun delete(category: Category)
    suspend fun getById(id: Long): Category?
    fun observeAll(): Flow<List<Category>>
}
