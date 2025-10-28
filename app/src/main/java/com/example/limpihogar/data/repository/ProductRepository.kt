package com.example.limpihogar.data.repository

import com.example.limpihogar.data.database.CategoryDao
import com.example.limpihogar.data.database.ProductDao
import com.example.limpihogar.data.model.Category
import com.example.limpihogar.data.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) {
    //  Método que usa el BackOffice (AdminDashboardScreen)
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    fun getProductsByCategory(categoryId: Int): Flow<List<Product>> =
        productDao.getProductsByCategory(categoryId)

    suspend fun getProductById(productId: Int): Product? =
        productDao.getProductById(productId)

    fun searchProducts(query: String): Flow<List<Product>> =
        productDao.searchProducts(query)

    fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories()

    suspend fun getCategoryById(categoryId: Int): Category? =
        categoryDao.getCategoryById(categoryId)
}
