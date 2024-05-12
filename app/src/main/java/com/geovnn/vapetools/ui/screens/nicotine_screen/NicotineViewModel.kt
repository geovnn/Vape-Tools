package com.geovnn.vapetools.ui.screens.nicotine_screen

import androidx.lifecycle.ViewModel
import com.geovnn.vapetools.safeStringToDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class NicotineViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(NicotineUiState())
    val uiState: StateFlow<NicotineUiState> = _uiState.asStateFlow()
    fun addNicotineFlag(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                addNicotine = value,
            )
        }
        setError(false)
        if (value) {
            calculateResults("yes")
        } else {
            calculateResults("no")
        }
    }

    fun updateTotalAmount(amount:String) {
        val normalizedString = safeStringToDouble(amount)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    totalAmount = normalizedString,
                )
            }
        }
        calculateResults()

    }

    fun updateCurrentNicotineStrength(milligrams: String) {
        val normalizedString = safeStringToDouble(milligrams)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentNicotine = normalizedString,
                )
            }
        }
        calculateResults()

    }

    fun updateTargetNicotineStrength(milligrams: String) {
        val normalizedString = safeStringToDouble(milligrams)
        if (normalizedString!=null) {
            setError(false)
            _uiState.update { currentState ->
                currentState.copy(
                    targetStrength = normalizedString,
                )
            }
        }
        calculateResults()

    }

    fun updateShotStrength(milligrams: String) {
        val normalizedString = safeStringToDouble(milligrams)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    shotStrength = normalizedString,
                )
            }
        }
        calculateResults()
    }

    private fun updateResult(result: Double) {

        var normalizedString = String.format(Locale.ENGLISH,"%.2f",result)

        if (result==0.0) {
            normalizedString = ""
        }

        _uiState.update { currentState ->
            currentState.copy(
                resultAmount = normalizedString,
            )
        }
    }

    private fun setError(flag: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isError = flag,
            )
        }
    }

    private fun calculateResults(
        isAdding: String = ""
    ) {
        val add:Boolean = if (isAdding=="yes") {
            true
        } else if (isAdding=="no") {
            false
        } else {
            _uiState.value.addNicotine
        }
        val totalAmount = _uiState.value.totalAmount.toDoubleOrNull()
        val currentStrength = _uiState.value.currentNicotine.toDoubleOrNull()
        val targetStrength = _uiState.value.targetStrength.toDoubleOrNull()
        val shotStrength = _uiState.value.shotStrength.toDoubleOrNull()

        if (add) {
            if(totalAmount!=0.0 && currentStrength!=0.0 && targetStrength!=0.0 && shotStrength!=0.0 &&
                totalAmount!=null && currentStrength!=null && targetStrength!=null && shotStrength!=null) {
                if (targetStrength<=currentStrength){
                    setError(true)
                } else {
                    updateResult((((targetStrength*totalAmount)-(currentStrength*totalAmount))/shotStrength))
                }
            } else {
                updateResult(0.0)
            }
        } else {
            if(totalAmount!=0.0 && currentStrength!=0.0 && targetStrength!=0.0 &&
                totalAmount!=null && currentStrength!=null && targetStrength!=null) {
                if (targetStrength>=currentStrength){
                    setError(true)
                } else {
                    updateResult((totalAmount*currentStrength/targetStrength)-totalAmount)
                }
            } else {
                updateResult(0.0)
            }
        }
    }
}