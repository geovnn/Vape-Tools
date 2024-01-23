package com.geovnn.vapetools.ui.screens.nicotine_screen

data class NicotineUiState(
    val addNicotine: Boolean = true,
    val totalAmount: String = "",
    val currentNicotine: String = "",
    val targetStrength: String = "",
    val shotStrength: String = "",
    val resultAmount: String = "",
    val isError: Boolean = false
)
