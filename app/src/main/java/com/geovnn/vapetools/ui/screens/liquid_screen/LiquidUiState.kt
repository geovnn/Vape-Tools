package com.geovnn.vapetools.ui.screens.liquid_screen

data class LiquidUiState(
    val currentTargetTotalAmount: String = "",
    val currentTargetVgRatio: Int = 50,
    val currentTargetPgRatio: Int = 50,
    val currentTargetNicotineStrength: String = "",
    val currentAromaRatio: String = "",
    val currentAromaOption: String = "PG",
    val currentAdditiveRatio: String = "",
    val currentNicotineShotStrength: String = "",
    val currentNicotineVgRatio: Int = 50,
    val currentNicotinePgRatio: Int = 50,
    )