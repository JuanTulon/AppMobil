package com.example.limpihogar.ui.screens.chekout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CheckoutFailedScreen(
    onNavigateBackToCart: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        //msg error
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "hubo un problema",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "no se puede procesar el pago, verifica que este todo correcto",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))

        //opciones para corregir
        Button(
            onClick = onNavigateBackToCart,
            modifier = Modifier.fillMaxWidth()
                .height(50.dp)
        ) {
            Text("revisar mi carrito")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = {/*aca iria la logica para cambiar el metodo de pago*/},
            modifier = Modifier.fillMaxWidth()
                .height(50.dp)
        ){
            Text("cambiar metodo de pago")
        }



    }
}









