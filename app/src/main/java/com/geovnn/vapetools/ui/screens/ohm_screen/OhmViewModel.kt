package com.geovnn.vapetools.ui.screens.ohm_screen

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.geovnn.vapetools.safeStringToDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import kotlin.math.sqrt

class OhmViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(OhmUiState())
    val uiState: StateFlow<OhmUiState> = _uiState.asStateFlow()
    private val listState = mutableStateListOf<String>()
    fun updateVoltage(voltage:String) {
        val normalizedString = safeStringToDouble(voltage)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentVoltage = normalizedString,
                )
            }
        }
    }

    fun updateResistance(resistance:String) {
        val normalizedString = safeStringToDouble(resistance)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentResistance = normalizedString,
                )
            }
        }
    }

    fun updateCurrent(current:String) {
        val normalizedString = safeStringToDouble(current)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentCurrent = normalizedString,
                )
            }
        }
    }

    fun updateWattage(wattage:String) {
        val normalizedString = safeStringToDouble(wattage)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentWattage = normalizedString,
                )
            }
        }
    }

    fun addToSelectionList(label: String) {
        if (listState.size <= 1 && !listState.contains(label)) {
            listState.add(label)
        } else if (!listState.contains(label)) {
            listState.removeAt(listState.size - 2)
            listState.add(label)
        }
        _uiState.update { currentState ->
            currentState.copy(
                lastSelectedList = listState,
            )
        }
    }

    fun calculateOhm() {
        val format="%.2f"
        if (listState.contains("Voltage") && _uiState.value.currentVoltage!="" && listState.contains("Resistance") && _uiState.value.currentResistance!="") {
            val returnCurrent=_uiState.value.currentVoltage.toDouble()/_uiState.value.currentResistance.toDouble()
            val returnWattage=(_uiState.value.currentVoltage.toDouble()*_uiState.value.currentVoltage.toDouble())/_uiState.value.currentResistance.toDouble()
            _uiState.update { currentState ->
                currentState.copy(
                    currentCurrent = String.format(Locale.ENGLISH,format,returnCurrent),
                    currentWattage = String.format(Locale.ENGLISH,format,returnWattage)
                )
            }
        }
        if (listState.contains("Voltage") && _uiState.value.currentVoltage!="" && listState.contains("Ampere") && _uiState.value.currentCurrent!="") {
            val returnResistance=_uiState.value.currentVoltage.toDouble()/_uiState.value.currentCurrent.toDouble()
            val returnWattage=_uiState.value.currentVoltage.toDouble()*_uiState.value.currentCurrent.toDouble()
            _uiState.update { currentState ->
                currentState.copy(
                    currentResistance = String.format(Locale.ENGLISH,format,returnResistance),
                    currentWattage = String.format(Locale.ENGLISH,format,returnWattage)
                )
            }
        }
        if (listState.contains("Voltage") && _uiState.value.currentVoltage!="" && listState.contains("Wattage") && _uiState.value.currentWattage!="") {
            val returnResistance=(_uiState.value.currentVoltage.toDouble()*_uiState.value.currentVoltage.toDouble())/_uiState.value.currentWattage.toDouble()
            val returnCurrent=_uiState.value.currentWattage.toDouble()/_uiState.value.currentVoltage.toDouble()
            _uiState.update { currentState ->
                currentState.copy(
                    currentResistance = String.format(Locale.ENGLISH,format,returnResistance),
                    currentCurrent = String.format(Locale.ENGLISH,format,returnCurrent)
                )
            }
        }
        if (listState.contains("Resistance") && _uiState.value.currentResistance!="" && listState.contains("Ampere") && _uiState.value.currentCurrent!="") {
            val returnVoltage=_uiState.value.currentResistance.toDouble()*_uiState.value.currentCurrent.toDouble()
            val returnWattage=(_uiState.value.currentCurrent.toDouble()*_uiState.value.currentCurrent.toDouble())*_uiState.value.currentResistance.toDouble()
            _uiState.update { currentState ->
                currentState.copy(
                    currentVoltage = String.format(Locale.ENGLISH,format,returnVoltage),
                    currentWattage = String.format(Locale.ENGLISH,format,returnWattage)
                )
            }
        }
        if (listState.contains("Resistance") && _uiState.value.currentResistance!="" && listState.contains("Wattage") && _uiState.value.currentWattage!="") {
            val returnVoltage= sqrt(_uiState.value.currentWattage.toDouble()*_uiState.value.currentResistance.toDouble())
            val returnCurrent= sqrt(_uiState.value.currentWattage.toDouble()/_uiState.value.currentResistance.toDouble())
            _uiState.update { currentState ->
                currentState.copy(
                    currentVoltage = String.format(Locale.ENGLISH,format,returnVoltage),
                    currentCurrent = String.format(Locale.ENGLISH,format,returnCurrent)
                )
            }
        }
        if (listState.contains("Ampere") && _uiState.value.currentCurrent!="" && listState.contains("Wattage") && _uiState.value.currentWattage!="") {
            val returnVoltage=_uiState.value.currentWattage.toDouble()/_uiState.value.currentCurrent.toDouble()
            val returnResistance=_uiState.value.currentWattage.toDouble()/(_uiState.value.currentCurrent.toDouble()*_uiState.value.currentCurrent.toDouble())
            _uiState.update { currentState ->
                currentState.copy(
                    currentVoltage = String.format(Locale.ENGLISH,format,returnVoltage),
                    currentResistance = String.format(Locale.ENGLISH,format,returnResistance)
                )
            }
        }
    }
}