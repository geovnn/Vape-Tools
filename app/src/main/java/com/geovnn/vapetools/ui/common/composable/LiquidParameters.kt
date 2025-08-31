package com.geovnn.vapetools.ui.common.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.geovnn.vapetools.data.model.AromaType
import com.geovnn.vapetools.ui.screen.liquid_calculator.state.LiquidUiState

@Composable
fun LiquidParameters(
    modifier: Modifier = Modifier,
    state: LiquidParametersState,
    onChangeAmount: (String) -> Unit,
    onSliderChangeAmount: (Float) -> Unit,
    onAromaValueChange: (String) -> Unit,
    onAromaTypeChange: (AromaType) -> Unit,
    onAdditiveValueChange: (String) -> Unit,
    onTargetNicotineValueChange: (String) -> Unit,
    onNicotineShotValueChange: ((String) -> Unit)? = null,
    onNicotineShotSliderChange: ((Float) -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    val spaceBetween = 8.dp
    Column(
        modifier = modifier,

        ) {
        VapeTextFieldWithSlider(
            modifier = Modifier.padding(bottom = spaceBetween),
            onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) },
            state = state.amount,
            onTextFieldValueChange = onChangeAmount,
            onSliderChange = onSliderChangeAmount
        )
        VapeTextFieldWithPGVGCombo(
            modifier = Modifier.padding(bottom = spaceBetween),
            state = state.aroma,
            onValueChange = onAromaValueChange,
            onSelectionChange = onAromaTypeChange,
            onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }
        )
        VapeTextField(
            modifier = Modifier.padding(bottom = spaceBetween),
            state = state.additive,
            onValueChange = onAdditiveValueChange,
            onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }
        )


        VapeTextField(
            modifier = Modifier.padding(bottom = spaceBetween),
            state = state.targetNicotineStrength,
            onValueChange = onTargetNicotineValueChange,
            onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }
        )
        if (onNicotineShotValueChange != null && onNicotineShotSliderChange != null) {
            VapeTextFieldWithSlider(
                onNext = { focusManager.clearFocus() },
                state = state.nicotineShotStrength,
                onTextFieldValueChange = onNicotineShotValueChange,
                onSliderChange = onNicotineShotSliderChange
            )
        }
    }
}


data class LiquidParametersState(
    val amount: VapeTextFieldWithSliderState = VapeTextFieldWithSliderState(),
    val aroma: VapeTextFieldWithPGVGComboState = VapeTextFieldWithPGVGComboState(),
    val additive: VapeTextFieldState = VapeTextFieldState(),
    val targetNicotineStrength: VapeTextFieldState = VapeTextFieldState(),
    val nicotineShotStrength: VapeTextFieldWithSliderState = VapeTextFieldWithSliderState(),
)