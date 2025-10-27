package com.example.limpihogar.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Routes.HOME, "Inicio", Icons.Filled.Home)
    object Cart : BottomNavItem(Routes.CART, "Carrito", Icons.Filled.ShoppingCart)
    object Profile : BottomNavItem(Routes.PROFILE, "Perfil", Icons.Filled.Person)
}

