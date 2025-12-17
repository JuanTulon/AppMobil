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

    fun addProduct(
        nombre: String,
        marca: String,
        precio: Double,
        stock: Int,
        descripcion: String,
        imageUri: Uri?, // <--- Recibimos URI de la pantalla
        context: Context, // Necesitamos contexto para leer el archivo
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            var imgUrlFinal = "placeholder" // Valor por defecto si no hay foto

            if (imageUri != null) {
                try {
                    // 1. Abrir el flujo de datos de la URI
                    val inputStream = context.contentResolver.openInputStream(imageUri)

                    // 2. Crear un archivo temporal en la caché de la app
                    val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)

                    // 3. Copiar los datos al archivo temporal
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    // 4. Subir el archivo temporal
                    val resultadoSubida = repository.uploadImage(tempFile)

                    if (resultadoSubida != null) {
                        imgUrlFinal = resultadoSubida // Ej: "uploads/foto-123.jpg"
                    }

                    // Borrar temporal
                    tempFile.delete()

                } catch (e: Exception) {
                    Log.e("ViewModel", "Error procesando imagen: ${e.message}")
                }
            }

            // Crear el producto con la URL que nos devolvió el servidor
            val newProd = Product(
                nombre = nombre,
                marca = marca,
                precio = precio,
                stock = stock,
                descripcion = descripcion,
                imagenUrl = imgUrlFinal,
                categoriaId = 1
            )

            if (repository.createProduct(newProd)) {
                onSuccess()
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
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