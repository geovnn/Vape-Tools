package com.geovnn.vapetools.ui.navigation

sealed class Screen(val route: String) {
    object LiquidScreen : Screen("liquid_screen")
    object NicotineScreen : Screen("nicotine_screen")
    object CoilScreen : Screen("coil_screen")
    object OhmScreen : Screen("ohm_screen")
    object SavedScreen : Screen("saved_screen")
}
