package com.geovnn.vapetools.ui.screens.coil_screen

data class CoilUiState(
    val currentNumberOfCoils: String = "Single coil",
    val currentMaterial: String = "Kanthal A1",
    val currentWireDiameter: String = "",
    val isWireDiameterEmpty: Boolean = false,
    val currentLegLength: String = "",
    val isLegLengthEmpty: Boolean = false,
    val currentCoilDiameter: String = "",
    val isCoilDiameterEmpty: Boolean = false,
    val currentWraps : String = "",
    val isWrapsEmpty: Boolean = false,
    val currentResistance : String = "",
    val isResistanceFocused : Boolean = false,
)
