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
        val database = LimpioHogarDatabase.getInstance(application)
        repository = UserRepository(database.userDao())
    }

    // --- LOGIN ---
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            try {
                // El repositorio ahora usa BCrypt.checkpw
                val user = repository.login(email.trim(), password)
                if (user != null) {
                    _authState.value = _authState.value.copy(
                        isLoading = false, isLoggedIn = true, currentUser = user
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false, errorMessage = "Email o contraseña incorrectos"
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false, errorMessage = "Error al iniciar sesión: ${e.message}"
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun register(
        nombre: String,
        email: String,
        password: String,
        fechaNacimiento: String,
        rut: String,
        direccion: String
    ) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            try {
                // --- Validaciones ---
                if (!isValidEmail(email.trim())) {
                    _authState.value = _authState.value.copy(isLoading = false, errorMessage = "Email inválido")
                    return@launch
                }
                if (!isValidRut(rut.trim())) { // <-- Validación RUT
                    _authState.value = _authState.value.copy(isLoading = false, errorMessage = "RUT inválido (formato: 12345678-9)")
                    return@launch
                }
                if (direccion.trim().isEmpty()) { // <-- Validación Dirección
                    _authState.value = _authState.value.copy(isLoading = false, errorMessage = "La dirección no puede estar vacía")
                    return@launch
                }
                if (!isOver18(fechaNacimiento)) { // Ahora solo hay una función isOver18
                    _authState.value = _authState.value.copy(isLoading = false, errorMessage = "Debes ser mayor de 18 años")
                    return@launch
                }
                if (password.length < 6) {
                    _authState.value = _authState.value.copy(isLoading = false, errorMessage = "La contraseña debe tener al menos 6 caracteres")
                    return@launch
                }

                // --- Creación del User ---
                val user = User(
                    nombre = nombre.trim(),
                    email = email.trim(),
                    password = password,
                    fechaNacimiento = fechaNacimiento.trim(),
                    rut = rut.trim(),
                    direccion = direccion.trim()
                )

                // --- Llamada al Repositorio ---
                val result = repository.registerUser(user) // El repo hashea y guarda
                if (result.isSuccess) {
                    login(email, password) // Usamos el email y pass originales para el autologin
                } else {
                    // Muestra el error específico (ej: "Email ya existe")
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido al registrar"
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false, errorMessage = "Error al registrarse: ${e.message}"
                )
            }
        }
    }

    // --- LOGOUT y CLEAR ERROR ---
    fun logout() {
        _authState.value = AuthState() // Resetea el estado
    }

    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
    }

    // --- Funciones de Validación Privadas ---
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // --- Validación rut formato y calculo matematico ---
    private fun isValidRut(rut: String): Boolean {
        try {
            // 1. LIMPIEZA Y FORMATO BÁSICO
            val cleanRut = rut.replace(Regex("[.-]"), "").uppercase() // Quita puntos y guion
            if (cleanRut.length !in 8..9) return false // Comprueba el largo

            val body = cleanRut.dropLast(1).toInt() // Separa el cuerpo y lo convierte a número
            val verifier = cleanRut.last() // Obtiene el dígito verificador ingresado

            // 2. CÁLCULO MATEMÁTICO (MÓDULO 11)
            var m = 0
            var s = 1
            var t = body
            while (t != 0) {
                s = (s + t % 10 * (9 - m++ % 6)) % 11
                t /= 10
            }
            val dvCalculado = if (s > 0) (s + 47).toChar() else 'K'

            // 3. COMPARACIÓN FINAL
            return verifier == dvCalculado // Compara si el dígito ingresado es igual al calculado

        } catch (e: Exception) {
            // Si algo falla (ej. hay letras en el cuerpo del RUT), no es válido.
            return false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isOver18(birthDateString: String): Boolean {
        return try {
            // Intentar parsear con el formato DD/MM/YYYY
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(birthDateString.trim(), formatter)
            val age = Period.between(birthDate, LocalDate.now()).years
            age >= 18
        } catch (e: DateTimeParseException) {
            // Intentar parsear con formato D/M/YYYY (si el usuario pone 1/1/2000)
            try {
                val formatterShort = DateTimeFormatter.ofPattern("d/M/yyyy")
                val birthDate = LocalDate.parse(birthDateString.trim(), formatterShort)
                val age = Period.between(birthDate, LocalDate.now()).years
                age >= 18
            } catch (e2: DateTimeParseException) {
                false // Si ambos formatos fallan, no es válido
            }
        } catch (e: Exception) {
            false // Cualquier otro error
        }
    }

}
