package com.example.limpihogar.data.database

import androidx.room.*
import com.example.limpihogar.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY nombre ASC")
    fun getAllProducts(): Flow<List<Product>>

    // Para el BackOffice: Búsquedas específicas
    @Query("SELECT * FROM products WHERE categoriaId = :categoryId ORDER BY nombre ASC")
    fun getProductsByCategory(categoryId: Int): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE nombre LIKE '%' || :searchQuery || '%' ORDER BY nombre ASC")
    fun searchProducts(searchQuery: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Int): Product?


    // 1. Borra toda la caché antigua
    @Query("DELETE FROM products")
    suspend fun deleteAll()

    // 2. Guarda la lista nueva que llegó de internet
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)
}