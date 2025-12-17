package com.example.limpihogar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val fechaNacimiento: String,
    val rut: String? = null,      // <-- AÑADIDO
    val direccion: String? = null, // <-- AÑADIDO
    val role: String = "user",
    val fechaRegistro: Long = System.currentTimeMillis()
)

