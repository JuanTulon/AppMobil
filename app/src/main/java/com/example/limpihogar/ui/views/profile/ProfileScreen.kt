package com.example.limpihogar.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.limpihogar.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val currentUser = authState.currentUser
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Color primario
                    titleContentColor = MaterialTheme.colorScheme.onPrimary, // Texto blanco
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary // Icono blanco
                ),
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Cerrar sesión"
                            // El color lo toma del actionIconContentColor
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp)) // Pequeño espacio arriba
            // ---Nombre ---
            if (currentUser != null) {
                Text(
                    text = currentUser.nombre,
                    style = MaterialTheme.typography.headlineSmall, // Tamaño nombre
                    color = MaterialTheme.colorScheme.onBackground, // Color texto principal
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentUser.email,
                    style = MaterialTheme.typography.bodyLarge, // Tamaño email
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Color texto secundario
                )
            } else {
                // Placeholder si el usuario no carga
                Text("Cargando información...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // --- Tarjeta de Información Personal ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp), // Bordes redondeados
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface, // Color superficie (blanco/gris claro)
                    contentColor = MaterialTheme.colorScheme.onSurface // Color contenido (oscuro)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Sombra sutil
            ) {
                Column(
                    modifier = Modifier.padding(20.dp), // Padding interno
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre items
                ) {
                    Text( // Título opcional para la tarjeta
                        text = "Información Personal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary // Título con color primario
                    )
                    Divider() // Separador
                    ProfileInfoItem(
                        icon = Icons.Filled.Email,
                        label = "Email",
                        value = currentUser?.email ?: "No disponible"
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)) // Separador más sutil
                    ProfileInfoItem(
                        icon = Icons.Filled.Badge, // Icono para RUT
                        label = "RUT",
                        value = currentUser?.rut ?: "No ingresado"
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    ProfileInfoItem(
                        icon = Icons.Filled.Home, // Icono para Dirección
                        label = "Dirección",
                        value = currentUser?.direccion ?: "No ingresada"
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    ProfileInfoItem(
                        icon = Icons.Filled.Cake, // Icono para Cumpleaños
                        label = "Fecha de Nacimiento",
                        value = currentUser?.fechaNacimiento ?: "No ingresada"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón de logout hacia abajo

            // --- Botón de Cerrar Sesión ---
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error, // Color de error del tema
                    contentColor = MaterialTheme.colorScheme.onError // Color del contenido sobre error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }
        } // Fin Column principal
    } // Fin Scaffold

    // --- Diálogo de Confirmación de Logout ---
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        onLogout() // Llama a la navegación de vuelta al login
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error // Botón rojo
                    )
                ) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar") // Usa el color primario por defecto
                }
            }
            // Los colores del AlertDialog los toma del tema
        )
    }
}

// --- Componente de Item de Información de Perfil (Sin cambios visuales necesarios) ---
@Composable
private fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary, // Icono con color primario
            modifier = Modifier.size(24.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp) // Menos espacio entre label y value
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium, // Estilo para etiquetas
                color = MaterialTheme.colorScheme.onSurfaceVariant // Color secundario
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge, // Estilo para el valor
                color = MaterialTheme.colorScheme.onSurface, // Color principal
                fontWeight = FontWeight.Medium
            )
        }
    }
}
