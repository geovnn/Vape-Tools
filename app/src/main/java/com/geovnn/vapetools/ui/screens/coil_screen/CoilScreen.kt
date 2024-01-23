package com.geovnn.vapetools.ui.screens.coil_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CoilScreen(
    drawerState: DrawerState,
    coilViewModel: CoilViewModel = viewModel()
    ) {
    val scope = rememberCoroutineScope()
    val coilUiState by coilViewModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Coil Calculator")
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
    ) { values ->
        val scrollState = rememberScrollState()
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val dropdownOptionsSetup = listOf("Single coil", "Double coil", "Triple coil", "Quad coil")
        val dropdownOptionsMaterial = listOf("Kanthal A1", "SS316L", "Ni80")

        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(values)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DropdownMenu(
                    Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    coilUiState.currentNumberOfCoils,
                    "Setup",
                    dropdownOptionsSetup,
                    onValueChange = { coilViewModel.updateNumberOfCoils(it)},
                    keyboardController)

                DropdownMenu(
                    Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    coilUiState.currentMaterial,
                    "Coil Material",
                    dropdownOptionsMaterial,
                    onValueChange = { coilViewModel.updateMaterial(it)},
                    keyboardController)
            }

            InputRow(Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Localized Description" }
                .padding(horizontal = 16.dp)
                .padding(vertical = 5.dp),
                coilUiState.currentWireDiameter,
                "Diameter of wire",
                "mm",
                coilUiState.isWireDiameterEmpty,
                getValue = { coilViewModel.updateWireDiameter(it)},
                focusManager
            )

            InputRow(Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Localized Description" }
                .padding(horizontal = 16.dp)
                .padding(vertical = 5.dp),
                coilUiState.currentLegLength,
                "Leg length (total)",
                "mm",
                coilUiState.isLegLengthEmpty,
                getValue = { coilViewModel.updateLegLength(it)},
                focusManager
            )

            InputRow(Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Localized Description" }
                .padding(horizontal = 16.dp)
                .padding(vertical = 5.dp),
                coilUiState.currentCoilDiameter,
                "Inner diameter of coil",
                "mm",
                coilUiState.isCoilDiameterEmpty,
                getValue = { coilViewModel.updateCoilDiameter(it)},
                focusManager
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InputRow(Modifier
                    .weight(1F)
                    .semantics { contentDescription = "Localized Description" }
                    .padding(end = 8.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            coilViewModel.updateIsResistanceFocused(false)
                        }
                    },
                    coilUiState.currentWraps,
                    "Wraps",
                    "",
                    coilUiState.isWrapsEmpty,
                    getValue = { coilViewModel.updateWraps(it)},
                    focusManager
                )

                InputRow(Modifier
                    .weight(1F)
                    .semantics { contentDescription = "Localized Description" }
                    .padding(start = 8.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            coilViewModel.updateIsResistanceFocused(true)
                        }
                    },
                    coilUiState.currentResistance,
                    "Resistance",
                    "Ω",
                    false,
                    getValue = { coilViewModel.updateResistance(it)},
                    focusManager
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 5.dp),
                onClick = { coilViewModel.clickButton() }
            ) {
                Text("Calculate")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DropdownMenu(
    modifier: Modifier, valueVar: String, label: String, optionsList: List<String>, onValueChange: (String) -> Unit,
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
            value = valueVar,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            optionsList.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onValueChange(selectionOption)
                        isExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
fun InputRow(modifier: Modifier, valueVar: String, label: String, measureUnit: String, isError: Boolean, getValue: (String) -> Unit,
             focus: FocusManager
) {
    OutlinedTextField(
        modifier = modifier,
        value = valueVar,
        isError = isError,
        onValueChange = {
            getValue(it)
        },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Right
        ),
        label = {
            Text(text = label)
        },
        suffix = {
            Text(text = measureUnit)
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