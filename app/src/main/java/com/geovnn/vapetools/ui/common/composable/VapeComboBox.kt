package com.geovnn.vapetools.ui.common.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp


@Composable
fun VapeComboBox(
    modifier: Modifier = Modifier,
    state: VapeComboBoxState,
    onItemSelected: (VapeComboBoxState.Item) -> Unit = {}
) {
    Row(
        modifier = modifier
    ) {
        state.items.forEach { item ->
            Row(
                Modifier
                    .selectableGroup()
                    .weight(1f)
                    .clickable { onItemSelected(item) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp,horizontal = 10.dp)
                ) {
                    RadioButton(
                        selected = state.selectedItem?.id==item.id,
                        onClick = null,
                        modifier = Modifier
                    )
                    Text(
                        text = item.string,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp),
                    )

                }
            }
        }
    }
}

data class VapeComboBoxState(
    val items: List<Item> = emptyList(),
    val selectedItem: Item? = null,
) {
    data class Item(
        val string: String,
        val id: String
    )
}