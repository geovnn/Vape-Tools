package com.geovnn.vapetools.ui.screen.liquid_calculator.state

import com.geovnn.vapetools.helper.UiText
import com.geovnn.vapetools.ui.common.composable.LiquidParametersState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState

data class LiquidUiState(
    val topAppBar: VapeTopAppBarState = VapeTopAppBarState(),
    val liquidParameters: LiquidParametersState = LiquidParametersState(),
    val liquidsResults: LiquidsResultsBoxState? = null,
) {

    data class LiquidsResultsBoxState(
        val title: UiText,
        val description: UiText,
        val ingredients: List<Ingredient>,
        val isError: Boolean,
    ) {
        data class Ingredient(
            val name: UiText,
            val volume: UiText,
            val weight: UiText?
        )
    }
}