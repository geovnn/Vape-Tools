package com.geovnn.vapetools.ui.screen.ohm_calculator.viewmodel

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geovnn.vapetools.R
import com.geovnn.vapetools.helper.UiText
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState
import com.geovnn.vapetools.ui.screen.ohm_calculator.state.OhmUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.util.Locale
import kotlin.math.sqrt

class OhmViewModel: ViewModel() {
    private val navBarFlow = flowOf(VapeTopAppBarState(title = UiText.StringResource(R.string.ohm_calculator_screen_title)))

    private val listState = MutableStateFlow<List<OhmUiState.FieldIDs>>(emptyList())
    val voltage = MutableStateFlow("")
    val resistance = MutableStateFlow("")
    val current = MutableStateFlow("")
    val wattage = MutableStateFlow("")


    val contentFlow = combine(
        voltage,
        resistance,
        current,
        wattage,
        listState
    ) { voltage, resistance, current, wattage, lockedList ->
        OhmUiState.Content(
            currentVoltage = OhmUiState.Content.Field(
                id = OhmUiState.FieldIDs.VOLTAGE,
                textField = VapeTextFieldState(
                    label = UiText.StringResource(R.string.label_voltage),
                    measureUnit = UiText.StringResource(R.string.unit_v),
                    text = voltage,
                ),
                locked = lockedList.contains(OhmUiState.FieldIDs.VOLTAGE)
            ),
            currentResistance = OhmUiState.Content.Field(
                id = OhmUiState.FieldIDs.RESISTANCE,
                textField = VapeTextFieldState(
                    label = UiText.StringResource(R.string.label_resistance),
                    measureUnit = UiText.StringResource(R.string.unit_ohm),
                    text = resistance,
                ),
                locked = lockedList.contains(OhmUiState.FieldIDs.RESISTANCE)
            ),
            currentCurrent = OhmUiState.Content.Field(
                id = OhmUiState.FieldIDs.CURRENT,
                textField = VapeTextFieldState(
                    label = UiText.StringResource(R.string.label_current),
                    measureUnit = UiText.StringResource(R.string.unit_a),
                    text = current,
                ),
                locked = lockedList.contains(OhmUiState.FieldIDs.CURRENT)
            ),
            currentWattage = OhmUiState.Content.Field(
                id = OhmUiState.FieldIDs.WATTAGE,
                textField = VapeTextFieldState(
                    label = UiText.StringResource(R.string.label_wattage),
                    measureUnit = UiText.StringResource(R.string.unit_wattage),
                    text = wattage,
                ),
                locked = lockedList.contains(OhmUiState.FieldIDs.WATTAGE)
            )
        )
    }

