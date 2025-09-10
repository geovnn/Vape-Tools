package com.geovnn.vapetools.ui.screen.liquid_calculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geovnn.vapetools.R
import com.geovnn.vapetools.helper.combine
import com.geovnn.vapetools.data.model.AromaType
import com.geovnn.vapetools.helper.UiText
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

    val navBarFlow = flowOf(VapeTopAppBarState(title = UiText.StringResource(R.string.liquid_calculator_screen_title)))

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
                textFieldLabel = UiText.StringResource(R.string.label_target_total_amount),
                textFieldSuffix = UiText.StringResource(R.string.unit_ml),
                textFieldValue = targetAmountText,
                pgRatio = targetAmountPGRatio,
                vgRatio = 100f - targetAmountPGRatio - (targetAdditiveText.toFloatOrNull() ?: 0f),
                additiveRatio = targetAdditiveText.toFloatOrNull() ?: 0f
            ),
            aroma = VapeTextFieldWithPGVGComboState(
                label = UiText.StringResource(R.string.label_target_aroma_percentage),
                measure =UiText.StringResource(R.string.unit_percentage),
                textFieldValue = targetAromaText,
                selectedOption = targetAromaType
            ),
            additive = VapeTextFieldState(
                label = UiText.StringResource(R.string.label_additive_extended),
                measureUnit = UiText.StringResource(R.string.unit_percentage),
                text = targetAdditiveText
            ),
            targetNicotineStrength = VapeTextFieldState(
                label = UiText.StringResource(R.string.label_target_nicotine_strength),
                measureUnit = UiText.StringResource(R.string.unit_mg_ml),
                text = targetNicotineStrength
            ),
            nicotineShotStrength = VapeTextFieldWithSliderState(
                textFieldLabel = UiText.StringResource(R.string.label_nicotine_shot_strength),
                textFieldSuffix = UiText.StringResource(R.string.unit_mg_ml),
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
        val targetAromaType = settings.aroma.selectedOption

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
        val aromaPGAmount = if (targetAromaType == AromaType.PG) aromaAmount else 0.0
        val aromaVGAmount = if (targetAromaType == AromaType.VG) aromaAmount else 0.0

        val pgAmount = targetAmount * (targetPGRatio / 100) - (nicotineAmount * (nicotineShotPGRatio / 100)) - aromaPGAmount
        val vgAmount = targetAmount * (targetVGRatio / 100) - (nicotineAmount * (nicotinVGRatio / 100)) - aromaVGAmount
        val additiveAmount = targetAmount * (targetAdditiveRatio / 100)

        // === Build ingredient list ===
        val ingredients = buildList {
            if (pgAmount!=0.0) add(
                LiquidUiState.LiquidsResultsBoxState.Ingredient(
                    name = UiText.StringResource(R.string.unit_pg),
                    volume = UiText.StringResource(R.string.num_pg_volume,pgAmount),
                    weight = UiText.StringResource(R.string.num_grams,pgVolumeToWeight(pgAmount))
                ))
            if (vgAmount!=0.0) add(
                LiquidUiState.LiquidsResultsBoxState.Ingredient(
                    name = UiText.StringResource(R.string.unit_vg),
                    volume = UiText.StringResource(R.string.num_vg_volume,vgAmount),
                    weight = UiText.StringResource(R.string.num_grams,vgVolumeToWeight(vgAmount))
                )
            )
            if (aromaAmount != 0.0) add(
                LiquidUiState.LiquidsResultsBoxState.Ingredient(
                    name = when (targetAromaType) {
                        AromaType.PG ->  UiText.StringResource(R.string.label_aroma_pg)
                        AromaType.VG -> UiText.StringResource(R.string.label_aroma_vg)
                    },
                    volume = UiText.StringResource(R.string.num_aroma_volume,aromaAmount),
                    weight = UiText.StringResource(R.string.num_grams,
                        if (targetAromaType == AromaType.PG) pgVolumeToWeight(volume = aromaAmount)
                        else vgVolumeToWeight(volume = aromaAmount))
                ))
            if (additiveAmount != 0.0) add(
                LiquidUiState.LiquidsResultsBoxState.Ingredient(
                    name = UiText.StringResource(R.string.label_additive_extended),
                    volume =  UiText.StringResource(R.string.unit_ml,additiveAmount),
                    weight = null
                ))
            if (nicotineAmount != 0.0) add(
                LiquidUiState.LiquidsResultsBoxState.Ingredient(
                    name = UiText.StringResource(R.string.label_nicotine_shot),
                    volume = UiText.StringResource(R.string.num_nicotine_volume,nicotineAmount),
                    weight = UiText.StringResource(R.string.num_grams,pgVgRatioToWeight(pgRatio = nicotineShotPGRatio, amount = nicotineAmount))
                ))
        }
        val isError = pgAmount < 0 || vgAmount < 0 || aromaAmount < 0 || additiveAmount < 0 || nicotineAmount < 0
        LiquidUiState.LiquidsResultsBoxState(
            title = UiText.StringResource(R.string.label_results),
            description = when {
                isError -> UiText.StringResource(R.string.error_liquid_invalid_input)
                ingredients.isEmpty() -> UiText.StringResource(R.string.error_liquid_not_enough_input)
                else -> UiText.StringResource(R.string.label_liquid_you_need)
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