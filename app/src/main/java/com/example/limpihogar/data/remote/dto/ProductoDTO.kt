package com.example.limpihogar.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductoDto(
    val id: Int,
    val nombre: String,
    val descripcionCorta: String?,
    val descripcionLarga: String?,
    val precio: Int,
    val oferta: Boolean,
    val precioOferta: Int?,
    val categoria: String?,
    val img: String?,
    val stock: Int,
    val iva: Int?
)