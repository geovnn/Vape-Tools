package com.geovnn.vapetools.ui.screen.coil_calculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geovnn.vapetools.R
import com.geovnn.vapetools.helper.combine
import com.geovnn.vapetools.data.model.CoilMaterial
import com.geovnn.vapetools.data.model.CoilType
import com.geovnn.vapetools.helper.UiText
import com.geovnn.vapetools.helper.roundTo1DecimalPlaces
import com.geovnn.vapetools.ui.common.composable.VapeButtonState
import com.geovnn.vapetools.ui.common.composable.VapeDropdownMenuState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState
import com.geovnn.vapetools.ui.screen.coil_calculator.state.CoilUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlin.math.PI

class CoilViewModel : ViewModel() {

    companion object {
        val WRAPS_ID = 1
        val RESISTANCE_ID = 2
    }

    val setupType = MutableStateFlow(CoilType.entries.first())
    val material = MutableStateFlow(CoilMaterial.entries.first())
    val wireDiameter = MutableStateFlow("")
    val isWireDiameterError = MutableStateFlow(false)
    val legLength = MutableStateFlow("")
    val isLegLengthError = MutableStateFlow(false)
    val innerDiameter = MutableStateFlow("")
    val isInnerDiameterError = MutableStateFlow(false)
    val wraps = MutableStateFlow("")
    val isWrapsError = MutableStateFlow(false)
    val ohms = MutableStateFlow("")
    val isOhmsError = MutableStateFlow(false)

    val lastSelectedField = MutableStateFlow(WRAPS_ID)
    val navBarFlow = flowOf(VapeTopAppBarState(title = UiText.StringResource(R.string.coil_calculator_screen_title)))

    val contentFlow = combine(
        setupType,
        material,
        wireDiameter,
        isWireDiameterError,
        legLength,
        isLegLengthError,
        innerDiameter,
        isInnerDiameterError,
        wraps,
        isWrapsError,
        ohms,
        isOhmsError,
    ) { setupType,
        material,
        wireDiameter,
        isWireDiameterError,
        legLength,
        isLegLengthError,
        innerDiameter,
        isInnerDiameterError,
        wraps,
        isWrapsError,
        ohms,
        isOhmsError,
        ->
        CoilUiState.Content(
            setupType = VapeDropdownMenuState(
                options = CoilType.entries .map {
                    val label = when (it) {
                        CoilType.SINGLE -> UiText.StringResource(R.string.coil_type_single)
                        CoilType.PARALLEL -> UiText.StringResource(R.string.coil_type_parallel)
                        CoilType.TWIN -> UiText.StringResource(R.string.coil_type_twin)
                        CoilType.TRIPLE -> UiText.StringResource(R.string.coil_type_triple)
                        CoilType.QUAD -> UiText.StringResource(R.string.coil_type_quad)
                    }
                    VapeDropdownMenuState.Option(
                        id = it.name,
                        label = label
                    )
                },
                label = UiText.StringResource(R.string.coil_calculator_label_coil_type),
                text = when (setupType) {
                    CoilType.SINGLE -> UiText.StringResource(R.string.coil_type_single)
                    CoilType.PARALLEL -> UiText.StringResource(R.string.coil_type_parallel)
                    CoilType.TWIN -> UiText.StringResource(R.string.coil_type_twin)
                    CoilType.TRIPLE -> UiText.StringResource(R.string.coil_type_triple)
                    CoilType.QUAD -> UiText.StringResource(R.string.coil_type_quad)
                }
            ),
            material = VapeDropdownMenuState(
                options = CoilMaterial.entries .map {
                    val label = when (it) {
                        CoilMaterial.KANTHAL_A1 -> UiText.StringResource(R.string.material_kanthal_a1)
                        CoilMaterial.STAINLESS_STEEL_316L -> UiText.StringResource(R.string.material_ss316l)
                        CoilMaterial.NICHROME_80 -> UiText.StringResource(R.string.material_ni80)
                    }
                    VapeDropdownMenuState.Option(
                        id = it.name,
                        label = label
                    )
                },
                label = UiText.StringResource(R.string.coil_calculator_label_coil_material),
                text = when (material) {
                    CoilMaterial.KANTHAL_A1 -> UiText.StringResource(R.string.material_kanthal_a1)
                    CoilMaterial.STAINLESS_STEEL_316L -> UiText.StringResource(R.string.material_ss316l)
                    CoilMaterial.NICHROME_80 -> UiText.StringResource(R.string.material_ni80)
                } ,
            ),
            wireDiameter = VapeTextFieldState(
                label = UiText.StringResource(R.string.coil_calculator_label_wire_diameter),
                measureUnit = UiText.StringResource(R.string.unit_mm),
                text = wireDiameter,
                isError = isWireDiameterError
            ),
            legLength = VapeTextFieldState(
                label = UiText.StringResource(R.string.coil_calculator_label_leg_length),
                measureUnit = UiText.StringResource(R.string.unit_mm),
                text = legLength,
                isError = isLegLengthError
            ),
            innerDiameter = VapeTextFieldState(
                label = UiText.StringResource(R.string.coil_calculator_label_coil_inner_diameter),
                measureUnit = UiText.StringResource(R.string.unit_mm),
                text = innerDiameter,
                isError = isInnerDiameterError
            ),
            wraps = VapeTextFieldState(
                label = UiText.StringResource(R.string.coil_calculator_label_wraps),
                text = wraps,
                isError = isWrapsError
            ),
            ohms = VapeTextFieldState(
                label = UiText.StringResource(R.string.coil_calculator_label_ohms),
                measureUnit = UiText.StringResource(R.string.unit_ohm),
                text = ohms,
                isError = isOhmsError
            ),
            calculateButton = VapeButtonState(text = UiText.StringResource(R.string.label_calculate))
        )
    }

