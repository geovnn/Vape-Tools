package com.geovnn.vapetools.ui.screens.ohm_screen

data class OhmUiState(
    val currentVoltage: String = "",
    val currentResistance: String = "",
    val currentCurrent: String = "",
    val currentWattage: String = "",
    val lastSelectedList: List<String> = listOf("")
)