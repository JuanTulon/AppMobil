package com.example.limpihogar.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import kotlin.math.ceil

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
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = context.resources.getIdentifier(product!!.imagenUrl, "drawable", context.packageName),
                    contentDescription = product!!.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = product!!.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Marca: ${product!!.marca}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    product!!.formato?.let {
                        Text(
                            text = "Formato: $it",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // --- Recomendación de compra dinámica ---
                    var peopleCount by remember { mutableStateOf("4") }
                    val recommendation = getRecommendationForProduct(product!!.nombre, peopleCount.toIntOrNull() ?: 0)

                    if (recommendation != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Lightbulb,
                                        contentDescription = "Recomendación",
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Recomendación de Consumo",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Para", color = MaterialTheme.colorScheme.onTertiaryContainer)
                                    OutlinedTextField(
                                        value = peopleCount,
                                        onValueChange = { value ->
                                            if (value.all { it.isDigit() } && value.length <= 2) {
                                                peopleCount = value
                                            }
                                        },
                                        modifier = Modifier.width(80.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                                    )
                                    Text("persona(s), se sugiere:", color = MaterialTheme.colorScheme.onTertiaryContainer)
                                }

                                if (peopleCount.isNotBlank() && (peopleCount.toIntOrNull() ?: 0) > 0) {
                                    Text(
                                        text = recommendation,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = if (index < product!!.calificacion.toInt()) Color(0xFFFFA500) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${product!!.calificacion} (${product!!.numeroReviews} reseñas)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (product!!.precioAnterior != null) {
                            Text(
                                text = "Antes: ${formatPrice(product!!.precioAnterior!!)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                            val descuento = ((product!!.precioAnterior!! - product!!.precio) /
                                    product!!.precioAnterior!! * 100).toInt()
                            Text(
                                text = "¡$descuento% de descuento!",
                                color = Color(0xFF28A745),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = formatPrice(product!!.precio),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val stockColor = if (product!!.stock > 0) Color(0xFF28A745) else MaterialTheme.colorScheme.error
                    Text(
                        text = if (product!!.stock > 0) "Stock disponible: ${product!!.stock} unidades" else "Producto agotado",
                        color = stockColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = product!!.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

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
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (product!!.stock > 0) "Agregar al Carrito" else "Sin Stock",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (showAddedToCartMessage) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showAddedToCartMessage = false
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(bottom = (56.dp + 16.dp + 8.dp)),
                    contentAlignment = Alignment.BottomCenter
                )  {
                    Snackbar(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        containerColor = Color(0xFF28A745),
                        contentColor = Color.White
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

private fun getRecommendationForProduct(productName: String, people: Int): String? {
    if (people <= 0) {
        return "Ingresa un número de personas válido."
    }

    // 🔹 Función interna para formatear la recomendación
    fun formatRecommendation(baseQuantityForFourPeople: Double, unit: String, productFormat: String, frequency: String): String {
        val calculatedQuantity = (baseQuantityForFourPeople / 4.0) * people
        // Redondea hacia arriba y asegura que el mínimo sea 1
        val finalQuantity = maxOf(1, ceil(calculatedQuantity).toInt())
        // Maneja el plural
        val pluralizedUnit = if (finalQuantity == 1) unit else "${unit}s"

        return "$finalQuantity $pluralizedUnit de $productFormat $frequency"
    }

    return when {
        productName.contains("Detergente", ignoreCase = true) -> formatRecommendation(1.0, "botella", "3L", "al mes.")
        productName.contains("Lavaloza", ignoreCase = true) -> formatRecommendation(2.0, "botella", "500ml", "al mes.")
        productName.contains("Limpiador de Pisos", ignoreCase = true) -> "1 botella de 1L al mes (no depende del n° de personas)."
        productName.contains("Cloro", ignoreCase = true) -> formatRecommendation(1.0, "botella", "2L", "al mes.")
        productName.contains("Limpiador Multiuso", ignoreCase = true) -> formatRecommendation(2.0, "botella", "1L", "al mes.")
        else -> null
    }
}

private fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    return format.format(price)
}
