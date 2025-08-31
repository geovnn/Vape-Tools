package com.geovnn.vapetools.ui.common.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun VapeDropdownMenu(
    modifier: Modifier,
    state: VapeDropdownMenuState,
    onValueChange: (VapeDropdownMenuState.Option) -> Unit,
    keyboardController: SoftwareKeyboardController?
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier= modifier,
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .semantics { contentDescription = "Localized Description" }
                .onFocusChanged { keyboardController?.hide() },
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Right
            ),
            value = state.text,
            onValueChange = {  },
            singleLine = state.singleLine,
            label = { Text(state.label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = OutlinedTextFieldDefaults.colors().unfocusedTextColor,
                disabledLabelColor = OutlinedTextFieldDefaults.colors().unfocusedLabelColor,
                disabledPrefixColor = OutlinedTextFieldDefaults.colors().unfocusedPrefixColor,
                disabledSuffixColor = OutlinedTextFieldDefaults.colors().unfocusedSuffixColor,
                disabledContainerColor = OutlinedTextFieldDefaults.colors().unfocusedContainerColor,
                disabledPlaceholderColor = OutlinedTextFieldDefaults.colors().unfocusedPlaceholderColor,
                disabledLeadingIconColor = OutlinedTextFieldDefaults.colors().unfocusedLeadingIconColor,
                disabledTrailingIconColor = OutlinedTextFieldDefaults.colors().unfocusedTrailingIconColor,

                ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            state.options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onValueChange(option)
                        isExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

data class VapeDropdownMenuState(
    val label: String = "",
    val measureUnit: String = "",
    val text: String = "",
    val singleLine: Boolean = true,
    val options: List<Option> = emptyList()
) {
    data class Option(
        val id: String,
        val label: String
    )
}
