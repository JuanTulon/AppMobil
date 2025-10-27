package com.example.limpihogar.ui.screens.chekout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.room.util.TableInfo

@Composable
fun ChekoutSuccessScreen(
    onNavigateToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
) {
        //msg de exito
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Exito",
            tint = Color.Green,
            modifier = Modifier.size(100.dp)
            )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Compra realizada con exito",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "tu pedido se ha recibido y esta siendo procesado",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        //detalles del pedido
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp)
        ){
            Column (
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
            Text(
                text = "resumen del pedido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Divider()
            DetallePedidoRow("Número de Orden:", "#LP-184592")
            DetallePedidoRow("Fecha:", "27/10/2025")
            DetallePedidoRow("Total:", "$19.990") // Simulado
            DetallePedidoRow("Método de Pago:", "Tarjeta de Crédito")
        }
    }
    Spacer(modifier = Modifier.height(32.dp))

        //opciones para continuar
        Button(
            onClick = onNavigateToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Seguir comprando")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onNavigateToHome) {
            Text("Volver al inicio")
        }
    }
}

@Composable
private fun DetallePedidoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}








