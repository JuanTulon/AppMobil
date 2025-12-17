package com.example.limpihogar.data.remote.dto

data class UsuarioDto(
    val id: Int?, // Puede ser null al registrarse
    val nombre: String,
    val email: String,
    val password: String?, // Se envía al login, pero quizás quieras ocultarlo al recibirlo
    val rut: String?,
    val region: String?,
    val comuna: String?,
    val rol: RolDto? // Objeto anidado
)

data class RolDto(
    val id: Int,
    val nombreRol: String?
)