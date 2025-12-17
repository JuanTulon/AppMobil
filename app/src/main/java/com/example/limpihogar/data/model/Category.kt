package com.example.limpihogar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una categoría de productos de limpieza.
 * La estructura es idéntica, pero los datos serán diferentes
 * (ej. "Baño", "Cocina" en lugar de "Consolas").
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    // El icono puede ser un emoji o un nombre de recurso
    val icono: String
)
