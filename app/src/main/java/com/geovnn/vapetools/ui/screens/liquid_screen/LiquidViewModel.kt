package com.geovnn.vapetools.ui.screens.liquid_screen

import androidx.lifecycle.ViewModel
import com.geovnn.vapetools.safeStringToDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LiquidViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(LiquidUiState())
    val uiState: StateFlow<LiquidUiState> = _uiState.asStateFlow()
    fun updateTargetTotalAmount(total:String) {
        if (total.toDoubleOrNull()!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentTargetTotalAmount = total
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    currentTargetTotalAmount = ""
                )
            }
        }
    }

    private fun updateTargetVgRatio(ratio:Int) {
        _uiState.update { currentState ->
            currentState.copy(
                currentTargetVgRatio = ratio
            )
        }
    }

    private fun updateTargetPgRatio(ratio:Int) {
        _uiState.update { currentState ->
            currentState.copy(
                currentTargetPgRatio = ratio
            )
        }
    }

    fun updateTargetNicotineStrength(nicotineStrength:String) {
        if (nicotineStrength.toDoubleOrNull()!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentTargetNicotineStrength = nicotineStrength
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    currentTargetNicotineStrength = ""
                )
            }
        }
    }

    fun updateAromaRatio(aroma:String) {
        if (aroma.toDoubleOrNull()!=null) {
            if (aroma.toDouble()<=100) {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentAromaRatio = aroma
                    )
                }
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    currentAromaRatio = ""
                )
            }
        }
    }

    fun updateAromaOption(aromaOption:String) {
        _uiState.update { currentState ->
            currentState.copy(
                currentAromaOption = aromaOption
            )
        }
    }

    fun updateAdditiveRatio(additive:String) {
        if (additive!="") {
            val normalizedString = safeStringToDouble(additive)
            if (normalizedString!=null)
            if (normalizedString.toDouble()<=100) {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentAdditiveRatio = additive
                    )
                }
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    currentAdditiveRatio = ""
                )
            }
        }
    }

    fun updateNicotineShotStrength(strength:String) {
        if (strength.toDoubleOrNull()!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentNicotineShotStrength = strength
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    currentNicotineShotStrength = ""
                )
            }
        }
    }

    private fun updateNicotineVgRatio(ratio:Int) {
        _uiState.update { currentState ->
            currentState.copy(
                currentNicotineVgRatio = ratio
            )
        }
    }

    private fun updateNicotinePgRatio(ratio:Int) {
        _uiState.update { currentState ->
            currentState.copy(
                currentNicotinePgRatio = ratio
            )
        }
    }

    fun getTargetRatioFromSlider(
        sliderValue: Double,
    ) {
        val vgRatio: Int = if (_uiState.value.currentAdditiveRatio.toIntOrNull()!=null) {
            100-sliderValue.toInt()-_uiState.value.currentAdditiveRatio.toInt()
        } else {
            100-sliderValue.toInt()
        }
        val pgRatio = sliderValue.toInt()
        updateTargetVgRatio(vgRatio)
        updateTargetPgRatio(pgRatio)
    }

    fun getNicotineRatioFromSlider(
        sliderValue: Double,
    ) {
        val vgRatio = 100-sliderValue.toInt()
        val pgRatio = sliderValue.toInt()
        updateNicotineVgRatio(vgRatio)
        updateNicotinePgRatio(pgRatio)
    }
}