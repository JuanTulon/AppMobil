package com.example.limpihogar.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.limpihogar.data.model.Product
import com.example.limpihogar.navigation.Routes
import com.example.limpihogar.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    // IMPORTANTE: Usamos la Factory para que no se cierre la app
    viewModel: ProductViewModel = viewModel(factory = ProductViewModel.Factory)
) {
    // Usamos el uiState que ya tiene la lógica de carga y lista actualizada
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bienvenido Administrador",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider()

            Text(
                text = "Productos (${uiState.products.size})",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.products.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No hay productos cargados.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(
                        items = uiState.products,
                        key = { p -> p.id }
                    ) { p ->
                        ProductAdminItem(
                            product = p,
                            onEdit = { /* Navegar a editar si lo implementas */ },
                            onDelete = {
                                // QUÍ ESTÁ LA FUNCIONALIDAD DE ELIMINAR
                                viewModel.deleteProduct(p)
                            }
                        )
                    }
                }
            }

            // 🔹 Botón AGREGAR PRODUCTO (Funcional)
            Button(
                onClick = { navController.navigate(Routes.ADMIN_ADD_PRODUCT) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar producto")
            }

            OutlinedButton(
                onClick = {
                    // Cierra sesión y vuelve al login limpiando el historial
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) { Text("Cerrar Sesión") }
        }
    }
}

@Composable
private fun ProductAdminItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    //Limpiamos el nombre antes de buscar el ID
    val model = remember(product.imagenUrl) {
        if (product.imagenUrl.startsWith("http")) {
            product.imagenUrl // Es URL de internet
        } else {
            // Es local: Quitamos extensión y pasamos a minúsculas
            val nombreLimpio = product.imagenUrl
                .lowercase()
                .trim()
                .substringBefore(".")

            val resId = context.resources.getIdentifier(
                nombreLimpio,
                "drawable",
                context.packageName
            )
            // Si no existe, usa icono por defecto para no crashear
            if (resId != 0) resId else android.R.drawable.ic_menu_gallery
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Usamos AsyncImage con el modelo corregido
            AsyncImage(
                model = model,
                contentDescription = product.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
                error = painterResource(android.R.drawable.ic_menu_report_image)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = product.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Marca: ${product.marca ?: "-"}  •  Formato: ${product.formato ?: "-"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Precio: $${product.precio.toInt()}  •  Stock: ${product.stock}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Editar (Visual por ahora)
                OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) { Text("Editar") }

                // Botón Eliminar (Funcional y rojo para destacar)
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}