package com.example.limpihogar.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.limpihogar.data.database.LimpioHogarDatabase
import com.example.limpihogar.data.model.Category
import com.example.limpihogar.data.model.Product
import com.example.limpihogar.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedPriceRange: String = "Todos"
)

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.refreshProducts()
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error sincronizando: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val productsFlow = when {
                currentState.searchQuery.isNotEmpty() -> repository.searchProducts(currentState.searchQuery)
                currentState.selectedCategory != null -> repository.getProductsByCategory(currentState.selectedCategory.id)
                else -> repository.getAllProducts()
            }

            productsFlow.collect { products ->
                var filteredProducts = products
                filteredProducts = when (currentState.selectedPriceRange) {
                    "< $5000" -> products.filter { it.precio < 5000 }
                    "$5000 - $10000" -> products.filter { it.precio >= 5000 && it.precio <= 10000 }
                    "> $10000" -> products.filter { it.precio > 10000 }
                    else -> products
                }
                _uiState.value = _uiState.value.copy(products = filteredProducts)
            }
        }
    }

    // --- FUNCIONES DE UI ---
    fun selectCategory(category: Category?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category, searchQuery = "")
        loadProducts()
    }

    fun searchProducts(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, selectedCategory = null)
        loadProducts()
    }

    fun applyFilters(category: Category?, priceRange: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            selectedPriceRange = priceRange,
            searchQuery = ""
        )
        loadProducts()
    }

    suspend fun getProductById(productId: Int): Product? {
        return repository.getProductById(productId)
    }

    //NUEVO: AGREGAR PRODUCTO (CON FOTO) ---
    fun addProduct(nombre: String, marca: String, precio: Double, stock: Int, descripcion: String, imageUri: Uri?, context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            var imageUrlFinal = "placeholder" // Imagen por defecto si no hay foto

            if (imageUri != null) {
                try {
                    // Convertimos la URI a Archivo real para subirlo
                    val stream = context.contentResolver.openInputStream(imageUri)
                    val file = File.createTempFile("upload", ".jpg", context.cacheDir)
                    stream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }

                    val urlSubida = repository.uploadImage(file)
                    if (urlSubida != null) imageUrlFinal = urlSubida

                } catch (e: Exception) {
                    Log.e("ViewModel", "Error preparando imagen: ${e.message}")
                }
            }

            val nuevoProducto = Product(
                nombre = nombre,
                marca = marca,
                precio = precio,
                stock = stock,
                descripcion = descripcion,
                imagenUrl = imageUrlFinal,
                categoriaId = 1 // Categoría por defecto
            )

            val exito = repository.createProduct(nuevoProducto)
            _uiState.value = _uiState.value.copy(isLoading = false)
            if (exito) {
                onSuccess()
            }
        }
    }

    // NUEVO: ELIMINAR PRODUCTO ---
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as Application)
                val db = LimpioHogarDatabase.getDatabase(app.applicationContext)
                val repo = ProductRepository(db.productDao(), db.categoryDao())
                ProductViewModel(repo)
            }
        }
    }
}