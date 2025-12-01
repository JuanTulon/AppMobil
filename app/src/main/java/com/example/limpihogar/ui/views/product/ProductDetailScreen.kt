package com.example.limpihogar.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.limpihogar.data.model.Product
import com.example.limpihogar.ui.viewmodel.CartViewModel
import com.example.limpihogar.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    onNavigateBack: () -> Unit,
    viewModel: ProductViewModel = viewModel(factory = ProductViewModel.Factory),
    cartViewModel: CartViewModel = viewModel()
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var showAddedToCartMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Para cargar la imagen

    LaunchedEffect(productId) {
        product = viewModel.getProductById(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                // Colores del tema claro
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadow(elevation = 4.dp)
            )
        },
        // Fondo claro
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (product == null) {
            // Estado de carga
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            // Contenido del producto
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- Imagen del producto (ACTUALIZADO) ---
                AsyncImage(
                    model = context.resources.getIdentifier(product!!.imagenUrl, "drawable", context.packageName),
                    contentDescription = product!!.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant), // Fondo claro
                    contentScale = ContentScale.Crop
                )

                // Contenido
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp), // Más padding para un look más limpio
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- Nombre y Marca (ACTUALIZADO) ---
                    Text(
                        text = product!!.nombre,
                        style = MaterialTheme.typography.headlineMedium, // Tamaño de título
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Marca: ${product!!.marca}", // Campo actualizado
                        style = MaterialTheme.typography.titleMedium, // Un poco más grande
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Color secundario
                    )

                    product!!.formato?.let {
                        Text(
                            text = "Formato: $it", // Campo nuevo
                            style = MaterialTheme.typography.titleMedium, // Un poco más grande
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Color secundario
                        )
                    }

                    // --- Calificación (ACTUALIZADO) ---
                    // Requerido por la evaluación ("opiniones/valoraciones")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = if (index < product!!.calificacion.toInt())
                                        Color(0xFFFFA500) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${product!!.calificacion} (${product!!.numeroReviews} reseñas)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Color secundario
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // --- Precio (ACTUALIZADO) ---
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (product!!.precioAnterior != null) {
                            Text(
                                text = "Antes: ${formatPrice(product!!.precioAnterior!!)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, // Color secundario
                                textDecoration = TextDecoration.LineThrough
                            )
                            val descuento = ((product!!.precioAnterior!! - product!!.precio) /
                                    product!!.precioAnterior!! * 100).toInt()
                            Text(
                                text = "¡$descuento% de descuento!",
                                color = Color(0xFF28A745), // Un verde limpio
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = formatPrice(product!!.precio),
                            style = MaterialTheme.typography.headlineMedium, // Precio destacado
                            color = MaterialTheme.colorScheme.primary, // Color primario
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Stock
                    val stockColor = if (product!!.stock > 0) Color(0xFF28A745) else MaterialTheme.colorScheme.error
                    Text(
                        text = if (product!!.stock > 0) "Stock disponible: ${product!!.stock} unidades" else "Producto agotado",
                        color = stockColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // --- Descripción (ACTUALIZADO) ---
                    // Requerido por la evaluación ("descripción extendida")
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium, // Título para la sección
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = product!!.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Texto de descripción
                        lineHeight = 20.sp
                    )

                    // Espacio para que el botón flotante no tape el texto
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // --- Botón Agregar al Carrito (Flotante) (ACTUALIZADO) ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            if (product!!.stock > 0) {
                                cartViewModel.addProductToCart(product!!)
                                showAddedToCartMessage = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
                    enabled = product!!.stock > 0,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary // Texto/icono blanco
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (product!!.stock > 0) "Agregar al Carrito" else "Sin Stock",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- Snackbar para confirmar (ACTUALIZADO) ---
            if (showAddedToCartMessage) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showAddedToCartMessage = false
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(bottom = (56.dp + 16.dp + 8.dp)), // Justo encima del botón
                    contentAlignment = Alignment.BottomCenter
                )  {
                    Snackbar( // Usamos Snackbar en lugar de Surface para mejor estilo
                        modifier = Modifier.padding(horizontal = 16.dp),
                        containerColor = Color(0xFF28A745), // Verde éxito
                        contentColor = Color.White // Texto blanco
                    ) {
                        Text(
                            text = "✓ Producto agregado al carrito",
                            fontWeight = FontWeight.Bold
                        )
                    }
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