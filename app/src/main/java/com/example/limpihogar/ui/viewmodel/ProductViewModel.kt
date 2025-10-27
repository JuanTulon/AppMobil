package com.example.limpihogar.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.limpihogar.data.database.LimpioHogarDatabase
import com.example.limpihogar.data.model.Category
import com.example.limpihogar.data.model.Product
import com.example.limpihogar.data.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedPriceRange: String = "Todos"
)

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository
    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        val database = LimpioHogarDatabase.getInstance(application)
        repository = ProductRepository(database.productDao(), database.categoryDao())
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val productsFlow = if (_uiState.value.searchQuery.isNotEmpty()) {
                repository.searchProducts(_uiState.value.searchQuery)
            } else if (_uiState.value.selectedCategory != null) {
                repository.getProductsByCategory(_uiState.value.selectedCategory!!.id)
            } else {
                repository.getAllProducts()
            }

            productsFlow.collect { products ->
                var filteredProducts = products
                // Filtrado por precio
                when (_uiState.value.selectedPriceRange) {
                    "< $5000" -> {
                        filteredProducts = products.filter { it.precio < 5000 }
                    }
                    "$5000 - $10000" -> {
                        filteredProducts = products.filter { it.precio >= 5000 && it.precio <= 10000 }
                    }
                    "> $10000" -> {
                        filteredProducts = products.filter { it.precio > 10000 }
                    }
                }

                _uiState.value = _uiState.value.copy(
                    products = filteredProducts,
                    isLoading = false
                )
            }
        }
    }

    fun selectCategory(category: Category?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category, searchQuery = "")
        loadProducts()
    }

    fun searchProducts(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, selectedCategory = null)
        loadProducts()
    }

    fun applyFilters(category: Category?, priceRange: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category, selectedPriceRange = priceRange, searchQuery = "")
        loadProducts()
    }

    suspend fun getProductById(productId: Int): Product? {
        return repository.getProductById(productId)
    }
}


