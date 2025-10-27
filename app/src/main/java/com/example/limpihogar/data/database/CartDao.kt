package com.example.limpihogar.data.database

import androidx.room.*
import com.example.limpihogar.data.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items WHERE productoId = :productId LIMIT 1")
    suspend fun getCartItemByProductId(productId: Int): CartItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT SUM(precio * cantidad) FROM cart_items")
    fun getCartTotal(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>
}
