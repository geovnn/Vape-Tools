package com.geovnn.vapetools.ui.screen.liquid_calculator.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.geovnn.vapetools.data.model.AromaType
import com.geovnn.vapetools.ui.common.composable.LiquidParameters
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBar
import com.geovnn.vapetools.ui.screen.liquid_calculator.state.LiquidUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidScreen(
    uiState: LiquidUiState,
    openDrawer: () -> Unit,
    onChangeAmount: (String) -> Unit,
    onSliderChangeAmount: (Float) -> Unit,
    onAromaValueChange: (String) -> Unit,
    onAromaTypeChange: (AromaType) -> Unit,
    onAdditiveValueChange: (String) -> Unit,
    onTargetNicotineValueChange: (String) -> Unit,
    onNicotineShotValueChange: (String) -> Unit,
    onNicotineShotSliderChange: (Float) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .keepScreenOn(),
        topBar = {
            VapeTopAppBar(
                modifier = Modifier,
                state = uiState.topAppBar,
                openDrawer = openDrawer,
                actions = { }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LiquidParameters(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                state = uiState.liquidParameters,
                onChangeAmount = onChangeAmount,
                onSliderChangeAmount = onSliderChangeAmount,
                onAromaValueChange = onAromaValueChange,
                onAromaTypeChange = onAromaTypeChange,
                onAdditiveValueChange = onAdditiveValueChange,
                onTargetNicotineValueChange = onTargetNicotineValueChange,
                onNicotineShotValueChange = onNicotineShotValueChange,
                onNicotineShotSliderChange = onNicotineShotSliderChange,
            )

            uiState.liquidsResults?.let {
                HorizontalDivider(Modifier.padding(bottom = 10.dp))
                LiquidsResultsBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    state = it
                )
            }
        }
    }
}


@Composable
private fun LiquidsResultsBox(
    modifier: Modifier,
    state: LiquidUiState.LiquidsResultsBoxState
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
                text = state.title.asString(),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = state.description.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (state.isError) MaterialTheme.colorScheme.error else Color.Unspecified,
            )
            state.ingredients.forEach { result ->
                Row(
                    modifier = Modifier.padding(top = 5.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = result.name.asString()
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        text = result.volume.asString()
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        text = result.weight?.asString()?: ""
                    )
                }
            }
        }
    }
}