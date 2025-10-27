package com.example.limpihogar.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.limpihogar.data.model.CartItem
import com.example.limpihogar.ui.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartScreen(
    onNavigateToCheckoutSuccess: () -> Unit,
    onNavigateToCheckoutFailed: () -> Unit,
    viewModel: CartViewModel = viewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val cartTotal by viewModel.cartTotal.collectAsState()

    // Este estado simulará un error. En una app real, vendría de un ViewModel.
    var simularError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Encabezado
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "Mi Carrito",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${cartItems.size} productos",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (cartItems.isEmpty()) {
            // Carrito vacío
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "🛒", fontSize = 80.sp)
                    Text(
                        text = "Tu carrito está vacío",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Explora nuestro catálogo y agrega productos",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Lista de productos
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems, key = { it.id }) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onQuantityChange = { newQuantity ->
                            viewModel.updateQuantity(cartItem, newQuantity)
                        },
                        onRemove = {
                            viewModel.removeFromCart(cartItem)
                        }
                    )
                }
            }

            // Resumen y botón de compra
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Switch para simular error
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Simular error de pago", color = MaterialTheme.colorScheme.onSurface)
                        Switch(
                            checked = simularError,
                            onCheckedChange = { simularError = it }
                        )
                    }

                    // Resumen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatPrice(cartTotal ?: 0.0), color = MaterialTheme.colorScheme.onSurface)
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total", color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(formatPrice(cartTotal ?: 0.0), color = MaterialTheme.colorScheme.primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    // Botón de compra
                    Button(
                        onClick = {
                            if (simularError) {
                                onNavigateToCheckoutFailed()
                            } else {
                                // En una app real, aquí también limpiarías el carrito
                                // viewModel.clearCart()
                                onNavigateToCheckoutSuccess()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Finalizar Compra", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagen (Usando emoji como placeholder)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧼", fontSize = 32.sp)
            }

            // Información
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(cartItem.nombre, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text(formatPrice(cartItem.precio), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Controles de cantidad
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onQuantityChange(cartItem.cantidad - 1) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.Remove, "Disminuir", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("${cartItem.cantidad}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { onQuantityChange(cartItem.cantidad + 1) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.Add, "Aumentar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Botón eliminar
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    return format.format(price)
}