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
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var showAddedToCartMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Para cargar la imagen

    LaunchedEffect(productId) {
        product = productViewModel.getProductById(productId)
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
                )
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
                        color = MaterialTheme.colorScheme.onBackground, // Texto oscuro
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Marca: ${product!!.marca}", // Campo actualizado
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Texto gris
                        fontSize = 16.sp
                    )

                    product!!.formato?.let {
                        Text(
                            text = "Formato: $it", // Campo nuevo
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                    }

                    // --- Calificación (ACTUALIZADO) ---
                    // Requerido por la evaluación ("opiniones/valoraciones")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        Text(
                            text = "${product!!.calificacion} (${product!!.numeroReviews} reseñas)",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // --- Precio (ACTUALIZADO) ---
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (product!!.precioAnterior != null) {
                            Text(
                                text = "Antes: ${formatPrice(product!!.precioAnterior!!)}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 16.sp,
                                textDecoration = TextDecoration.LineThrough
                            )
                            val descuento = ((product!!.precioAnterior!! - product!!.precio) /
                                    product!!.precioAnterior!! * 100).toInt()
                            Text(
                                text = "¡$descuento% de descuento!",
                                color = Color(0xFF28A745), // Un verde limpio
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = formatPrice(product!!.precio),
                            color = MaterialTheme.colorScheme.primary, // Color del tema
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // --- Stock (ACTUALIZADO) ---
                    Text(
                        text = if (product!!.stock > 0)
                            "Stock disponible: ${product!!.stock} unidades"
                        else "Producto agotado",
                        color = if (product!!.stock > 0) Color(0xFF28A745) else MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // --- Descripción (ACTUALIZADO) ---
                    // Requerido por la evaluación ("descripción extendida")
                    Text(
                        text = "Descripción",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = product!!.descripcion,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
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
                        containerColor = MaterialTheme.colorScheme.primary, // Color del tema
                        disabledContainerColor = MaterialTheme.colorScheme.outline
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
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp,
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
                        .padding(bottom = 90.dp), // Justo encima del botón
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Surface(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF28A745), // Verde limpio
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            text = "✓ Producto agregado al carrito",
                            modifier = Modifier.padding(16.dp),
                            color = Color.White, // Texto blanco
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