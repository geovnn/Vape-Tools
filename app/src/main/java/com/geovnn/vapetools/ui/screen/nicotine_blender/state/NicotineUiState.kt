package com.geovnn.vapetools.ui.screen.nicotine_blender.state

import com.geovnn.vapetools.ui.common.composable.VapeComboBoxState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState

data class NicotineUiState(
    val topAppBar: VapeTopAppBarState = VapeTopAppBarState(),
    val content: Content = Content(),
) {
    enum class Mode {
        ADD_NICOTINE,
        REMOVE_NICOTINE
    }


    data class Content(
        val mode: VapeComboBoxState = VapeComboBoxState(),
        val amount: VapeTextFieldState = VapeTextFieldState(),
        val currentNicotineStrength: VapeTextFieldState = VapeTextFieldState(),
        val targetStrength: VapeTextFieldState = VapeTextFieldState(),
        val shotStrength: VapeTextFieldState = VapeTextFieldState(),
        val result: ResultsBoxState = ResultsBoxState(),
    ) {
        data class ResultsBoxState(
            val title: String = "",
            val description: String = "",
            val isError: Boolean = false,
        )
    }
}