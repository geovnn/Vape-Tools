package com.geovnn.vapetools.ui.screens.liquid_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidScreen(
    drawerState: DrawerState,
    liquidViewModel: LiquidViewModel = viewModel()
    ) {
    val scope = rememberCoroutineScope()
    val liquidUiState by liquidViewModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Liquid Calculator")
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
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(values)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val radioOptionsAroma = listOf("PG", "VG")
            SliderTextInput(
                Modifier
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp),
                label = "Target total amount",
                measure = "ml",
                additiveRatio = liquidUiState.currentAdditiveRatio,
                valueVar = liquidUiState.currentTargetTotalAmount,
                getValue = {liquidViewModel.updateTargetTotalAmount(it)},
                focus = focusManager,
                pgRatio = liquidUiState.currentTargetPgRatio,
                getSliderValue = {liquidViewModel.getTargetRatioFromSlider(it)},
            )

            ComboTextInput(
                radioList = radioOptionsAroma,
                selectedOption = liquidUiState.currentAromaOption,
                label = "Target aroma level",
                measure = "%",
                valueVar = liquidUiState.currentAromaRatio,
                getValue = { liquidViewModel.updateAromaRatio(it) },
                getSelection = { liquidViewModel.updateAromaOption(it) },
                focus = focusManager
            )

            LiquidText(
                label = "Additive (e.g. water)",
                measure = "%",
                valueVar = liquidUiState.currentAdditiveRatio,
                getValue =  { liquidViewModel.updateAdditiveRatio(it) },
                focus = focusManager
            )

            LiquidText(
                label = "Target nicotine strength",
                measure = "mg/ml",
                valueVar = liquidUiState.currentTargetNicotineStrength,
                getValue = { liquidViewModel.updateTargetNicotineStrength(it) },
                focus = focusManager
            )

            SliderTextInput(
                Modifier
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 16.dp),
                label = "Nicotine Shot Strength",
                measure = "mg/ml",
                valueVar = liquidUiState.currentNicotineShotStrength,
                additiveRatio = "",
                getValue = { liquidViewModel.updateNicotineShotStrength(it) },
                focus = focusManager,
                pgRatio = liquidUiState.currentNicotinePgRatio,
                getSliderValue = { liquidViewModel.getNicotineRatioFromSlider(it) },
            )

            LiquidResults(
                targetTotalAmount = liquidUiState.currentTargetTotalAmount,
                targetVgRatio = liquidUiState.currentTargetVgRatio,
                targetPgRatio = liquidUiState.currentTargetPgRatio,
                targetAdditiveRatio = liquidUiState.currentAdditiveRatio,
                targetAromaRatio = liquidUiState.currentAromaRatio,
                targetNicotineStrength = liquidUiState.currentTargetNicotineStrength,
                aromaType = liquidUiState.currentAromaOption,
                nicotineShotStrength = liquidUiState.currentNicotineShotStrength,
                nicotineVgRatio = liquidUiState.currentNicotineVgRatio,
                nicotinePgRatio =liquidUiState.currentNicotinePgRatio
            )
        }
    }
}

