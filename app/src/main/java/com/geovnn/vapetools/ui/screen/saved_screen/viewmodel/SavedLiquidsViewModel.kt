package com.geovnn.vapetools.ui.screen.saved_screen.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geovnn.vapetools.combine
import com.geovnn.vapetools.data.db.SortType
import com.geovnn.vapetools.data.model.AromaType
import com.geovnn.vapetools.data.model.Liquid
import com.geovnn.vapetools.data.repository.LiquidRepository
import com.geovnn.vapetools.ui.common.composable.LiquidParametersState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldWithPGVGComboState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldWithSliderState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState
import com.geovnn.vapetools.ui.screen.saved_screen.state.SavedLiquidsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class SavedLiquidsViewModel(
    private val liquidRepository: LiquidRepository
) : ViewModel() {

    private val navBarFlow = flowOf(VapeTopAppBarState(title = "Saved Liquids"))
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val showDialog = MutableStateFlow(false)

    val showDeleteDialog = MutableStateFlow<Liquid?>(null)
    val dialogTargetAmountText = MutableStateFlow("")
    val dialogTargetAmountPGRatio = MutableStateFlow(50f)
    val dialogTargetAromaText = MutableStateFlow("")
    val dialogTargetAromaType = MutableStateFlow(AromaType.PG)
    val dialogTargetAdditiveText = MutableStateFlow("")
    val dialogTargetNicotineStrength = MutableStateFlow("")
    val dialogNicotineShotStrength = MutableStateFlow("")
    val dialogNicotineShotPGRatio = MutableStateFlow(50f)

    val dialogId = MutableStateFlow(0)
    val dialogName = MutableStateFlow("")
    val dialogSteepingDate = MutableStateFlow<LocalDate?>(null)
    val dialogStars = MutableStateFlow(0)
    val dialogNote = MutableStateFlow("")
    val dialogAromaType = MutableStateFlow(AromaType.PG)

    private val deleteDialogFlow = showDeleteDialog.map { liquid ->
        liquid?.let {
            SavedLiquidsState.DeleteDialog(
                title = "Delete ${liquid.name}?",
                description = "Are you sure you want to delete this liquid? This action cannot be undone.",
                liquid = liquid
            )
        }
    }

    private val dialogLiquidParametersFlow = combine(
        dialogTargetAmountText,
        dialogTargetAromaText,
        dialogTargetAmountPGRatio,
        dialogTargetAromaType,
        dialogTargetAdditiveText,
        dialogTargetNicotineStrength,
        dialogNicotineShotStrength,
        dialogNicotineShotPGRatio
    ) { amount,
        aromaAmount,
        PGRatio,
        aromaType,
        additive, targetNicotine,
        nicotineShotStrength,
        nicotineShotPGRatio ->
        LiquidParametersState(
            amount = VapeTextFieldWithSliderState(
                textFieldLabel = "Target Total Amount",
                textFieldSuffix = "ml",
                textFieldValue = amount,
                pgRatio = PGRatio,
                vgRatio = 100f - PGRatio - (additive.toFloatOrNull() ?: 0f),
                additiveRatio = additive.toFloatOrNull() ?: 0f
            ),
            aroma = VapeTextFieldWithPGVGComboState(
                label = "Target aroma level",
                measure = "%",
                textFieldValue = aromaAmount,
                selectedOption = aromaType
            ),
            additive = VapeTextFieldState(
                label = "Additive (e.g. water)",
                measureUnit = "%",
                text = additive
            ),
            targetNicotineStrength = VapeTextFieldState(
                label = "Target nicotine strength",
                measureUnit = "mg/ml",
                text = targetNicotine
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
    private val _dialog = combine(
        dialogName,
        dialogLiquidParametersFlow,
        dialogSteepingDate,
        dialogNote,
        dialogStars,
        showDialog,
        liquidRepository.tempImageUri,
        dialogId
    ) { name, liquid, steepingDate, note, stars, showDialog, uri, id ->
        if (!showDialog) return@combine null
        SavedLiquidsState.Dialog(
            id = id,
            name = VapeTextFieldState(
                label = "Name",
                text = name,
            ),
            liquidParameters = liquid,
            steepingDate = steepingDate,
            note = VapeTextFieldState(
                label = "Note",
                text = note,
                singleLine = false,
            ),
            rating = stars,
            imageUri = uri
        )
    }

    val contentFlow = kotlinx.coroutines.flow.combine(
        liquidRepository.getAllLiquids(),
        _sortType,
    ) { liquidsList, sortType ->
        SavedLiquidsState.Content(
            liquids = liquidsList,
            sortType = sortType,
        )
    }

    val uiState: StateFlow<SavedLiquidsState> = kotlinx.coroutines.flow.combine(
        navBarFlow,
        contentFlow,
        _dialog,
        deleteDialogFlow
    ) { navBar, content, dialog, deleteDialog ->
        SavedLiquidsState(
            topAppBar = navBar,
            content = content,
            editDialog = dialog,
            deleteDialog = deleteDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = SavedLiquidsState()
    )



//    private val _uiState = MutableStateFlow(SavedLiquidsState())
//    private val _sortType = MutableStateFlow(SortType.NAME)
//    @OptIn(ExperimentalCoroutinesApi::class)
//    private val _liquids = _sortType
//        .flatMapLatest { sortType ->
//            when(sortType) {
//                SortType.NAME -> dao.getLiquidsOrderedByName()
//            }
//        }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
//
//    val state = combine(_uiState, _sortType, _liquids) { state, sortType, liquids ->
//        state.copy(
//            liquids = liquids,
//            sortType = sortType
//        )
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SavedLiquidsState())

    fun showEditLiquidDialog(liquid: Liquid) {
        viewModelScope.launch {
            dialogId.emit(liquid.id)
            showDialog.emit(true)
        }
    }

    fun showNewLiquidDialog() {
        viewModelScope.launch {
            dialogId.emit(0)
            showDialog.emit(true)
        }
    }

    fun hideEditDialog() {
        viewModelScope.launch {
            showDialog.emit(false)
            dialogId.emit(0)
            dialogName.emit("")
            dialogTargetAmountText.emit("")
            dialogTargetAmountPGRatio.emit(50f)
            dialogTargetAromaText.emit("")
            dialogTargetAromaType.emit(AromaType.PG)
            dialogTargetAdditiveText.emit("")
            dialogTargetNicotineStrength.emit("")
            dialogNicotineShotStrength.emit("")
            dialogNicotineShotPGRatio.emit(50f)
            dialogSteepingDate.emit(null)
            dialogNote.emit("")
            dialogStars.emit(0)
        }
    }

    fun updateDialogName(value: String) {
        dialogName.value = value
    }

    fun updateDialogAmount(value: String) {
        when {
            value.isEmpty() ->  dialogTargetAmountText.value = ""
            value.toDoubleOrNull() != null -> dialogTargetAmountText.value = value
        }
    }

    fun updateDialogNicotine(value: String) {
        when {
            value.isEmpty() -> dialogTargetNicotineStrength.value = ""
            value.toDoubleOrNull() != null -> dialogTargetNicotineStrength.value = value
        }
    }

    fun updateDialogAromaAmount(value: String) {
        when {
            value.isEmpty() -> dialogTargetAromaText.value = ""
            value.toDoubleOrNull() != null && value.toDouble() <= 100f -> dialogTargetAromaText.value = value
        }
    }

    fun updateAromaType(value: AromaType) {
        dialogTargetAromaType.value = value
    }

    fun updateDialogAdditiveRatio(value: String) {
        when {
            value.isEmpty() -> dialogTargetAdditiveText.value = ""
            value.toDoubleOrNull() != null && value.toDouble() <= 99f -> dialogTargetAdditiveText.value = value
        }
    }

    fun updateDialogTargetPgRatio(value: Float) {
        dialogTargetAmountPGRatio.value = value
    }

    fun updateDialogNicotinePgRatio(value: Float ) {
        dialogNicotineShotPGRatio.value = value
    }

    fun updateDialogSteepingDate(value: String) {
        dialogSteepingDate.value = value
    }

    fun updateDialogNote(value: String) {
        dialogNote.value = value
    }

    fun updateDialogRating(value: Int) {
        dialogStars.value = value
    }

//    fun updateId(int: Int) {
//        _uiState.update { currentState ->
//            currentState.copy(
//                id = int
//            )
//        }
//    }

//    fun updateImageUri(string: String) {
//        _uiState.update { currentState ->
//            currentState.copy(
//                imageUri = string
//            )
//        }
//    }

    fun saveLiquid() {
        viewModelScope.launch {
            val liquid = Liquid(
                id = dialogId.value,
                name = dialogName.value,
                quantity = dialogTargetAmountText.value.toIntOrNull() ?: 0,
                pgRatio = dialogTargetAmountPGRatio.value.toInt(),
                additiveRatio = dialogTargetAdditiveText.value.toIntOrNull() ?: 0,
                aromaRatio = dialogTargetAromaText.value.toIntOrNull() ?: 0,
                nicotineStrength = dialogTargetNicotineStrength.value.toDoubleOrNull() ?: 0.0,
                steepingDate = dialogSteepingDate.value,
                note = dialogNote.value,
                rating = dialogStars.value,
                imageUri = liquidRepository.tempImageUri.first(),
                aromaType = dialogAromaType.value
            )
            liquidRepository.upsertLiquid(liquid)
        }
    }

    fun showDeleteDialog(liquid: Liquid) {
        viewModelScope.launch {
            showDeleteDialog.value = liquid
        }
    }

    fun deleteLiquid() {
        viewModelScope.launch {
            showDeleteDialog.value?.let {
                liquidRepository.deleteLiquid(it)
                showDeleteDialog.value = null
            }
        }
    }

    fun hideDeleteDialog() {
        viewModelScope.launch {
            showDeleteDialog.value = null
        }
    }

    fun updateEditDialogImage(image: Bitmap) {
        liquidRepository.setTempImage(image)
    }
}
