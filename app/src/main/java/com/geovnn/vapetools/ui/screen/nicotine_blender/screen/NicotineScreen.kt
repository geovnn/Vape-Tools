package com.geovnn.vapetools.ui.screen.nicotine_blender.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.geovnn.vapetools.ui.common.composable.VapeComboBox
import com.geovnn.vapetools.ui.common.composable.VapeTextField
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBar
import com.geovnn.vapetools.ui.screen.nicotine_blender.state.NicotineUiState
import com.geovnn.vapetools.ui.screen.nicotine_blender.viewmodel.NicotineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NicotineScreen(
    openDrawer: () -> Unit,
    viewModel: NicotineViewModel = viewModel(),
    uiState: NicotineUiState,
){
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
            .padding(horizontal = 20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VapeComboBox(
                modifier = Modifier,
                state = uiState.content.mode,
                onItemSelected = viewModel::updateMode
            )
            VapeTextField(
                modifier = Modifier,
                state = uiState.content.amount,
                onValueChange = viewModel::updateTotalAmount ,
                onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }
            )
            VapeTextField(
                modifier = Modifier,
                state = uiState.content.currentNicotineStrength,
                onValueChange = viewModel::updateCurrentNicotineStrength ,
                onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }

            )

            VapeTextField(
                modifier = Modifier,
                state = uiState.content.targetStrength,
                onValueChange = viewModel::updateTargetNicotineStrength ,
                onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }

            )
            AnimatedVisibility(visible = uiState.content.mode.selectedItem?.id == NicotineUiState.Mode.ADD_NICOTINE.ordinal.toString()) {
                VapeTextField(
                    modifier = Modifier,
                    state = uiState.content.shotStrength,
                    onValueChange = viewModel::updateShotStrength ,
                    onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }

                )
            }
            HorizontalDivider(Modifier.padding(vertical = 20.dp))
            ResultsBox(
                modifier = Modifier.fillMaxWidth(),
                state = uiState.content.result
            )
        }
    }
}

@Composable
private fun ResultsBox(
    modifier: Modifier,
    state: NicotineUiState.Content.ResultsBoxState
) {
    Card (
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = state.title?.asString() ?: "",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = state.description?.asString() ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = if (state.isError) MaterialTheme.colorScheme.error else Color.Unspecified,
            )
        }
    }
}