    val uiState: StateFlow<OhmUiState> = combine(
        navBarFlow,
        contentFlow
    ) { navBar, content ->
        OhmUiState(
            topAppBar = navBar,
            content = content
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = OhmUiState()
    )


    fun updateVoltage(value:String) {
        when {
            value.isEmpty() -> voltage.value = ""
            value.toDoubleOrNull() != null -> voltage.value = value
        }
    }

    fun updateResistance(value:String) {
        when {
            value.isEmpty() -> resistance.value = ""
            value.toDoubleOrNull() != null -> resistance.value = value
        }
    }

    fun updateCurrent(value:String) {
        when {
            value.isEmpty() -> current.value = ""
            value.toDoubleOrNull() != null -> current.value = value
        }
    }

    fun updateWattage(value:String) {
        when {
            value.isEmpty() -> wattage.value = ""
            value.toDoubleOrNull() != null -> wattage.value = value
        }
    }

    fun addToSelectionList(item: OhmUiState.FieldIDs) {
        val tempListState = listState.value.toMutableStateList()
        if (tempListState.size <= 1 && !tempListState.contains(item)) {
            tempListState.add(item)
        } else if (!tempListState.contains(item)) {
            tempListState.removeAt(tempListState.size - 2)
            tempListState.add(item)
        }
        listState.value = tempListState
    }

    fun calculateOhm() {
        val format="%.2f"
        val tempVoltage = voltage.value.toDoubleOrNull()
        val tempResistance = resistance.value.toDoubleOrNull()
        val tempCurrent = current.value.toDoubleOrNull()
        val tempWattage = wattage.value.toDoubleOrNull()
        val selectionList = listState.value
        if (
            selectionList.contains(OhmUiState.FieldIDs.VOLTAGE)
            && tempVoltage!=null && tempVoltage>0.0
            && selectionList.contains(OhmUiState.FieldIDs.RESISTANCE)
            && tempResistance!=null && tempResistance>0.0
            ) {
            val returnCurrent=tempVoltage/tempResistance
            val returnWattage=(tempVoltage*tempVoltage)/tempResistance
            current.value= String.Companion.format(Locale.ENGLISH,format,returnCurrent)
            wattage.value= String.Companion.format(Locale.ENGLISH,format,returnWattage)
        }
        if (
            selectionList.contains(OhmUiState.FieldIDs.VOLTAGE)
            && tempVoltage!=null && tempVoltage>0.0
            && selectionList.contains(OhmUiState.FieldIDs.CURRENT)
            && tempCurrent!=null && tempCurrent>0.0
            ) {
            val returnResistance=tempVoltage/tempCurrent
            val returnWattage=tempVoltage*tempCurrent
            resistance.value= String.Companion.format(Locale.ENGLISH,format,returnResistance)
            wattage.value= String.Companion.format(Locale.ENGLISH,format,returnWattage)
        }
        if (
            selectionList.contains(OhmUiState.FieldIDs.VOLTAGE)
            && tempVoltage!=null && tempVoltage>0.0
            && selectionList.contains(OhmUiState.FieldIDs.WATTAGE)
            && tempWattage!=null && tempWattage>0.0
            ) {
            val returnResistance=(tempVoltage*tempVoltage)/tempWattage
            val returnCurrent=tempWattage/tempVoltage
            resistance.value= String.Companion.format(Locale.ENGLISH,format,returnResistance)
            current.value= String.Companion.format(Locale.ENGLISH,format,returnCurrent)
        }
        if (
            selectionList.contains(OhmUiState.FieldIDs.RESISTANCE)
            && tempResistance!=null && tempResistance>0.0
            && selectionList.contains(OhmUiState.FieldIDs.CURRENT)
            && tempCurrent!=null && tempCurrent>0.0
            ) {
            val returnVoltage=tempResistance*tempCurrent
            val returnWattage=(tempCurrent*tempCurrent)*tempResistance
            voltage.value= String.Companion.format(Locale.ENGLISH,format,returnVoltage)
            wattage.value= String.Companion.format(Locale.ENGLISH,format,returnWattage)
        }
        if (
            selectionList.contains(OhmUiState.FieldIDs.RESISTANCE)
            && tempResistance!=null && tempResistance>0.0
            && selectionList.contains(OhmUiState.FieldIDs.WATTAGE)
            && tempWattage!=null && tempWattage>0.0
            ) {
            val returnVoltage= sqrt(tempWattage * tempResistance)
            val returnCurrent= sqrt(tempWattage / tempResistance)
            voltage.value= String.Companion.format(Locale.ENGLISH,format,returnVoltage)
            current.value= String.Companion.format(Locale.ENGLISH,format,returnCurrent)
        }
        if (
            selectionList.contains(OhmUiState.FieldIDs.CURRENT)
            && tempCurrent!=null && tempCurrent>0.0
            && selectionList.contains(OhmUiState.FieldIDs.WATTAGE)
            && tempWattage!=null && tempWattage>0.0
            ) {
            val returnVoltage=tempWattage/tempCurrent
            val returnResistance=tempWattage/(tempCurrent*tempCurrent)
            voltage.value= String.Companion.format(Locale.ENGLISH,format,returnVoltage)
            resistance.value= String.Companion.format(Locale.ENGLISH,format,returnResistance)
        }
    }
}