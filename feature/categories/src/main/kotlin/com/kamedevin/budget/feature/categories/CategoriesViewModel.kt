package com.kamedevin.budget.feature.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamedevin.budget.core.designsystem.theme.BucketColors
import com.kamedevin.budget.core.domain.repository.CategoryRepository
import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val uiState: StateFlow<CategoriesUiState> = categoryRepository.observeAll()
        .map { CategoriesUiState(categories = it, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CategoriesUiState())

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
        viewModelScope.launch { categoryRepository.delete(category) }
    }
}
