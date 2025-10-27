package com.example.limpihogar


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.limpihogar.navigation.BottomNavItem
import com.example.limpihogar.navigation.NavGraph
import com.example.limpihogar.navigation.Routes
import com.example.limpihogar.ui.theme.LimpioHogarTheme //

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Usa el tema de tu proyecto "MovilPrueba" que ya definiste
            LimpioHogarTheme {
                MainApp()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Rutas que NO deben mostrar el BottomBar (Login, Registro, etc.)
    val noBottomBarRoutes = listOf(
        Routes.LOGIN,
        Routes.REGISTER
        // Añadiremos aquí las rutas de Checkout y Admin cuando las creemos
    )
    // Mostramos la barra si la ruta actual NO está en la lista anterior
    val shouldShowBottomBar = currentDestination?.route !in noBottomBarRoutes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Color de fondo claro
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant, // Color de tema claro
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    // --- ACTUALIZADO ---
                    // Lista de items (ahora sin Perfil)
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Cart,
                        BottomNavItem.Profile
                    )

                    items.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            // Colores del tema claro
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
