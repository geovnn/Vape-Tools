package com.geovnn.vapetools.ui.common.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

@Composable
fun VapeTextField(
    modifier: Modifier = Modifier,
    state: VapeTextFieldState,
    onValueChange: (String) -> Unit,
    onNext: (KeyboardActionScope) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
//            .semantics { contentDescription = "${state.label} input box" },
        value = state.text,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right),
        label = { Text(text = state.label) },
        suffix = { Text(text = state.measureUnit) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        isError = state.isError,
        singleLine = state.singleLine,
        keyboardActions = KeyboardActions(
            onNext = onNext
        ),
    )
}

data class VapeTextFieldState(
    val label: String = "",
    val measureUnit: String = "",
    val text: String = "",
    val singleLine: Boolean = true,
    val isError: Boolean = false,
)
