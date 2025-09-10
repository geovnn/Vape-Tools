package com.geovnn.vapetools.ui.common.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.geovnn.vapetools.data.model.AromaType
import com.geovnn.vapetools.helper.UiText

@Composable
fun VapeTextFieldWithPGVGCombo(
    modifier: Modifier = Modifier,
    state : VapeTextFieldWithPGVGComboState,
    onValueChange: (String) -> Unit,
    onSelectionChange: (AromaType) -> Unit,
    onNext: (KeyboardActionScope) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val label = state.label?.asString() ?: ""
        val suffix = state.measure?.asString() ?: ""
        OutlinedTextField(
            modifier = Modifier.weight(3f),
            value = state.textFieldValue,
            onValueChange = onValueChange,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right),
            label = { Text(text = label) },
            suffix = { Text(text = suffix) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = onNext
            ),
            singleLine = true
        )
        Column(
            modifier = Modifier
                .selectableGroup()
                .weight(1f),
        ) {
            AromaType.entries.forEach { type ->
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .selectable(
                            selected = (type == state.selectedOption),
                            onClick = { onSelectionChange(type) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    RadioButton(
                        selected = (type == state.selectedOption),
                        onClick = null,
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    )
                    Text(
                        text = type.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(3.dp)
                            .weight(1f)
                    )
                }
            }
        }
    }
}

data class VapeTextFieldWithPGVGComboState(
    val label: UiText? = null,
    val measure: UiText? = null,
    val textFieldValue: String = "",
    val selectedOption: AromaType = AromaType.PG,
)