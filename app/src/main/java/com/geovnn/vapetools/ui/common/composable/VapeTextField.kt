package com.geovnn.vapetools.ui.common.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.geovnn.vapetools.helper.UiText

@Composable
fun VapeTextField(
    modifier: Modifier = Modifier,
    state: VapeTextFieldState,
    onValueChange: (String) -> Unit,
    onNext: (KeyboardActionScope) -> Unit,
    onClick: (() -> Unit)? = null,
) {
    val colors = if (onClick!=null) {
        OutlinedTextFieldDefaults.colors(
            disabledTextColor = OutlinedTextFieldDefaults.colors().unfocusedTextColor,
            disabledLeadingIconColor = OutlinedTextFieldDefaults.colors().unfocusedLeadingIconColor,
            disabledTrailingIconColor = OutlinedTextFieldDefaults.colors().unfocusedTrailingIconColor,
            disabledLabelColor = OutlinedTextFieldDefaults.colors().unfocusedLabelColor,
            disabledPlaceholderColor = OutlinedTextFieldDefaults.colors().unfocusedPlaceholderColor,
            disabledSuffixColor = OutlinedTextFieldDefaults.colors().unfocusedSuffixColor,
            disabledPrefixColor = OutlinedTextFieldDefaults.colors().unfocusedPrefixColor,
            disabledContainerColor = OutlinedTextFieldDefaults.colors().unfocusedContainerColor,
            disabledSupportingTextColor = OutlinedTextFieldDefaults.colors().unfocusedSupportingTextColor,
            disabledBorderColor = OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor,
            )
    } else OutlinedTextFieldDefaults.colors()
    val modifier = if (onClick!=null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }
    val label = state.label?.asString() ?: ""
    val suffix = state.measureUnit?.asString() ?: ""
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
//            .semantics { contentDescription = "${state.label} input box" },
        value = state.text,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(textAlign = state.textAlign),
        label = { Text(text = label) },
        suffix = { Text(text = suffix) },
        colors = colors,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (state.isNumber) KeyboardType.Number else KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        enabled = onClick==null,
        isError = state.isError,
        singleLine = state.singleLine,
        keyboardActions = KeyboardActions(
            onNext = onNext
        ),
    )
}

data class VapeTextFieldState(
    val label: UiText? = null,
    val measureUnit: UiText? = null,
    val text: String = "",
    val singleLine: Boolean = true,
    val isError: Boolean = false,
    val textAlign: TextAlign = TextAlign.Right,
    val isNumber: Boolean = true
)
