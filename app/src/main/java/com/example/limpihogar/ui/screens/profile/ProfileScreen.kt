package com.example.limpihogar.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.limpihogar.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToAdminProductList: () -> Unit, // Para "Gestionar Productos"
    onNavigateToAddProduct: () -> Unit,       // Para "Agregar Producto"
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Cerrar sesión"
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
            // --- Sección de Avatar y Nombre ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ){}

            if (currentUser != null) {
                Text(
                    text = currentUser.nombre,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentUser.email,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // --- Tarjeta de Información Personal ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileInfoItem(
                        icon = Icons.Filled.Email,
                        label = "Email",
                        value = currentUser?.email ?: "No disponible"
                    )
                    Divider()
                    ProfileInfoItem(
                        icon = Icons.Filled.Badge, // Icono para RUT
                        label = "RUT",
                        value = currentUser?.rut ?: "No disponible"
                    )
                    Divider()
                    ProfileInfoItem(
                        icon = Icons.Filled.Home, // Icono para Dirección
                        label = "Dirección",
                        value = currentUser?.direccion ?: "No disponible"
                    )
                }
            }

            // --- Tarjeta de Panel de Administrador (REQUISITO EVALUACIÓN) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant, // Un color diferente
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Panel de Administrador",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Botón para ver lista (Visual)
                    Button(
                        onClick = onNavigateToAdminProductList,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gestionar Productos (Visual)")
                    }
                    // Botón para añadir producto (Visual)
                    OutlinedButton(
                        onClick = onNavigateToAddProduct,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar Producto (Visual)")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Espacio flexible

            // --- Botón de Cerrar Sesión ---
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error // Color rojo para logout
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }

    // --- Diálogo de Confirmación de Logout ---
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Cerrar Sesión",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Estás seguro que deseas cerrar sesión?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// --- Componente de Item de Información de Perfil ---
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
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
