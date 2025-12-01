package com.example.limpihogar.data.remote.retrofitClient

import com.example.limpihogar.data.remote.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Si usas emulador: "http://10.0.2.2:8080/"
    // Si usas celular físico: La IP de tu PC "http://192.168.1.X:8080/"
    // Opción B (AWS):   "http://tu-instancia-ec2.us-east-1.compute.amazonaws.com:8080/" o tu IP pública
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}