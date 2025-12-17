package com.example.limpihogar.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.limpihogar.data.database.LimpioHogarDatabase
import com.example.limpihogar.data.model.CartItem
import com.example.limpihogar.data.model.Product
import com.example.limpihogar.data.repository.CartRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CartRepository

    val cartItems: StateFlow<List<CartItem>>
    val cartTotal: StateFlow<Double?>
    val cartItemCount: StateFlow<Int>

    init {
        val database = LimpioHogarDatabase.getDatabase(application)
        repository = CartRepository(database.cartDao())

        cartItems = repository.getAllCartItems()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        cartTotal = repository.getCartTotal()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        cartItemCount = repository.getCartItemCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    }

    fun addProductToCart(product: Product) {
        viewModelScope.launch {
            repository.addProductToCart(product)
        }
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartItemQuantity(cartItem, newQuantity)
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            repository.removeCartItem(cartItem)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }
}

