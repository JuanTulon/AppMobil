@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.limpihogar.ui.screens.admin
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.limpihogar.data.model.Product
import com.example.limpihogar.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun AdminDashboardScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel()
) {
    //  Leemos productos desde un Flow del ViewModel:
    //    Asegúrate de que ProductViewModel tenga: fun getAllProducts(): Flow<List<Product>>
    val productsFlow: Flow<List<Product>> = productViewModel.getAllProducts()
    val products: List<Product> by productsFlow.collectAsState(initial = emptyList())

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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: abrir formulario de alta (placeholder) */ },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Agregar producto") }
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
                text = " Bienvenido Administrador",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = { /* TODO */ }, modifier = Modifier.weight(1f)) { Text("Gestionar Productos") }
                Button(onClick = { /* TODO */ }, modifier = Modifier.weight(1f)) { Text("Gestionar Usuarios") }
                Button(onClick = { /* TODO */ }, modifier = Modifier.weight(1f)) { Text("Ver Pedidos") }
            }

            Divider()

            Text(
                text = "Productos (${products.size})",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
            )

            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos cargados.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = products,
                        key = { p -> p.id ?: p.hashCode() } // si id es nullable
                    ) { p ->
                        ProductAdminItem(
                            product = p,
                            onEdit = { /* TODO */ },
                            onDelete = { /* TODO */ }
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
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
    // Busca el drawable por nombre (ej: "quix", "clorox", "poett", sin extensión)
    val imageResId = remember(product.imagenUrl) {
        if (product.imagenUrl.isNullOrBlank()) 0
        else context.resources.getIdentifier(product.imagenUrl, "drawable", context.packageName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Imagen (con fallback si no existe el recurso)
            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = product.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin imagen", style = MaterialTheme.typography.labelMedium)
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

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
                OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) { Text("Editar") }
                OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f)) { Text("Eliminar") }
            }
        }
    }
}