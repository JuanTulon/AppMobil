package com.example.limpihogar.ui.viewmodel

import android.app.Application
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.limpihogar.data.database.LimpioHogarDatabase
import com.example.limpihogar.data.model.User
import com.example.limpihogar.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val errorMessage: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        val database = LimpioHogarDatabase.getDatabase(application)
        repository = UserRepository(database.userDao())
    }

    // --- LOGIN ---
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            try {
                val user = repository.login(email.trim(), password)
                if (user != null) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = "Email o contraseña incorrectos"
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al iniciar sesión: ${e.message}"
                )
            }
        }
    }

    // --- REGISTER ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun register(
        nombre: String,
        email: String,
        password: String,
        fechaNacimiento: String,
        rut: String,
        direccion: String,
        role: String = "user" // 🔹 nuevo parámetro con valor por defecto
    ) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            try {
                // --- Validaciones ---
                if (!isValidEmail(email.trim())) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = "Email inválido"
                    )
                    return@launch
                }
                if (!isValidRut(rut.trim())) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = "RUT inválido (formato: 12345678-9)"
                    )
                    return@launch
                }
                if (direccion.trim().isEmpty()) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = "La dirección no puede estar vacía"
                    )
                    return@launch
                }
                if (!isOver18(fechaNacimiento)) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = "Debes ser mayor de 18 años"
                    )
                    return@launch
                }
                if (password.length < 6) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = "La contraseña debe tener al menos 6 caracteres"
                    )
                    return@launch
                }

                // --- Crear el usuario ---
                val user = User(
                    nombre = nombre.trim(),
                    email = email.trim(),
                    password = password,
                    fechaNacimiento = fechaNacimiento.trim(),
                    rut = rut.trim(),
                    direccion = direccion.trim(),
                    role = role // 🔹 se guarda el rol (admin o user)
                )

                // --- Guardar en la base de datos ---
                val result = repository.registerUser(user)
                if (result.isSuccess) {
                    login(email, password)
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Error desconocido al registrar"
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al registrarse: ${e.message}"
                )
            }
        }
    }

    // --- LOGOUT ---
    fun logout() {
        _authState.value = AuthState()
    }

    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
    }

    // --- VALIDACIONES ---
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidRut(rut: String): Boolean {
        try {
            val cleanRut = rut.replace(Regex("[.-]"), "").uppercase()
            if (cleanRut.length !in 8..9) return false

            val body = cleanRut.dropLast(1).toInt()
            val verifier = cleanRut.last()
            var m = 0
            var s = 1
            var t = body
            while (t != 0) {
                s = (s + t % 10 * (9 - m++ % 6)) % 11
                t /= 10
            }
            val dvCalculado = if (s > 0) (s + 47).toChar() else 'K'
            return verifier == dvCalculado
        } catch (e: Exception) {
            return false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isOver18(birthDateString: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(birthDateString.trim(), formatter)
            Period.between(birthDate, LocalDate.now()).years >= 18
        } catch (e: DateTimeParseException) {
            try {
                val formatterShort = DateTimeFormatter.ofPattern("d/M/yyyy")
                val birthDate = LocalDate.parse(birthDateString.trim(), formatterShort)
                Period.between(birthDate, LocalDate.now()).years >= 18
            } catch (_: DateTimeParseException) {
                false
            }
        } catch (_: Exception) {
            false
        }
    }
}
