package com.example.limpihogar.data.remote.dto

import com.example.limpihogar.data.model.Product

fun ProductoDto.toEntity(): Product {
    // Si el backend manda solo el nombre de la imagen, le pegamos la base de AWS
    // Si ya manda la URL completa (http...), lo dejamos tal cual.
    val rawImage = this.img ?: ""
    val finalImageUrl = if (rawImage.startsWith("http")) {
        rawImage
    } else {
        // Ajusta esta URL a tu bucket real de S3 o carpeta pública de Spring
        "https://tu-bucket-s3.amazonaws.com/imagenes/$rawImage"
    }
    return Product(
        // 1. Mapeo de campos directos
        id = this.id ?:0,
        nombre = this.nombre,
        descripcion = this.descripcionCorta ?: "Sin descripción",
        precio = this.precio.toDouble(),
        stock = this.stock,

        // 2. Campos con nombres diferentes (Backend -> Local)
        imagenUrl = finalImageUrl,

        // 3. Campos que faltan en el Backend (Usamos valores por defecto)
        marca = "Genérica",
        categoriaId = 1,

        // 4. Campos opcionales
        calificacion = 0f,
        numeroReviews = 0
    )
}