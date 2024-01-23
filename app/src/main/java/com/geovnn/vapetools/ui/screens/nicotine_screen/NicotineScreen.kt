package com.geovnn.vapetools.ui.screens.nicotine_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.geovnn.vapetools.ui.screens.coil_screen.InputRow
import com.geovnn.vapetools.ui.screens.liquid_screen.LiquidText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NicotineScreen(
    drawerState: DrawerState,
    nicotineViewModel: NicotineViewModel = viewModel()
){
    val scope = rememberCoroutineScope()
    val nicotineUiState by nicotineViewModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Nicotine Blender")
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
            Row(
                Modifier
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 16.dp)
                    .selectableGroup())
            {
                RadioButton(
                    selected = nicotineUiState.addNicotine,
                    onClick = { nicotineViewModel.addNicotineFlag(true) },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Localized Description" }
                )
                Text(
                    text = "Increase nicotine",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(3f)
                        .clickable { nicotineViewModel.addNicotineFlag(true) }
                        .padding(start = 16.dp),
                )
                RadioButton(
                    selected = !nicotineUiState.addNicotine,
                    onClick = { nicotineViewModel.addNicotineFlag(false) },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Localized Description" }
                )
                Text(
                    text = "Decrease nicotine",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(3f)
                        .clickable { nicotineViewModel.addNicotineFlag(false) }
                        .padding(start = 16.dp)
                )
            }
            LiquidText(
                label = "Total amount",
                measure = "ml",
                valueVar = nicotineUiState.totalAmount,
                getValue = {
                    nicotineViewModel.updateTotalAmount(it)
                },
                focus = focusManager
            )
            LiquidText(
                label = "Current nicotine strength",
                measure = "mg/ml",
                valueVar = nicotineUiState.currentNicotine,
                getValue = {
                    nicotineViewModel.updateCurrentNicotineStrength(it)
                },
                focus = focusManager
            )
            InputRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Target strength" }
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 16.dp),
                valueVar = nicotineUiState.targetStrength,
                label = "Target strength",
                measureUnit = "mg/ml",
                isError = nicotineUiState.isError,
                getValue = {
                    nicotineViewModel.updateTargetNicotineStrength(it)
                },
                focus = focusManager
            )
            AnimatedVisibility(nicotineUiState.addNicotine) {
                LiquidText(
                    label = "Shot strength",
                    measure = "mg/ml",
                    valueVar = nicotineUiState.shotStrength,
                    getValue = {
                        nicotineViewModel.updateShotStrength(it)
                    },
                    focus = focusManager
                )
            }
            if (!nicotineUiState.isError) {
                NicotineResults(
                    modifier = Modifier,
                    isAdding = nicotineUiState.addNicotine,
                    amount = nicotineUiState.resultAmount
                )
            }
        }
    }
}

@Composable
fun NicotineResults(
    modifier: Modifier,
    isAdding: Boolean,
    amount: String
) {
    if (amount!="") {
        if (isAdding) {
            Text(
                modifier=modifier
                    .padding(16.dp),
                text = "Add $amount ml of nicotine shot")
        } else {
            Text(
                modifier=modifier
                    .padding(16.dp),
                text = "Add $amount ml of nicotine-free base")
        }
    }
}