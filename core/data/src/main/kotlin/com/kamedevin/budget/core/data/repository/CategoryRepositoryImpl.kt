package com.kamedevin.budget.core.data.repository

import com.kamedevin.budget.core.data.mapper.toDomain
import com.kamedevin.budget.core.data.mapper.toEntity
import com.kamedevin.budget.core.database.dao.CategoryDao
import com.kamedevin.budget.core.domain.repository.CategoryRepository
import com.kamedevin.budget.core.model.Category
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
) : CategoryRepository {

    override suspend fun add(category: Category): Long = categoryDao.insert(category.toEntity())

    override suspend fun update(category: Category) {
        categoryDao.update(category.toEntity())
    }

    override suspend fun delete(category: Category) {
        categoryDao.delete(category.toEntity())
    }

    override suspend fun getById(id: Long): Category? = categoryDao.getById(id)?.toDomain()

    override fun observeAll(): Flow<List<Category>> =
        categoryDao.observeAll().map { list -> list.map { it.toDomain() } }
}
