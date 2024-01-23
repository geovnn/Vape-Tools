package com.geovnn.vapetools.ui.screens.coil_screen

import androidx.lifecycle.ViewModel
import com.geovnn.vapetools.safeStringToDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.PI

class CoilViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CoilUiState())
    val uiState: StateFlow<CoilUiState> = _uiState.asStateFlow()
    fun updateNumberOfCoils(numberOfCoils:String) {
        _uiState.update { currentState ->
            currentState.copy(
                currentNumberOfCoils = numberOfCoils
            )
        }
    }

    fun updateMaterial(material:String) {
        _uiState.update { currentState ->
            currentState.copy(
                currentMaterial = material
            )
        }
    }

    fun updateWireDiameter(wireDiameter:String) {
        val normalizedString = safeStringToDouble(wireDiameter)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentWireDiameter = normalizedString,
                    isWireDiameterEmpty = false,
                )
            }
        }
    }

    fun updateLegLength(legLength:String) {
        val normalizedString = safeStringToDouble(legLength)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentLegLength = normalizedString,
                    isLegLengthEmpty = false
                )
            }
        }
    }

    fun updateCoilDiameter(coilDiameter:String) {
        val normalizedString = safeStringToDouble(coilDiameter)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentCoilDiameter = normalizedString,
                    isCoilDiameterEmpty = false
                )
            }
        }
    }

    fun updateWraps(wraps:String) {
        val normalizedString = safeStringToDouble(wraps)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentWraps = normalizedString,
                    isWrapsEmpty = false
                )
            }
        }
    }

    fun updateResistance(resistance:String) {
        val normalizedString = safeStringToDouble(resistance)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentResistance = normalizedString
                )
            }
        }
    }

    fun updateIsResistanceFocused(value:Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isResistanceFocused = value
            )
        }
    }



    fun clickButton() {
        if (_uiState.value.currentWireDiameter == "") {
            _uiState.update { currentState ->
                currentState.copy(
                    isWireDiameterEmpty = true
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isWireDiameterEmpty = false
                )
            }
        }

        if (_uiState.value.currentLegLength == "") {
            _uiState.update { currentState ->
                currentState.copy(
                    isLegLengthEmpty = true
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isLegLengthEmpty = false
                )
            }
        }

        if (_uiState.value.currentCoilDiameter == "") {
            _uiState.update { currentState ->
                currentState.copy(
                    isCoilDiameterEmpty = true
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isCoilDiameterEmpty = false
                )
            }
        }

        if (_uiState.value.currentWraps=="" && _uiState.value.currentResistance!="" || _uiState.value.isResistanceFocused && _uiState.value.currentResistance!="") {
            val wraps = calculateWraps(
                _uiState.value.currentResistance.toDouble(),
                _uiState.value.currentWireDiameter.toDouble(),
                _uiState.value.currentMaterial,
                _uiState.value.currentLegLength.toDouble(),
                _uiState.value.currentCoilDiameter.toDouble(),
                _uiState.value.currentNumberOfCoils
            )
            updateWraps(wraps)

        } else {
            if (_uiState.value.currentWraps == "") {
                _uiState.update { currentState ->
                    currentState.copy(
                        isWrapsEmpty = true,
                    )
                }
            } else {
                val resistance = calculateResistance(
                    _uiState.value.currentWraps.toDouble(),
                    _uiState.value.currentWireDiameter.toDouble(),
                    _uiState.value.currentMaterial,
                    _uiState.value.currentLegLength.toDouble(),
                    _uiState.value.currentCoilDiameter.toDouble(),
                    _uiState.value.currentNumberOfCoils
                )
                updateResistance(resistance)

            }
        }
    }

    private fun calculateResistance(
        numberOfTurns: Double,
        wireDiameter: Double,
        material: String,
        legLength: Double,
        internalDiameter: Double,
        coilNumber: String
    ): String {
        val outerDiameter = internalDiameter + 2 * wireDiameter
        val wrapCircumference = PI * outerDiameter
        val totalWireLength = numberOfTurns * wrapCircumference + legLength
        val wireRadius = wireDiameter / 2.0
        val wireCrossSectionArea = PI * wireRadius * wireRadius
        var materialResistivity = 0.0
        when (material) {
            "Kanthal A1" -> materialResistivity = 1.45e-8
            "SS316L" -> materialResistivity = 1.39e-8
            "Ni80" -> materialResistivity = 1.09e-8
        }
        val wireResistancePerUnitLength = materialResistivity / wireCrossSectionArea * 100000
        var totalResistance = wireResistancePerUnitLength * totalWireLength
        when (coilNumber) {
            "Double coil" -> totalResistance /= 2
            "Triple coil" -> totalResistance /= 3
            "Quad coil" -> totalResistance /= 4
        }
        return  String.format("%.2f", totalResistance).replace(",", ".")
    }

    private fun calculateWraps(
        targetResistance: Double,
        wireDiameter: Double,
        material: String,
        legLength: Double,
        internalDiameter: Double,
        coilNumber: String
    ): String {
        val wireRadius = wireDiameter / 2.0
        val wireCrossSectionArea = PI * wireRadius * wireRadius
        var materialResistivity = 0.0
        when (material) {
            "Kanthal A1" -> materialResistivity = 1.45e-8
            "SS316L" -> materialResistivity = 0.75e-8
            "Ni80" -> materialResistivity = 1.09e-8
        }
        val wireResistancePerUnitLength = (materialResistivity * (1.0 / wireCrossSectionArea))*100000
        var tempTargetResistance = targetResistance
        when (coilNumber) {
            "Double coil" -> tempTargetResistance *= 2
            "Triple coil" -> tempTargetResistance *= 3
            "Quad coil" -> tempTargetResistance *= 4
        }
        val resistanceWireLength = tempTargetResistance / wireResistancePerUnitLength
        val totalWireLength = resistanceWireLength - legLength
        val outerDiameter = internalDiameter + 2 * wireDiameter
        val wrapCircumference = PI * outerDiameter
        val numberOfTurns = (totalWireLength / wrapCircumference)
        return String.format("%.1f", numberOfTurns).replace(",", ".")
    }
}

