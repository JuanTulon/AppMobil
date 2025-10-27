package com.example.limpihogar.ui.screens.auth


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

// 1. Paleta de colores "limpia"
val colorPrimario = Color(0xFF007BFF) // Un azul limpio y corporativo
val colorSecundario = Color(0xFF28A745) // Un verde fresco
val colorFondo = Color.White
val colorTexto = Color.Black
val colorTextoSecundario = Color.Gray

// 2. Quitamos la fuente personalizada "Orbitron"

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    // 3. Cambiamos el fondo a Blanco
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo) // Antes Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 4. Cambiamos el icono y el branding
            Text(
                text = "🧼", // Icono de limpieza
                fontSize = 80.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "LimpioHogar", // Nuevo nombre
                // fontFamily = orbitronFont, // Quitamos fuente gamer
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = colorTexto // Antes Color.White
            )
            Text(
                text = "Tu aliado en la limpieza", // Nuevo eslogan
                color = colorPrimario, // Usamos el nuevo color
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 5. Ajustamos los colores de los OutlinedTextField para tema claro
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearError()
                },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        tint = colorPrimario
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorTexto,
                    unfocusedTextColor = colorTexto,
                    focusedBorderColor = colorPrimario,
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = colorPrimario,
                    unfocusedLabelColor = colorTextoSecundario,
                    cursorColor = colorPrimario
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.clearError()
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorTexto,
                    unfocusedTextColor = colorTexto,
                    focusedBorderColor = colorPrimario,
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = colorPrimario,
                    unfocusedLabelColor = colorTextoSecundario,
                    cursorColor = colorPrimario
                ),
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible)
                                "Ocultar contraseña" else "Mostrar contraseña",
                            tint = colorTextoSecundario
                        )
                    }
                }
            )

            if (authState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = authState.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 6. Ajustamos los botones
            Button(
                onClick = { viewModel.login(email, password) },
                enabled = !authState.isLoading && email.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorPrimario, // Nuevo color
                    disabledContainerColor = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (authState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White, // El spinner sí puede ser blanco
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Iniciar Sesión",
                        color = Color.White,
                        fontSize = 18.sp,
                        // fontFamily = orbitronFont, // Quitamos fuente
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    color = colorSecundario, // Nuevo color
                    fontSize = 14.sp
                )
            }
        }
    }
}
