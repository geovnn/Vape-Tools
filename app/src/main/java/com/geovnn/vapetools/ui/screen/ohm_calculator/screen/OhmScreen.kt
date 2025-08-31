package com.geovnn.vapetools.ui.screen.ohm_calculator.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.geovnn.vapetools.ui.common.composable.VapeTextField
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBar
import com.geovnn.vapetools.ui.screen.ohm_calculator.state.OhmUiState
import com.geovnn.vapetools.ui.screen.ohm_calculator.viewmodel.OhmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OhmScreen(
    viewModel: OhmViewModel,
    uiState: OhmUiState,
    openDrawer: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            VapeTopAppBar(
                modifier = Modifier,
                state = uiState.topAppBar,
                openDrawer = openDrawer,
                actions = { }
            )
        },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OhmRow(
                fieldState = uiState.content.currentVoltage,
                onValueChange = viewModel::updateVoltage,
                onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) },
                onFocus = { viewModel.addToSelectionList(OhmUiState.FieldIDs.VOLTAGE) }
            )

            OhmRow(
                fieldState = uiState.content.currentResistance,
                onValueChange = viewModel::updateResistance,
                onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) },
                onFocus = { viewModel.addToSelectionList(OhmUiState.FieldIDs.RESISTANCE) }
            )
            OhmRow(
                fieldState = uiState.content.currentCurrent,
                onValueChange = viewModel::updateCurrent,
                onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) },
                onFocus = { viewModel.addToSelectionList(OhmUiState.FieldIDs.CURRENT) }
            )

            OhmRow(
                fieldState = uiState.content.currentWattage,
                onValueChange = viewModel::updateWattage,
                onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) },
                onFocus = { viewModel.addToSelectionList(OhmUiState.FieldIDs.WATTAGE) }
            )
            Button(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 16.dp),
                onClick = viewModel::calculateOhm
            ) {
                Text("Calculate")
            }
        }
    }
}

@Composable
fun OhmRow(
    fieldState: OhmUiState.Content.Field,
    onValueChange: (String) -> Unit,
    onNext: (KeyboardActionScope) -> Unit,
    onFocus: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AnimatedVisibility(fieldState.locked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "",
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                tint = Color.Gray
            )
        }
        VapeTextField(
            modifier = Modifier
                .onFocusChanged { focusState -> if (focusState.isFocused) { onFocus() } },
            state = fieldState.textField,
            onValueChange = onValueChange,
            onNext = onNext
        )
    }
}