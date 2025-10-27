package com.example.limpihogar.ui.screens.catalog


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.limpihogar.data.model.Category
import com.example.limpihogar.data.model.Product
import com.example.limpihogar.ui.viewmodel.ProductViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CatalogScreen(
    onProductClick: (Int) -> Unit,
    viewModel: ProductViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it; viewModel.searchProducts(it) },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Buscar productos...") },
            leadingIcon = { Icon(Icons.Filled.Search, "Buscar") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Filtro de categorías
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryChip(
                    text = "Todos",
                    icon = "🧼",
                    isSelected = uiState.selectedCategory == null,
                    onClick = { viewModel.selectCategory(null) }
                )
            }
            items(uiState.categories) { category ->
                CategoryChip(
                    text = category.nombre,
                    icon = category.icono, // El icono que definiste en la DB
                    isSelected = uiState.selectedCategory?.id == category.id,
                    onClick = { viewModel.selectCategory(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Lista de productos
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1E90FF))
            }
        } else if (uiState.products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No se encontraron productos",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 18.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.products) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isSelected) 2.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = icon, fontSize = 18.sp)
            Text(
                text = text,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Imagen del producto (ACTUALIZADO) ---
            // Carga la imagen desde res/drawable usando el 'imagenUrl' (ej: "quix", "cif")
            AsyncImage(
                model = context.resources.getIdentifier(product.imagenUrl, "drawable", context.packageName),
                contentDescription = product.nombre,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant), // Fondo claro
                contentScale = ContentScale.Crop
            )

            // --- Información del producto  ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.nombre,
                    color = MaterialTheme.colorScheme.onSurface, // Texto oscuro
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.marca,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Texto gris
                    fontSize = 12.sp
                )

                product.formato?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Texto gris
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Calificación
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFA500),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${product.calificacion}",
                        color = MaterialTheme.colorScheme.onSurface, // Texto oscuro
                        fontSize = 12.sp
                    )
                    Text(
                        text = "(${product.numeroReviews} reseñas)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Texto gris
                        fontSize = 12.sp
                    )
                }

                // Precio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = formatPrice(product.precio),
                            color = MaterialTheme.colorScheme.primary, // Color primario del tema
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (product.precioAnterior != null) {
                            Text(
                                text = formatPrice(product.precioAnterior),
                                color = MaterialTheme.colorScheme.onSurfaceVariant, // Texto gris
                                fontSize = 12.sp,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    // Stock (ACTUALIZADO)
                    Text(
                        text = if (product.stock > 0) "Stock: ${product.stock}" else "Sin stock",
                        color = if (product.stock > 0) androidx.compose.ui.graphics.Color(0xFF28A745) else MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// Función helper para formatear el precio
private fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    return format.format(price)
}
