package com.example.limpihogar.ui.screens.chekout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.limpihogar.ui.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ChekoutSuccessScreen(
    onNavigateToHome: () -> Unit,
    cartViewModel: CartViewModel = viewModel()
) {
    // Obtener datos del ViewModel del carrito
    val purchasedItems by cartViewModel.cartItems.collectAsState()
    val subtotalState by cartViewModel.cartTotal.collectAsState()

    // Calcular subtotal, IVA y total
    val subtotal = subtotalState ?: 0.0
    val iva = subtotal * 0.19
    val total = subtotal + iva

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mensaje de éxito
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Éxito",
            tint = Color(0xFF4CAF50), // Verde
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Compra realizada con éxito",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tu pedido está siendo procesado.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Detalles del pedido
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Resumen del Pedido",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Divider()

                // Lista de productos comprados
                purchasedItems.forEach { item ->
                    ProductoResumenRow(
                        nombre = item.nombre,
                        cantidad = item.cantidad,
                        precioTotal = item.precio * item.cantidad
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Resumen de costos
                DetallePedidoRow("Subtotal:", formatPrice(subtotal))
                DetallePedidoRow("IVA (19%):", formatPrice(iva))
                DetallePedidoRow("Total:", formatPrice(total), isTotal = true)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Opciones para continuar
        Button(
            onClick = {
                cartViewModel.clearCart() // Limpiar carrito antes de navegar
                onNavigateToHome()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Seguir comprando")
        }
        TextButton(onClick = {
            cartViewModel.clearCart() // Limpiar carrito antes de navegar
            onNavigateToHome()
        }) {
            Text("Volver al inicio")
        }
    }
}

@Composable
private fun ProductoResumenRow(nombre: String, cantidad: Int, precioTotal: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$nombre (x$cantidad)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = formatPrice(precioTotal),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DetallePedidoRow(label: String, value: String, isTotal: Boolean = false) {
    val textStyle = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
    val textColor = if (isTotal) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    val valueColor = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = textStyle,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
        Text(
            text = value,
            style = textStyle,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

private fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    return format.format(price)
}
