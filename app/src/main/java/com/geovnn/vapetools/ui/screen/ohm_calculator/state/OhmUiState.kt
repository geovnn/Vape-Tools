package com.geovnn.vapetools.ui.screen.ohm_calculator.state

import com.geovnn.vapetools.ui.common.composable.VapeTextFieldState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState

data class OhmUiState(
    val topAppBar: VapeTopAppBarState = VapeTopAppBarState(),
    val content: Content = Content(),
) {
    enum class FieldIDs {
        VOLTAGE,
        RESISTANCE,
        CURRENT,
        WATTAGE
    }

    data class Content(
        val currentVoltage: Field = Field(FieldIDs.VOLTAGE),
        val currentResistance: Field = Field(FieldIDs.RESISTANCE),
        val currentCurrent: Field = Field(FieldIDs.CURRENT),
        val currentWattage: Field = Field(FieldIDs.WATTAGE),
    ) {
        data class Field(
            val id: FieldIDs,
            val textField: VapeTextFieldState = VapeTextFieldState(),
            val locked: Boolean = false,
        )
    }
}