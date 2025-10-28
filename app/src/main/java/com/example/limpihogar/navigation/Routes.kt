package com.example.limpihogar.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val CART = "cart"
    const val PROFILE = "profile"

    const val CHECKOUT_SUCCESS = "checkout_success"

    const val CHECKOUT_FAILED = "checkout_failed"

    fun productDetail(productId: Int) = "product_detail/$productId"

    //  NUEVO: ruta para el panel de administración (BackOffice)
    const val ADMIN_DASHBOARD = "admin_dashboard"

    const val ADMIN_ADD_PRODUCT = "admin_add_product"


}
