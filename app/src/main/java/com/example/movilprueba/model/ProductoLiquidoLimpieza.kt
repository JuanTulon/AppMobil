package com.example.movilprueba.model

data class ProductoLiquidoLimpieza(
    override val id: String,
    override val nombre: String,
    override val descripcion: String,
    override val precio: Double,
    override val categoria: String = "Limpieza",
    val volumen: Double,
    val esFragante: Boolean
) : Producto(id, nombre, descripcion, precio, categoria)

