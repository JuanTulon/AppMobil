package com.example.limpihogar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,
    val nombre: String,
    val precio: Double,
    val cantidad: Int = 1,
    val imagenUrl: String
) {
    val subtotal: Double
        get() = precio * cantidad
}