    val uiState = kotlinx.coroutines.flow.combine(
        navBarFlow,
        contentFlow,
        ::CoilUiState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = CoilUiState()
    )

    fun updateSetupType(numberOfCoils: VapeDropdownMenuState.Option) {
        setupType.value = CoilType.valueOf(numberOfCoils.id)
    }

    fun updateMaterial(value: VapeDropdownMenuState.Option) {
        material.value = CoilMaterial.valueOf(value.id)
    }

    fun updateWireDiameter(value:String) {
        when {
            value.isEmpty() || value.toDoubleOrNull()!=null -> {
                wireDiameter.value = value
                isWireDiameterError.value = false
            }
        }
    }

    fun updateLegLength(value:String) {
        when {
            value.isEmpty() || value.toDoubleOrNull() != null -> {
                legLength.value = value
                isLegLengthError.value = false
            }
        }
    }

    fun updateInnerDiameter(value:String) {
        when {
            value.isEmpty() || value.toDoubleOrNull() != null -> {
                innerDiameter.value = value
                isInnerDiameterError.value = false
            }
        }
    }

    fun updateWraps(value:String) {
        when {
            value.isEmpty() || value.toDoubleOrNull() != null -> {
                wraps.value = value
                isWrapsError.value = false
            }
        }
    }

    fun updateResistance(value:String) {
        when {
            value.isEmpty() || value.toDoubleOrNull() != null -> {
                ohms.value = value
                isOhmsError.value = false
            }
        }
    }

    fun selectWraps() {
        lastSelectedField.value = WRAPS_ID
    }

    fun selectResistance() {
        lastSelectedField.value = RESISTANCE_ID
    }


