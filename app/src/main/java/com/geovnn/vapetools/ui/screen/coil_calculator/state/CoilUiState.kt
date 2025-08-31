package com.geovnn.vapetools.ui.screen.coil_calculator.state

import com.geovnn.vapetools.ui.common.composable.VapeButtonState
import com.geovnn.vapetools.ui.common.composable.VapeDropdownMenuState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState

data class CoilUiState(
    val navBar: VapeTopAppBarState = VapeTopAppBarState(),
    val content: Content = Content(),
) {
    data class Content(
        val setupType: VapeDropdownMenuState = VapeDropdownMenuState(),
        val material: VapeDropdownMenuState = VapeDropdownMenuState(),
        val wireDiameter: VapeTextFieldState = VapeTextFieldState(),
        val legLength: VapeTextFieldState = VapeTextFieldState(),
        val innerDiameter: VapeTextFieldState = VapeTextFieldState(),
        val wraps: VapeTextFieldState = VapeTextFieldState(),
        val ohms: VapeTextFieldState = VapeTextFieldState(),
        val calculateButton: VapeButtonState = VapeButtonState()
    )
}