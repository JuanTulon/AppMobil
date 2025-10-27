package com.example.limpihogar.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val CART = "cart"
    const val PROFILE = "profile"

    fun productDetail(productId: Int) = "product_detail/$productId"
}

