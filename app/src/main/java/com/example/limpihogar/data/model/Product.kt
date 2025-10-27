package com.example.limpihogar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un producto de limpieza.
 * He cambiado 'fabricante' por 'marca' que es más común para artículos de aseo.
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val precioAnterior: Double? = null,
    val stock: Int,
    val categoriaId: Int,
    val imagenUrl: String, // Usarás nombres como "cloro_gel", "detergente_ariel"
    val marca: String, // Cambiado de 'fabricante'
    val calificacion: Float = 0f,
    val numeroReviews: Int = 0,
    val formato: String? = null // Campo extra, ej: "Botella 900ml", "Pack 6 unidades"
)
