package com.example.limpihogar.data.repository


import com.example.limpihogar.data.database.UserDao
import com.example.limpihogar.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    fun getUserById(userId: Int): Flow<User?> = userDao.getUserById(userId)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun login(email: String, password: String): User? = userDao.login(email, password)

    suspend fun registerUser(user: User): Result<Long> {
        return try {
            // Verificar si el email ya existe
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                Result.failure(Exception("El email ya está registrado"))
            } else {
                val userId = userDao.insertUser(user)
                Result.success(userId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: User) = userDao.updateUser(user)
}

