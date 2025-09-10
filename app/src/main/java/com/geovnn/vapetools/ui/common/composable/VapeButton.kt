package com.geovnn.vapetools.ui.common.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.geovnn.vapetools.helper.UiText

@Composable
fun VapeButton(
    modifier: Modifier = Modifier,
    state: VapeButtonState,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(state.text?.asString() ?: "")
    }
}

data class VapeButtonState(
    val text: UiText? = null,
    val isEnabled: Boolean = true
)