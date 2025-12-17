package com.example.limpihogar.data.repository

import android.util.Log
import com.example.limpihogar.data.database.ProductDao
import com.example.limpihogar.data.database.CategoryDao
import com.example.limpihogar.data.model.Product
import com.example.limpihogar.data.model.Category
import com.example.limpihogar.data.remote.retrofitClient.RetrofitClient
import com.example.limpihogar.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ProductRepository(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) {

    // 1.La UI siempre mira la Base de Datos Local (Room)
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    // 2. SINCRONIZACIÓN (Nube -> Local)
    suspend fun refreshProducts() {
        try {
            Log.d("REPO", "Iniciando sincronización con Backend...")

            // A. Llamada a Internet (Retrofit)
            val response = RetrofitClient.apiService.obtenerProductos()

            if (response.isSuccessful && response.body() != null) {
                val listaRemota = response.body()!!

                // B. Transformación (DTO -> Entity)
                val listaLocal = listaRemota.map { it.toEntity() }

                // C. Actualización de Caché
                productDao.deleteAll()
                productDao.insertProducts(listaLocal)

                Log.d("REPO", "Sincronización exitosa: ${listaLocal.size} productos guardados.")
            } else {
                Log.e("REPO", "Error del Servidor: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("REPO", "Modo Offline: No se pudo conectar al backend (${e.message})")
        }
    }

    // --- Métodos de Lectura Local ---

    fun getProductsByCategory(categoryId: Int): Flow<List<Product>> =
        productDao.getProductsByCategory(categoryId)

    fun searchProducts(query: String): Flow<List<Product>> =
        productDao.searchProducts(query)

    suspend fun getProductById(id: Int): Product? =
        productDao.getProductById(id)

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    // --- NUEVOS MÉTODOS PARA BACKOFFICE ---

    // 1. Crear Producto
    suspend fun createProduct(product: Product): Boolean {
        return try {
            val productoDto = com.example.limpihogar.data.remote.dto.ProductoDto(
                id = null,
                nombre = product.nombre,
                descripcionCorta = product.descripcion,
                descripcionLarga = product.descripcion,
                precio = product.precio.toInt(),
                oferta = false,
                precioOferta = null,
                categoria = "General",
                img = product.imagenUrl,
                stock = product.stock,
                iva = 19
            )

            val response = RetrofitClient.apiService.crear(productoDto)

            if (response.isSuccessful && response.body() != null) {
                val nuevoProductoRemoto = response.body()!!
                productDao.insertProduct(nuevoProductoRemoto.toEntity())
                true
            } else {
                Log.e("REPO", "Error creando: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("REPO", "Error de red creando producto: ${e.message}")
            false
        }
    }

    // 2. Eliminar Producto
    suspend fun deleteProduct(product: Product) {
        try {
            val response = RetrofitClient.apiService.eliminar(product.id)
            if (response.isSuccessful) {
                productDao.deleteProduct(product)
            } else {
                Log.e("REPO", "Error eliminando en servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("REPO", "Error de red eliminando: ${e.message}")
        }
    }

    suspend fun uploadImage(imageFile: java.io.File): String? {
        return try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = RetrofitClient.apiService.subirImagen(body)

            if (response.isSuccessful && response.body() != null) {
                // Leemos el string crudo que manda el backend (ej: "uploads/foto.jpg")
                response.body()!!.string()
            } else {
                Log.e("REPO", "Error subida: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("REPO", "Excepción subida: ${e.message}")
            null
        }
    }
}