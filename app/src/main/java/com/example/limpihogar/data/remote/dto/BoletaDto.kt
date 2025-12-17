package com.example.limpihogar.data.remote.dto

data class BoletaDto(
    val id: Int? = null,
    val fecha: String? = null, // O el formato que use tu backend
    val total: Int,
    val usuarioId: Int?, // Solo mandamos el ID del usuario, no el objeto completo
    val items: List<BoletaItemDto>? = null
)

data class BoletaItemDto(
    val productoId: Int,
    val cantidad: Int,
    val precioUnitario: Int
)