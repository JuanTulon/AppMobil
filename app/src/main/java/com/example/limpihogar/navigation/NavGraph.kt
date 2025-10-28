package com.example.limpihogar.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.limpihogar.ui.screens.auth.LoginScreen
import com.example.limpihogar.ui.screens.auth.RegisterScreen
import com.example.limpihogar.ui.screens.cart.CartScreen
import com.example.limpihogar.ui.screens.catalog.CatalogScreen
import com.example.limpihogar.ui.screens.product.ProductDetailScreen
import com.example.limpihogar.ui.screens.profile.ProfileScreen
import com.example.limpihogar.ui.screens.chekout.ChekoutSuccessScreen
import com.example.limpihogar.ui.screens.chekout.CheckoutFailedScreen




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Autenticación
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla principal (Catálogo)
        composable(Routes.HOME) {
            CatalogScreen(
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                }
            )
        }

        // Detalle de producto
        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(
                productId = productId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Carrito
        composable(Routes.CART) {
            CartScreen(
                onNavigateToCheckoutSuccess = {navController.navigate(Routes.CHECKOUT_SUCCESS){
                    popUpTo(Routes.CART) { inclusive = true }
                } },
                onNavigateToCheckoutFailed = { navController.navigate(Routes.CHECKOUT_FAILED) }
            )
        }
        // Perfil
        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        //chekout
        composable(Routes.CHECKOUT_SUCCESS) {
            ChekoutSuccessScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME){
                        popUpTo(0)
                    }
                }
            )
    }
        composable(Routes.CHECKOUT_FAILED) {
            CheckoutFailedScreen(
                onNavigateBackToCart = {
                    //volvemos a la pantalla anterior (el carrito)
                    navController.popBackStack()
                }
            )
        }
    }
}

