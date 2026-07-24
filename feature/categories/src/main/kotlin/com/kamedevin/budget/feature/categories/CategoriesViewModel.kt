package com.kamedevin.budget.feature.categories

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamedevin.budget.core.designsystem.theme.BucketColors
import com.kamedevin.budget.core.domain.repository.CategoryRepository
import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CategoriesUiState> = combine(
        categoryRepository.observeAll(),
        errorMessage,
    ) { categories, error ->
        CategoriesUiState(categories = categories, isLoading = false, errorMessage = error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CategoriesUiState())

    fun addCategory(name: String, bucket: Bucket?) {
        viewModelScope.launch {
            categoryRepository.add(
                Category(
                    name = name,
                    bucket = bucket,
                    colorHex = bucket?.let { BucketColors.hexFor(it) } ?: "#4CAF50",
                ),
            )
        }
    }

    fun updateCategory(category: Category, name: String, bucket: Bucket?) {
        viewModelScope.launch {
            categoryRepository.update(
                category.copy(
                    name = name,
                    bucket = bucket,
                    colorHex = bucket?.let { BucketColors.hexFor(it) } ?: category.colorHex,
                ),
            )
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.delete(category)
            } catch (e: SQLiteConstraintException) {
                errorMessage.update {
                    "\"${category.name}\" has transactions on it and can't be deleted. Edit it instead, or move those transactions to another category first."
                }
            }
        }
    }

    fun consumeError() {
        errorMessage.update { null }
    }
}