    fun clickButton() {
        val shouldCalculateWraps = lastSelectedField.value == RESISTANCE_ID
        if (shouldCalculateWraps) {
            val safeWireDiameter = wireDiameter.value.toDoubleOrNull()
            val safeLegLength = legLength.value.toDoubleOrNull()
            val safeInnerDiameter = innerDiameter.value.toDoubleOrNull()
            val safeOhm = ohms.value.toDoubleOrNull()
            isWrapsError.value=false
            if (safeWireDiameter == null || safeLegLength == null || safeInnerDiameter == null || safeOhm == null) {
                if (safeWireDiameter == null) isWireDiameterError.value = true
                if (safeLegLength == null) isLegLengthError.value = true
                if (safeInnerDiameter == null) isInnerDiameterError.value = true
                if (safeOhm == null) isOhmsError.value = true
                return
            } else {
                val wraps = calculateWraps(
                    targetResistance = safeOhm,
                    wireDiameter = safeWireDiameter,
                    material = material.value,
                    legLength = safeLegLength,
                    internalDiameter = safeInnerDiameter,
                    coilNumber = setupType.value
                )
                updateWraps(wraps)
            }
        } else {
            val safeWireDiameter = wireDiameter.value.toDoubleOrNull()
            val safeLegLength = legLength.value.toDoubleOrNull()
            val safeInnerDiameter = innerDiameter.value.toDoubleOrNull()
            val safeWraps = wraps.value.toDoubleOrNull()
            isOhmsError.value=false

            if (safeWireDiameter == null || safeLegLength == null || safeInnerDiameter == null || safeWraps == null) {
                if (safeWireDiameter == null) isWireDiameterError.value = true
                if (safeLegLength == null) isLegLengthError.value = true
                if (safeInnerDiameter == null) isInnerDiameterError.value = true
                if (safeWraps == null) isWrapsError.value = true
                return
            } else {
                val wraps = calculateResistance(
                    wireDiameter = safeWireDiameter,
                    material = material.value,
                    legLength = safeLegLength,
                    internalDiameter = safeInnerDiameter,
                    coilNumber = setupType.value,
                    wraps = safeWraps
                )
                updateResistance(wraps)
            }
        }
    }

    private fun calculateResistance(
        wraps: Double,
        wireDiameter: Double,
        material: CoilMaterial,
        legLength: Double,
        internalDiameter: Double,
        coilNumber: CoilType
    ): String {
        val outerDiameter = internalDiameter + 2 * wireDiameter
        val wrapCircumference = PI * outerDiameter
        val totalWireLength = wraps * wrapCircumference + legLength
        val wireRadius = wireDiameter / 2.0
        val wireCrossSectionArea = PI * wireRadius * wireRadius
        var materialResistivity = 0.0
        materialResistivity = when (material) {
            CoilMaterial.KANTHAL_A1 -> 1.45e-8
            CoilMaterial.STAINLESS_STEEL_316L -> 1.39e-8
            CoilMaterial.NICHROME_80 -> 1.09e-8
        }
        val wireResistancePerUnitLength = materialResistivity / wireCrossSectionArea * 100000
        var totalResistance = wireResistancePerUnitLength * totalWireLength
        totalResistance /= when (coilNumber) {
            CoilType.SINGLE -> 1
            CoilType.PARALLEL -> 2
            CoilType.TWIN -> 2
            CoilType.TRIPLE -> 3
            CoilType.QUAD -> 4
        }
        return totalResistance.roundTo1DecimalPlaces()
    }

    private fun calculateWraps(
        targetResistance: Double,
        wireDiameter: Double,
        material: CoilMaterial,
        legLength: Double,
        internalDiameter: Double,
        coilNumber: CoilType
    ): String {
        val wireRadius = wireDiameter / 2.0
        val wireCrossSectionArea = PI * wireRadius * wireRadius
        var materialResistivity = 0.0
        materialResistivity = when (material) {
            CoilMaterial.KANTHAL_A1 -> 1.45e-8
            CoilMaterial.STAINLESS_STEEL_316L -> 0.75e-8
            CoilMaterial.NICHROME_80 -> 1.09e-8
        }
        val wireResistancePerUnitLength = (materialResistivity * (1.0 / wireCrossSectionArea))*100000
        var tempTargetResistance = targetResistance
        when (coilNumber) {
            CoilType.SINGLE -> tempTargetResistance *= 1
            CoilType.PARALLEL -> tempTargetResistance *= 2
            CoilType.TWIN -> tempTargetResistance *= 2
            CoilType.TRIPLE -> tempTargetResistance *= 3
            CoilType.QUAD -> tempTargetResistance *= 4
        }
        val resistanceWireLength = tempTargetResistance / wireResistancePerUnitLength
        val totalWireLength = resistanceWireLength - legLength
        val outerDiameter = internalDiameter + 2 * wireDiameter
        val wrapCircumference = PI * outerDiameter
        val numberOfTurns = (totalWireLength / wrapCircumference)
        return numberOfTurns.roundTo1DecimalPlaces()
    }
}