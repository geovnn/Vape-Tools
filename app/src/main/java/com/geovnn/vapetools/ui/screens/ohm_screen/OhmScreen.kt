package com.geovnn.vapetools.ui.screens.ohm_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OhmScreen(
    drawerState: DrawerState,
    ohmViewModel: OhmViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val ohmUiState by ohmViewModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Ohm's Law")
                },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
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
                "Voltage",
                "Volt",
                ohmUiState.currentVoltage,
                onValueChange = { ohmViewModel.updateVoltage(it)},
                ohmUiState.lastSelectedList,
                onFocus = {ohmViewModel.addToSelectionList("Voltage")},
                focusManager
                )
            OhmRow(
                "Resistance",
                "Ω",
                ohmUiState.currentResistance,
                onValueChange = { ohmViewModel.updateResistance(it)},
                ohmUiState.lastSelectedList,
                onFocus = {ohmViewModel.addToSelectionList("Resistance")},
                focusManager
            )
            OhmRow(
                "Current",
                "Ampere",
                ohmUiState.currentCurrent,
                onValueChange = { ohmViewModel.updateCurrent(it)},
                ohmUiState.lastSelectedList,
                onFocus = {ohmViewModel.addToSelectionList("Current")},
                focusManager
            )
            OhmRow(
                "Wattage",
                "Watt",
                ohmUiState.currentWattage,
                onValueChange = { ohmViewModel.updateWattage(it)},
                ohmUiState.lastSelectedList,
                onFocus = {ohmViewModel.addToSelectionList("Wattage")},
                focusManager
            )
            Button(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 16.dp),
                onClick = { ohmViewModel.calculateOhm() }
            ) {
                Text("Calculate")
            }
        }
    }
}

@Composable
fun OhmRow(
    label: String,
    measure: String,
    valueVar: String,
    onValueChange: (String) -> Unit,
    list: List<String>,
    onFocus: () -> Unit,
    focus: FocusManager
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AnimatedVisibility(list.contains(label)) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "",
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                tint = Color.Gray
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "$label input box" }
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        onFocus()
                    }
                },
            value = valueVar,
            onValueChange = { newText ->
                onValueChange(newText)
            },
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Right
            ),
            label = {
                Text(text = label)
            },
            suffix = {
                Text(text = measure)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focus.moveFocus(
                        focusDirection = FocusDirection.Next,
                    )
                }
            )
        )
    }
}