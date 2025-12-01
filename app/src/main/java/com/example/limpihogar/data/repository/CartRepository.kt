package com.example.limpihogar.data.repository

import com.example.limpihogar.data.database.CartDao
import com.example.limpihogar.data.model.CartItem
import com.example.limpihogar.data.model.Product
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {

    fun getAllCartItems(): Flow<List<CartItem>> = cartDao.getAllCartItems()

    fun getCartTotal(): Flow<Double?> = cartDao.getCartTotal()

    fun getCartItemCount(): Flow<Int> = cartDao.getCartItemCount()

    suspend fun addProductToCart(product: Product) {
        val existingItem = cartDao.getCartItemByProductId(product.id)
        if (existingItem != null) {
            // Incrementar cantidad
            cartDao.updateCartItem(existingItem.copy(cantidad = existingItem.cantidad + 1))
        } else {
            // Agregar nuevo item
            val cartItem = CartItem(
                productoId = product.id,
                nombre = product.nombre,
                precio = product.precio,
                cantidad = 1,
                imagenUrl = product.imagenUrl
            )
            cartDao.insertCartItem(cartItem)
        }
    }

    suspend fun updateCartItemQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity > 0) {
            cartDao.updateCartItem(cartItem.copy(cantidad = newQuantity))
        } else {
            cartDao.deleteCartItem(cartItem)
        }
    }

    suspend fun removeCartItem(cartItem: CartItem) = cartDao.deleteCartItem(cartItem)

    suspend fun clearCart() = cartDao.clearCart()
}

