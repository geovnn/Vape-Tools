package com.geovnn.vapetools.ui.screen.liquid_calculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geovnn.vapetools.combine
import com.geovnn.vapetools.data.model.AromaType
import com.geovnn.vapetools.roundTo2DecimalPlaces
import com.geovnn.vapetools.ui.common.composable.LiquidParametersState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldWithPGVGComboState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldWithSliderState
import com.geovnn.vapetools.ui.screen.liquid_calculator.state.LiquidUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LiquidViewModel: ViewModel() {
    val targetAmountText = MutableStateFlow("")
    val targetAmountPGRatio = MutableStateFlow(50f)
    val targetAromaText = MutableStateFlow("")
    val targetAromaType = MutableStateFlow(AromaType.PG)
    val targetAdditiveText = MutableStateFlow("")
    val targetNicotineStrength = MutableStateFlow("")
    val nicotineShotStrength = MutableStateFlow("")
    val nicotineShotPGRatio = MutableStateFlow(50f)

    val navBarFlow = flowOf(VapeTopAppBarState(title = "Liquid Calculator"))

    val liquidParametersStateFlow = combine(
        targetAmountText,
        targetAmountPGRatio,
        targetAromaText,
        targetAromaType,
        targetAdditiveText,
        targetNicotineStrength,
        nicotineShotStrength,
        nicotineShotPGRatio
    ) { targetAmountText,
        targetAmountPGRatio,
        targetAromaText,
        targetAromaType,
        targetAdditiveText,
        targetNicotineStrength,
        nicotineShotStrength,
        nicotineShotPGRatio ->
        LiquidParametersState(
            amount = VapeTextFieldWithSliderState(
                textFieldLabel = "Target Total Amount",
                textFieldSuffix = "ml",
                textFieldValue = targetAmountText,
                pgRatio = targetAmountPGRatio,
                vgRatio = 100f - targetAmountPGRatio - (targetAdditiveText.toFloatOrNull() ?: 0f),
                additiveRatio = targetAdditiveText.toFloatOrNull() ?: 0f
            ),
            aroma = VapeTextFieldWithPGVGComboState(
                label = "Target aroma level",
                measure = "%",
                textFieldValue = targetAromaText,
                selectedOption = targetAromaType
            ),
            additive = VapeTextFieldState(
                label = "Additive (e.g. water)",
                measureUnit = "%",
                text = targetAdditiveText
            ),
            targetNicotineStrength = VapeTextFieldState(
                label = "Target nicotine strength",
                measureUnit = "mg/ml",
                text = targetNicotineStrength
            ),
            nicotineShotStrength = VapeTextFieldWithSliderState(
                textFieldLabel = "Nicotine shot strength",
                textFieldSuffix = "mg/ml",
                textFieldValue = nicotineShotStrength,
                pgRatio = nicotineShotPGRatio,
                vgRatio = 100f - nicotineShotPGRatio,
                additiveRatio = 0f
            ),
        )
    }

    val liquidsResultsFlow = liquidParametersStateFlow.map { settings ->
        val targetAmount = settings.amount.textFieldValue.toDoubleOrNull() ?: 0.0
        val targetPGRatio = settings.amount.pgRatio.toDouble()
        val targetVGRatio = settings.amount.vgRatio.toDouble()

        val targetAromaRatio = settings.aroma.textFieldValue.toDoubleOrNull() ?: 0.0
        val targetAromaType = settings.aroma.selectedOption.name

        val targetAdditiveRatio = settings.additive.text.toDoubleOrNull() ?: 0.0

        val targetNicotineStrength = settings.targetNicotineStrength.text.toDoubleOrNull() ?: 0.0
        val nicotineShotStrength = settings.nicotineShotStrength.textFieldValue.toDoubleOrNull() ?: 0.0
        val nicotineShotPGRatio = settings.nicotineShotStrength.pgRatio.toDouble()
        val nicotinVGRatio = settings.nicotineShotStrength.vgRatio.toDouble()

        // === Calculations ===
        val nicotineAmount = if (targetNicotineStrength > 0 && nicotineShotStrength > 0) {
            (targetAmount * targetNicotineStrength) / nicotineShotStrength
        } else 0.0

        val aromaAmount = targetAmount * (targetAromaRatio / 100)
        val aromaPGAmount = if (targetAromaType == "PG") aromaAmount else 0.0
        val aromaVGAmount = if (targetAromaType == "VG") aromaAmount else 0.0

        val pgAmount = targetAmount * (targetPGRatio / 100) - (nicotineAmount * (nicotineShotPGRatio / 100)) - aromaPGAmount
        val vgAmount = targetAmount * (targetVGRatio / 100) - (nicotineAmount * (nicotinVGRatio / 100)) - aromaVGAmount
        val additiveAmount = targetAmount * (targetAdditiveRatio / 100)

        // === Build ingredient list ===
        val ingredients = buildList {
            if (pgAmount!=0.0) add(
                LiquidUiState.LiquidsResultsBoxState.Ingredient(
                    name = "PG",
                    volume = "${pgAmount.roundTo2DecimalPlaces()} ml",
                    weight = "${pgVolumeToWeight(pgAmount).roundTo2DecimalPlaces()} g"
                ))
            if (vgAmount!=0.0) add(
                LiquidUiState.LiquidsResultsBoxState.Ingredient(
                    name = "VG",
                    volume = "${vgAmount.roundTo2DecimalPlaces()} ml",
                    weight = "${vgVolumeToWeight(vgAmount).roundTo2DecimalPlaces()} g"
                ))
            if (aromaAmount != 0.0) add(
                LiquidUiState.LiquidsResultsBoxState.Ingredient(
                    name = "Aroma ($targetAromaType)",
                    volume = "${aromaAmount.roundTo2DecimalPlaces()} ml",
                    weight = "${
                        (
                            if (targetAromaType == "PG") pgVolumeToWeight(volume = aromaAmount)
                            else vgVolumeToWeight(volume = aromaAmount)
                                ).roundTo2DecimalPlaces()
                            } g"
                ))
            if (additiveAmount != 0.0) add(
                LiquidUiState.LiquidsResultsBoxState.Ingredient(
                    name = "Additive",
                    volume = "${additiveAmount.roundTo2DecimalPlaces()} ml",
                    weight = ""
                ))
            if (nicotineAmount != 0.0) add(
                LiquidUiState.LiquidsResultsBoxState.Ingredient(
                    name = "Nicotine shot",
                    volume = "${nicotineAmount.roundTo2DecimalPlaces()} ml",
                    weight = "${pgVgRatioToWeight(pgRatio = nicotineShotPGRatio, amount = nicotineAmount).roundTo2DecimalPlaces()} g"
                ))
        }
        val isError = pgAmount < 0 || vgAmount < 0 || aromaAmount < 0 || additiveAmount < 0 || nicotineAmount < 0
        LiquidUiState.LiquidsResultsBoxState(
            title = "Results",
            description = when {
                isError -> "This liquid cannot be made with the given parameters"
                ingredients.isEmpty() -> "Fill more inputs to see the recipe"
                else -> "To make this liquid you need:"
            },
            ingredients = ingredients,
            isError = isError
        )
    }

    val uiState = kotlinx.coroutines.flow.combine(
        navBarFlow,
        liquidParametersStateFlow,
        liquidsResultsFlow,
        ::LiquidUiState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = LiquidUiState()
    )

    fun updateTargetTotalAmount(total: String) {
        when {
            total.isEmpty() -> targetAmountText.value = ""
            total.toDoubleOrNull() != null -> targetAmountText.value = total
        }
    }

    fun updateTargetNicotineStrength(nicotineStrength: String) {
        when {
            nicotineStrength.isEmpty() -> targetNicotineStrength.value = ""
            nicotineStrength.toDoubleOrNull() != null -> targetNicotineStrength.value = nicotineStrength
        }
    }

    fun updateAromaRatio(aroma: String) {
        when {
            aroma.isEmpty() -> targetAromaText.value = ""
            aroma.toDoubleOrNull() != null && aroma.toDouble() <= 100f -> targetAromaText.value = aroma
        }
    }

    fun updateAromaOption(aromaOption: AromaType) {
        targetAromaType.value = aromaOption
    }

    fun updateAdditiveRatio(additive: String) {
        when {
            additive.isEmpty() -> targetAdditiveText.value = ""
            additive.toDoubleOrNull() != null && additive.toDouble() <= 99f -> targetAdditiveText.value = additive
        }
    }

    fun updateNicotineShotStrength(strength: String) {
        when {
            strength.isEmpty() -> nicotineShotStrength.value = ""
            strength.toDoubleOrNull() != null -> nicotineShotStrength.value = strength
        }
    }

    fun changeTargetPgRatio(sliderValue: Float) {
        targetAmountPGRatio.value = sliderValue
    }

    fun changeNicotinePgRatio(sliderValue: Float ) {
        nicotineShotPGRatio.value = sliderValue
    }

    private fun pgVgRatioToWeight(pgRatio: Double, amount: Double): Double {
        val pgVolume = amount * (pgRatio / 100)
        val vgVolume = amount * ((100 - pgRatio) / 100)
        return pgVolumeToWeight(pgVolume) + vgVolumeToWeight(vgVolume)
    }

    private fun pgVolumeToWeight(volume: Double): Double {
        return volume * 1.036 // Density of PG in g/ml
    }

    private fun vgVolumeToWeight(volume: Double): Double {
        return volume * 1.26 // Density of VG in g/ml
    }

//    private fun Double.roundTo2DecimalPlaces(): Double {
//        return (this * 100).roundToInt() / 100.0
//    }
}