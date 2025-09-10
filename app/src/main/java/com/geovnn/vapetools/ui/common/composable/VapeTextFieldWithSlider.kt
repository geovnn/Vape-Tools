package com.geovnn.vapetools.ui.common.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.geovnn.vapetools.helper.UiText
import kotlin.math.roundToInt

@Composable
fun VapeTextFieldWithSlider(
    modifier: Modifier = Modifier,
    state: VapeTextFieldWithSliderState,
    onTextFieldValueChange: (String) -> Unit,
    onSliderChange: (Float) -> Unit,
    onNext: (KeyboardActionScope) -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            val label = state.textFieldLabel?.asString() ?: ""
            val suffix = state.textFieldSuffix?.asString() ?: ""
            OutlinedTextField(
                modifier = Modifier.weight(3f),
                value = state.textFieldValue,
                onValueChange = { onTextFieldValueChange(it) },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right),
                label = { Text(text = label) },
                suffix = { Text(text = suffix) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = onNext
                )
            )
            Column(
                modifier = Modifier
                    .padding(start = 14.dp, top = 7.dp)
                    .widthIn(min = 60.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                val spaceBetween = 2.dp
                Text(
                    modifier = Modifier.padding(bottom = spaceBetween),
                    text = "${state.pgRatio.roundToInt()} PG"
                )
                Text(
                    text = "${state.vgRatio.roundToInt()} VG"
                )
                AnimatedVisibility(
                    visible = state.additiveRatio.roundToInt()>0
                ) {
                    Text(
                        modifier = Modifier.padding(top = spaceBetween),
                        text = "${state.additiveRatio.roundToInt()} A"
                    )

                }
            }
        }
        val valueRange = remember(state.additiveRatio) {
            0f..(100f - state.additiveRatio)
        }

        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .focusProperties { canFocus=false }
                .padding(top = 15.dp),
            value = state.pgRatio,
            onValueChange = { newValue -> onSliderChange(newValue) },
            valueRange = valueRange,
            onValueChangeFinished = {  },
        )
    }

}

data class VapeTextFieldWithSliderState(
    val textFieldLabel: UiText? = null,
    val textFieldSuffix: UiText? = null,
    val textFieldValue: String = "",
    val pgRatio: Float = 0f,
    val vgRatio: Float = 0f,
    val additiveRatio: Float = 0f,
)