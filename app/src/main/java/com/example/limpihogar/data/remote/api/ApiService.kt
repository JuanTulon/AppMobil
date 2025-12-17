package com.example.limpihogar.data.remote.api

import com.example.limpihogar.data.remote.dto.ProductoDto
import com.example.limpihogar.data.remote.dto.UsuarioDto
import com.example.limpihogar.data.remote.dto.BoletaDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==========================================
    // AUTH (Usuarios)
    // ==========================================
    @POST("api/auth/login")
    suspend fun login(@Body request: UsuarioDto): Response<UsuarioDto>

    @POST("api/auth/registro")
    suspend fun registrar(@Body usuario: UsuarioDto): Response<UsuarioDto>


    // ==========================================
    // PRODUCTOS (Catálogo y BackOffice)
    // ==========================================

    @GET("api/productos")
    suspend fun obtenerProductos(): Response<List<ProductoDto>>

    @GET("api/productos/ofertas")
    suspend fun obtenerOfertas(): Response<List<ProductoDto>>

    @GET("api/productos/{id}")
    suspend fun obtenerProductoPorId(@Path("id") id: Int): Response<ProductoDto>

    // ✅ NUEVO: Método para CREAR un producto (BackOffice)
    @POST("api/productos")
    suspend fun crear(@Body producto: ProductoDto): Response<ProductoDto>

    // ✅ NUEVO: Método para ELIMINAR un producto (BackOffice)
    @DELETE("api/productos/{id}")
    suspend fun eliminar(@Path("id") id: Int): Response<Void>

    // ✅ NUEVO: Método para SUBIR IMAGEN (BackOffice)
    @Multipart
    @POST("api/productos/upload")
    suspend fun subirImagen(@Part imagen: MultipartBody.Part): Response<ResponseBody>


    // ==========================================
    // BOLETAS (Carrito)
    // ==========================================

    @POST("api/boletas")
    suspend fun crearBoleta(@Body boleta: BoletaDto): Response<BoletaDto>

    @GET("api/boletas/{id}")
    suspend fun obtenerBoletaPorId(@Path("id") id: Int): Response<BoletaDto>
}