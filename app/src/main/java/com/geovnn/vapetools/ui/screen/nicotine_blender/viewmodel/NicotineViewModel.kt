package com.geovnn.vapetools.ui.screen.nicotine_blender.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geovnn.vapetools.R
import com.geovnn.vapetools.helper.UiText
import com.geovnn.vapetools.helper.combine
import com.geovnn.vapetools.ui.common.composable.VapeComboBoxState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState
import com.geovnn.vapetools.ui.screen.nicotine_blender.state.NicotineUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class NicotineViewModel: ViewModel() {

    val mode = MutableStateFlow(NicotineUiState.Mode.ADD_NICOTINE)
    val totalAmount = MutableStateFlow("")
    val currentNicotineStrength = MutableStateFlow("")
    val targetStrength = MutableStateFlow("")
    val shotStrength = MutableStateFlow("")
    val result = MutableStateFlow("")


    val navBarFlow = flowOf(VapeTopAppBarState(title = UiText.StringResource(R.string.nicotine_blender_screen_title)))


    val contentFlow = combine(
        mode,
        totalAmount,
        currentNicotineStrength,
        targetStrength,
        shotStrength,
        result
    ) { mode, totalAmount, currentNicotineStrength, targetStrength, shotStrength, result ->
        val result = calculateResults(
            mode = mode.ordinal,
            totalAmount = totalAmount.toDoubleOrNull(),
            currentStrength = currentNicotineStrength.toDoubleOrNull(),
            targetStrength = targetStrength.toDoubleOrNull(),
            shotStrength = shotStrength.toDoubleOrNull()
        )
        val aromaLoss = result?.let {
            val totalAmount = totalAmount.toDoubleOrNull()
            if (totalAmount==null) null else {
                (100 - ((totalAmount / (it+totalAmount)) * 100)).toInt()
            }
        }
        NicotineUiState.Content(
            mode = VapeComboBoxState(
                items = NicotineUiState.Mode.entries.map {
                    val label = when (it) {
                        NicotineUiState.Mode.ADD_NICOTINE -> UiText.StringResource(R.string.nicotine_blender_label_increase_nicotine)
                        NicotineUiState.Mode.REMOVE_NICOTINE -> UiText.StringResource(R.string.nicotine_blender_label_decrease_nicotine)
                    }
                    VapeComboBoxState.Item(
                        id = it.ordinal.toString(),
                        label = label
                    )
                },
                selectedItem = VapeComboBoxState.Item(
                    id = mode.ordinal.toString(),
                    label = UiText.StringResource(R.string.nicotine_blender_result_remove)
                )
            ),
            amount = VapeTextFieldState(
                label = UiText.StringResource(R.string.label_target_total_amount),
                measureUnit = UiText.StringResource(R.string.unit_ml),
                text = totalAmount,
            ),
            currentNicotineStrength = VapeTextFieldState(
                label = UiText.StringResource(R.string.label_current_nicotine_strength),
                measureUnit = UiText.StringResource(R.string.unit_ml),
                text = currentNicotineStrength,
            ),
            targetStrength = VapeTextFieldState(
                label = UiText.StringResource(R.string.label_target_strength),
                measureUnit = UiText.StringResource(R.string.unit_ml),
                text = targetStrength,
                isError = result==null
            ),
            shotStrength = VapeTextFieldState(
                label = UiText.StringResource(R.string.label_nicotine_shot_strength),
                measureUnit = UiText.StringResource(R.string.unit_ml),
                text = shotStrength,
            ),
            result =
                NicotineUiState.Content.ResultsBoxState(
                    title = UiText.StringResource(R.string.label_results),
                    isError = result==null,
                    description = when (mode) {
                        NicotineUiState.Mode.ADD_NICOTINE -> {
                            when (result) {
                                null -> {
                                    UiText.StringResource(R.string.nicotine_blender_error_target_must_be_greater)
                                }
                                0.0 -> {
                                    UiText.StringResource(R.string.nicotine_blender_error_not_enough_inputs)
                                }
                                else -> {
                                    UiText.StringResource(
                                        R.string.nicotine_blender_result_add,
                                        result, aromaLoss?: 0
                                    )
                                }
                            }
                        }

                        NicotineUiState.Mode.REMOVE_NICOTINE -> {
                            when (result) {
                                null -> {
                                    UiText.StringResource(R.string.nicotine_blender_error_target_must_be_less)
                                }
                                0.0 -> {
                                    UiText.StringResource(R.string.nicotine_blender_error_not_enough_inputs)
                                }
                                else -> {
                                    UiText.StringResource(
                                        R.string.nicotine_blender_result_remove,
                                        result, aromaLoss ?: 0
                                    )
                                }
                            }
                        }
                    }
                )


        )

    }

    val uiState: StateFlow<NicotineUiState> = combine(
        navBarFlow,
        contentFlow
    ) { navBar, content ->
        NicotineUiState(
            topAppBar = navBar,
            content = content
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = NicotineUiState()
    )

    fun updateTotalAmount(amount:String) {
        when {
            amount.isEmpty() -> totalAmount.value = ""
            amount.toDoubleOrNull() != null -> totalAmount.value = amount
        }
    }

    fun updateCurrentNicotineStrength(milligrams: String) {
        when {
            milligrams.isEmpty() -> currentNicotineStrength.value = ""
            milligrams.toDoubleOrNull() != null -> currentNicotineStrength.value = milligrams
        }
    }

    fun updateTargetNicotineStrength(milligrams: String) {
        when {
            milligrams.isEmpty() -> targetStrength.value = ""
            milligrams.toDoubleOrNull() != null -> targetStrength.value = milligrams
        }
    }

    fun updateShotStrength(milligrams: String) {
        when {
            milligrams.isEmpty() -> shotStrength.value = ""
            milligrams.toDoubleOrNull() != null -> shotStrength.value = milligrams
        }

    }

    fun updateMode(item: VapeComboBoxState.Item) {
        val selectedMode = NicotineUiState.Mode.entries.firstOrNull { it.ordinal.toString() == item.id }
        if (selectedMode!=null) {
            mode.value = selectedMode
        }
    }


    private fun calculateResults(
        mode : Int,
        totalAmount: Double?,
        currentStrength: Double?,
        targetStrength: Double?,
        shotStrength: Double?,
    ): Double? {
        when (mode) {
            NicotineUiState.Mode.ADD_NICOTINE.ordinal -> {
                if(totalAmount!=0.0 && targetStrength!=0.0 && shotStrength!=0.0 &&
                    totalAmount!=null && currentStrength!=null && targetStrength!=null && shotStrength!=null) {
                    if (targetStrength<=currentStrength){
//                            setError(true)
                        return null
                    } else {
                        val result = (((targetStrength*totalAmount)-(currentStrength*totalAmount))/shotStrength)
                        return result
                    }
                } else {
                    return 0.0
                }
            }
            NicotineUiState.Mode.REMOVE_NICOTINE.ordinal -> {
                if(totalAmount!=0.0 && currentStrength!=0.0 && targetStrength!=0.0 &&
                    totalAmount!=null && currentStrength!=null && targetStrength!=null) {
                    if (targetStrength>=currentStrength){
                        return null
                    } else {
                        val result = (totalAmount*currentStrength/targetStrength)-totalAmount
                        return result
                    }
                } else {
                    return 0.0
                }
            }
            else -> return null
        }
    }
}