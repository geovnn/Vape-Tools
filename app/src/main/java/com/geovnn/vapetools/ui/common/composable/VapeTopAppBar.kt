package com.geovnn.vapetools.ui.common.composable

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.geovnn.vapetools.helper.UiText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VapeTopAppBar(
    modifier: Modifier = Modifier,
    state: VapeTopAppBarState,
    openDrawer : () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {  }
) {
    TopAppBar(
        modifier = modifier,
        title = { state.title?.let { Text(text = state.title.asString()) } },
        navigationIcon = {
            IconButton(onClick =  openDrawer) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = actions
    )
}

data class VapeTopAppBarState(
    val title: UiText? = null,
)