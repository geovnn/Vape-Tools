package com.geovnn.vapetools.ui.screen.nicotine_blender.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geovnn.vapetools.combine
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
import java.util.Locale

class NicotineViewModel: ViewModel() {

    val mode = MutableStateFlow(NicotineUiState.Mode.ADD_NICOTINE)
    val totalAmount = MutableStateFlow("")
    val currentNicotineStrength = MutableStateFlow("")
    val targetStrength = MutableStateFlow("")
    val shotStrength = MutableStateFlow("")
    val result = MutableStateFlow("")


    val navBarFlow = flowOf(VapeTopAppBarState(title = "Nicotine Blender"))


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
                100 - ((totalAmount / (it+totalAmount)) * 100)
            }
        }
        NicotineUiState.Content(
            mode = VapeComboBoxState(
                items = NicotineUiState.Mode.entries.map {
                    VapeComboBoxState.Item(
                        id = it.ordinal.toString(),
                        string = it.name
                    )
                },
                selectedItem = VapeComboBoxState.Item(
                    id = mode.ordinal.toString(),
                    string = mode.name
                )
            ),
            amount = VapeTextFieldState(
                label = "Total amount",
                measureUnit = "ml",
                text = totalAmount,
            ),
            currentNicotineStrength = VapeTextFieldState(
                label = "Current nicotine strength",
                measureUnit = "ml",
                text = currentNicotineStrength,
            ),
            targetStrength = VapeTextFieldState(
                label = "Target strength",
                measureUnit = "ml",
                text = targetStrength,
                isError = result==null
            ),
            shotStrength = VapeTextFieldState(
                label = "Shot strength",
                measureUnit = "ml",
                text = shotStrength,
            ),
            result =
                NicotineUiState.Content.ResultsBoxState(
                    title = "Results",
                    isError = result==null,
                    description = when (mode) {
                        NicotineUiState.Mode.ADD_NICOTINE -> {
                            when (result) {
                                null -> {
                                    "Target strength must be greater than current strength"
                                }
                                0.0 -> {
                                    "Fill more inputs to see results"
                                }
                                else -> {
                                    String.format(
                                        Locale.ENGLISH,
                                        "Add %.2f ml of nicotine shot",
                                        result
                                    ) + if (aromaLoss != null) String.format(
                                        Locale.ENGLISH,
                                        " (Aroma will be %.1f%% less intense)",
                                        aromaLoss
                                    ) else ""
                                }
                            }
                        }

                        NicotineUiState.Mode.REMOVE_NICOTINE -> {
                            when (result) {
                                null -> {
                                    "Target strength must be less than current strength"
                                }
                                0.0 -> {
                                    "Fill more inputs to see results"
                                }
                                else -> {
                                    String.format(
                                        Locale.ENGLISH,
                                        "Add %.2f ml of nicotine free base",
                                        result
                                    ) + if (aromaLoss != null) String.format(
                                        Locale.ENGLISH,
                                        " (Aroma will be %.1f%% less intense)",
                                        aromaLoss
                                    ) else ""
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

//    private fun updateResult(result: Double) {
//
//        var normalizedString = String.Companion.format(Locale.ENGLISH,"%.2f",result)
//
//        if (result==0.0) {
//            normalizedString = ""
//        }
//
//        _uiState.update { currentState ->
//            currentState.copy(
//                resultAmount = normalizedString,
//            )
//        }
//    }
//
//    private fun setError(flag: Boolean) {
//        _uiState.update { currentState ->
//            currentState.copy(
//                isError = flag,
//            )
//        }
//    }

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