@Composable
fun ComboTextInput(
    radioList: List<String>,
    selectedOption:String,
    label: String,
    measure: String,
    valueVar: String,
    getValue: (String) -> Unit,
    getSelection: (String) -> Unit,
    focus: FocusManager
) {
    Row(
        Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(3f),
            value = valueVar,
            onValueChange = { getValue(it)},
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
        Column(
            Modifier
                .selectableGroup()
                .weight(1f),
        ) {
            radioList.forEach { text ->
                Row(
                    Modifier
                        .padding(top = 8.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { getSelection(text) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null,
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    )
                    Text(
                        text = text,
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
@Composable
fun LiquidResults(
    targetTotalAmount: String,
    targetVgRatio: Int,
    targetPgRatio: Int,
    targetAdditiveRatio: String,
    targetAromaRatio: String,
    targetNicotineStrength: String,
    aromaType: String,
    nicotineShotStrength: String,
    nicotineVgRatio: Int,
    nicotinePgRatio: Int,
)
{
    val errorText: String
    var resultPg=0.0
    var resultVg=0.0
    var resultAroma=0.0
    var resultNicotine=0.0
    var resultAdditive=0.0
    var aromaPg=0.0
    var aromaVg=0.0
    if (targetTotalAmount.toDoubleOrNull()!=null) {
        if (targetNicotineStrength.toDoubleOrNull() != null && nicotineShotStrength.toDoubleOrNull() != null) {
            resultNicotine =
                (targetTotalAmount.toDouble() * targetNicotineStrength.toDouble()) / nicotineShotStrength.toDouble()
        }
        if (targetAromaRatio.toDoubleOrNull() != null) {
            resultAroma =
                (targetTotalAmount.toDouble() * (targetAromaRatio.toDouble() / 100))
            aromaPg = if (aromaType == "PG") {
                targetTotalAmount.toDouble() * (targetAromaRatio.toDouble() / 100)
            } else {
                0.0
            }
            aromaVg = if (aromaType == "VG") {
                targetTotalAmount.toDouble() * (targetAromaRatio.toDouble() / 100)
            } else {
                0.0
            }
        }
        resultPg =
            targetTotalAmount.toDouble() * (targetPgRatio.toDouble() / 100) - (resultNicotine * (nicotinePgRatio.toDouble() / 100)) - aromaPg
        resultVg =
            targetTotalAmount.toDouble() * (targetVgRatio.toDouble() / 100) - (resultNicotine * (nicotineVgRatio.toDouble() / 100)) - aromaVg
        if (targetAdditiveRatio.toDoubleOrNull() != null) {
            resultAdditive =
                (targetTotalAmount.toDouble() * (targetAdditiveRatio.toDouble() / 100))
        }
    }
    errorText = if (resultPg < 0.0 || resultVg < 0.0) {
        "This liquid is impossible to make!"
    } else {
        ""
    }
    val vgWeight = String.format("%.2f",resultVg*1.26)
    val pgWeight = String.format("%.2f",resultPg*0.965)
    val nicotineWeight = String.format("%.2f",((resultNicotine*(nicotinePgRatio.toDouble()/100)*0.965)+(resultNicotine*(nicotineVgRatio.toDouble()/100)*1.26).toInt()))
    val aromaWeight = if (aromaType=="PG") {
        String.format("%.2f",resultAroma*0.965)
    } else {
        String.format("%.2f",resultAroma*1.26)
    }

    val vgFinalFormatted = String.format("%.2f", resultVg)
    val pgFinalFormatted = String.format("%.2f", resultPg)
    val targetNicotineFinalFormatted =
        String.format("%.2f", resultNicotine)
    val aromaLevelFinalFormatted =
        String.format("%.2f", resultAroma)
    val additiveFinalFormatted = String.format("%.2f", resultAdditive)
    if (errorText!="") {
        Text(
            textAlign = TextAlign.Center,
            text = errorText,
            modifier = Modifier
                .fillMaxWidth(),
            style = TextStyle(
                color = Color.Red,
                fontSize = 16.sp,
            )
        )
    }

    if (targetTotalAmount.toIntOrNull()!=null && targetTotalAmount.toIntOrNull()!=0) {
        Text(
            textAlign = TextAlign.Left,
            text = "To make this liquid you need:\n",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp)
                .padding(vertical = 5.dp)
                .fillMaxWidth(),

            )
    }

    if (pgFinalFormatted!="0,00") {
        Text(
            textAlign = TextAlign.Left,
            text = "$pgFinalFormatted ml - $pgWeight gr PG",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 5.dp)
                .fillMaxWidth(),
        )
    }

    if (vgFinalFormatted!="0,00") {
        Text(
            textAlign = TextAlign.Left,
            text = "$vgFinalFormatted ml - $vgWeight gr VG",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 5.dp)
                .fillMaxWidth(),
        )
    }

    if (targetNicotineFinalFormatted!="0,00") {
        Text(
            textAlign = TextAlign.Left,
            text = "$targetNicotineFinalFormatted ml - $nicotineWeight gr Nicotine ($nicotinePgRatio PG/$nicotineVgRatio VG)",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 5.dp)
                .fillMaxWidth(),
        )
    }

    if (aromaLevelFinalFormatted!="0,00") {
        Text(
            textAlign = TextAlign.Left,
            text = "$aromaLevelFinalFormatted ml - $aromaWeight gr Aroma ($aromaType)",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 5.dp)
                .padding(bottom = 5.dp)
                .fillMaxWidth(),
        )
    }

    if (additiveFinalFormatted!="0,00") {
        Text(
            textAlign = TextAlign.Left,
            text = "$additiveFinalFormatted ml Additive",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 5.dp)
                .padding(bottom = 5.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
fun LiquidText(
    label: String,
    measure: String,
    valueVar: String,
    getValue: (String) -> Unit,
    focus: FocusManager
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "$label input box" }
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
        value = valueVar,
        onValueChange = { getValue(it) },
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

@Composable
fun SliderTextInput(
    modifier: Modifier = Modifier,
    label: String,
    measure: String,
    additiveRatio: String,
    valueVar: String,
    getValue: (String) -> Unit,
    focus: FocusManager,
    getSliderValue: (Double) -> Unit,
    pgRatio: Int
) {
    var vgRatio by remember { mutableIntStateOf(50) }
    var valueRange by remember { mutableStateOf(0f..100f) }
    vgRatio = if (additiveRatio.toIntOrNull()!=null) {
        100-pgRatio-additiveRatio.toInt()
    } else {
        100-pgRatio
    }
    if (vgRatio<0) {
        getSliderValue(valueRange.endInclusive.toDouble())
    }
    valueRange = if(additiveRatio.toFloatOrNull()!=null && additiveRatio.toFloat()<=100) {
        0f..100f-additiveRatio.toFloat()
    } else {
        0f..100f
    }
    Row(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(3f),
            value = valueVar,
            onValueChange = { getValue(it) },
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
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 14.dp)
                    .padding(bottom = 2.dp),
                text = "$pgRatio PG"
            )
            Text(
                text = if (vgRatio>=0) {
                    "$vgRatio VG"
                } else {
                    "0 VG"
                }
            )
            if (additiveRatio!=""){
                Text(
                    text = "$additiveRatio A"
                )
            }
        }
    }
    Slider(
        modifier = Modifier
            .fillMaxWidth()
            .focusProperties { canFocus=false }
            .semantics { contentDescription = "Localized Description" }
            .padding(horizontal = 16.dp),
        value = pgRatio.toFloat(),
        onValueChange = { newValue ->
            getSliderValue(newValue.toDouble())
        },
        valueRange = valueRange,
        onValueChangeFinished = {
        },
    )
}