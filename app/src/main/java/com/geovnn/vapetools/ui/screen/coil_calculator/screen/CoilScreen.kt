package com.geovnn.vapetools.ui.screen.coil_calculator.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.geovnn.vapetools.ui.common.composable.VapeButton
import com.geovnn.vapetools.ui.common.composable.VapeDropdownMenu
import com.geovnn.vapetools.ui.common.composable.VapeTextField
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBar
import com.geovnn.vapetools.ui.screen.coil_calculator.state.CoilUiState
import com.geovnn.vapetools.ui.screen.coil_calculator.viewmodel.CoilViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CoilScreen(
    coilViewModel: CoilViewModel,
    uiState: CoilUiState,
    openDrawer: () -> Unit,
    ) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            VapeTopAppBar(
                modifier = Modifier,
                state = uiState.navBar,
                openDrawer = openDrawer,
                actions = { }
            )
        },
    ) { values ->
        val scrollState = rememberScrollState()
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

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
                VapeDropdownMenu(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    state = uiState.content.setupType,
                    onValueChange = { coilViewModel.updateSetupType(it)},
                    keyboardController = keyboardController
                )
                VapeDropdownMenu(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    state = uiState.content.material,
                    onValueChange = { coilViewModel.updateMaterial(it)},
                    keyboardController = keyboardController
                )
            }
            VapeTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 5.dp),
                state = uiState.content.wireDiameter,
                onValueChange = { coilViewModel.updateWireDiameter(it)},
                onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }
            )

            VapeTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 5.dp),
                state = uiState.content.legLength,
                onValueChange = { coilViewModel.updateLegLength(it)},
                onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }
            )

            VapeTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 5.dp),
                state = uiState.content.innerDiameter,
                onValueChange = { coilViewModel.updateInnerDiameter(it)},
                onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VapeTextField(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                        .onFocusChanged {
                            if (it.isFocused) { coilViewModel.selectWraps() }
                        },
                    state = uiState.content.wraps,
                    onValueChange = { coilViewModel.updateWraps(it)},
                    onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }
                )

                VapeTextField(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                        .onFocusChanged {
                            if (it.isFocused) { coilViewModel.selectResistance() }
                        },
                    state = uiState.content.ohms,
                    onValueChange = { coilViewModel.updateResistance(it)},
                    onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }
                )
            }
            VapeButton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 5.dp),
                onClick = { coilViewModel.clickButton() },
                state = uiState.content.calculateButton
            )
        }
    }
}