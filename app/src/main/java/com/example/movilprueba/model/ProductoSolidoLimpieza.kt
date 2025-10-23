package com.example.movilprueba.model

data class ProductoSolidoLimpieza(
    override val id: String,
    override val nombre: String,
    override val descripcion: String,
    override val precio: Double,
    override val categoria: String = "Limpieza",
    val peso: Double,
    val tipoDeMaterial: String
) : Producto(id, nombre, descripcion, precio, categoria)

