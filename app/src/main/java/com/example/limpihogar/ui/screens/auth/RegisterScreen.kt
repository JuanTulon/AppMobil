package com.example.limpihogar.ui.screens.auth


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.limpihogar.ui.viewmodel.AuthViewModel
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.OffsetMapping

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val input = text.text.filter { it.isDigit() }.take(8)
        val output = buildString {
            input.forEachIndexed { index, char ->
                append(char)
                if ((index == 1 || index == 3) && index < input.lastIndex) {
                    append('/')
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset > 3 -> offset + 2
                    offset > 1 -> offset + 1
                    else -> offset
                }.coerceAtMost(output.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset > 5 -> offset - 2
                    offset > 2 -> offset - 1
                    else -> offset
                }.coerceAtMost(input.length)
            }
        }
        return TransformedText(AnnotatedString(output), offsetMapping)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }


    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            onRegisterSuccess()
        }
    }
    // 3. Cambiamos el fondo a Blanco
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Antes Color.Black
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadow(elevation = 4.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🧼",
                fontSize = 60.sp
            )

            Text(
                text = "Únete a LimpioHogar",
                style = MaterialTheme.typography.headlineSmall, // Usar tipografía del tema
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground, // Color de texto principal
                textAlign = TextAlign.Center
            )

            Text(
                text = "Debes ser mayor de 18 años para registrarte",
                style = MaterialTheme.typography.bodySmall, // Texto más pequeño
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Color secundario
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {nombre = it; viewModel.clearError() },
                label = { Text("Nombre completo") },
                leadingIcon = {Icon(Icons.Filled.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearError()
                },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email, null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            // Campo RUT
            OutlinedTextField(
                value = rut,
                onValueChange = {
                    rut = it
                    viewModel.clearError() },
                label = { Text("RUT (Ej: 12345678-9)") },
                leadingIcon = { Icon(Icons.Filled.Badge, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),)

            // Campo Dirección
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it; viewModel.clearError() },
                label = { Text("Dirección") },
                leadingIcon = { Icon(Icons.Filled.Home, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            // Fecha Nacimiento
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = {
                    if (it.length <= 8) {
                        fechaNacimiento = it.filter { char -> char.isDigit() }
                        viewModel.clearError()
                    }
                },
                label = { Text("Fecha nacimiento (DD/MM/YYYY)") },
                leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                visualTransformation = DateVisualTransformation()
            )

            // Campo Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.clearError()
                },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null)
                    }
                }
            )

            // Campo Confirmar Contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    viewModel.clearError()
                },
                label = { Text("Confirmar contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null)
                    }
                },
                isError = confirmPassword.isNotBlank() && password != confirmPassword
            )

            if (confirmPassword.isNotBlank() && password != confirmPassword) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Mensaje de error general
            if (authState.errorMessage != null) {
                Text(
                    text = authState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de registro
            Button(
                onClick = {
                    if (password == confirmPassword) {
                        // Pasamos los nuevos campos al ViewModel
                        // Formateamos la fecha aquí antes de enviarla al ViewModel
                        val formattedDate = buildString {
                            fechaNacimiento.forEachIndexed { index, c ->
                                append(c)
                                if ((index == 1 || index == 3) && index < fechaNacimiento.lastIndex) {
                                    append('/')
                                }
                            }
                        }
                        viewModel.register(nombre, email, password, formattedDate, rut, direccion)                    }
                },
                // Habilitar solo si todos los campos están llenos y contraseñas coinciden
                enabled = !authState.isLoading &&
                        nombre.isNotBlank() &&
                        email.isNotBlank() &&
                        password.isNotBlank() &&
                        confirmPassword.isNotBlank() &&
                        fechaNacimiento.isNotBlank() &&
                        rut.isNotBlank() &&             // <-- Validación RUT
                        direccion.isNotBlank() &&       // <-- Validación Dirección
                        password == confirmPassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (authState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Registrarse", fontSize = 18.sp)
                }
            }

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ya tengo una cuenta",
                    color = MaterialTheme.colorScheme.secondary, // Color secundario
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
