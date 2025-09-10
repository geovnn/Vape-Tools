package com.geovnn.vapetools.ui.screen.saved_screen.viewmodel

import android.net.Uri
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geovnn.vapetools.R
import com.geovnn.vapetools.helper.combine
import com.geovnn.vapetools.data.db.SortType
import com.geovnn.vapetools.data.model.AromaType
import com.geovnn.vapetools.data.model.Liquid
import com.geovnn.vapetools.data.repository.LiquidRepository
import com.geovnn.vapetools.helper.UiText
import com.geovnn.vapetools.helper.millisToLocalDate
import com.geovnn.vapetools.helper.toFormattedString
import com.geovnn.vapetools.ui.common.composable.LiquidParametersState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldWithPGVGComboState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldWithSliderState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState
import com.geovnn.vapetools.ui.screen.saved_screen.composable.ImageBoxState
import com.geovnn.vapetools.ui.screen.saved_screen.state.SavedLiquidsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class SavedLiquidsViewModel(
    private val liquidRepository: LiquidRepository
) : ViewModel() {
    private val navBarFlow = flowOf(VapeTopAppBarState(title = UiText.StringResource(R.string.saved_liquids_screen_title)))

    private val _sortType = MutableStateFlow(SortType.NAME)
    private val showEditDialog = MutableStateFlow(false)

    val showDeleteDialog = MutableStateFlow<SavedLiquidsState.Content.LiquidList.LiquidCardState?>(null)
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
    val dialogImageUri = MutableStateFlow<Uri?>(null)
    val dialogImageUriVersion = MutableStateFlow(0)

    private val deleteDialogFlow = showDeleteDialog.map { liquid ->
        liquid?.let {
            val liquid = liquidRepository.getLiquidById(liquid.id) ?: return@map null
            SavedLiquidsState.DeleteDialog(
                title = liquid.name,
                description = UiText.StringResource(R.string.saved_liquids_delete_description),
                liquid = liquid,
                confirmButtonLabel = UiText.StringResource(R.string.label_confirm),
                cancelButtonLabel = UiText.StringResource(R.string.label_cancel),
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
                textFieldLabel = UiText.StringResource(R.string.label_target_total_amount),
                textFieldSuffix = UiText.StringResource(R.string.unit_ml),
                textFieldValue = amount,
                pgRatio = PGRatio,
                vgRatio = 100f - PGRatio - (additive.toFloatOrNull() ?: 0f),
                additiveRatio = additive.toFloatOrNull() ?: 0f
            ),
            aroma = VapeTextFieldWithPGVGComboState(
                label = UiText.StringResource(R.string.label_target_aroma_percentage),
                measure = UiText.StringResource(R.string.unit_percentage),
                textFieldValue = aromaAmount,
                selectedOption = aromaType
            ),
            additive = VapeTextFieldState(
                label = UiText.StringResource(R.string.label_additive_extended),
                measureUnit = UiText.StringResource(R.string.unit_percentage),
                text = additive
            ),
            targetNicotineStrength = VapeTextFieldState(
                label = UiText.StringResource(R.string.label_target_nicotine_strength),
                measureUnit = UiText.StringResource(R.string.unit_mg_ml),
                text = targetNicotine
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

    private val _dialog = combine(
        dialogName,
        dialogLiquidParametersFlow,
        dialogSteepingDate,
        dialogNote,
        dialogStars,
        showEditDialog,
        dialogId,
        dialogImageUri,
        dialogImageUriVersion
    ) { name, liquid, steepingDate, note, stars, showDialog, id, uri, uriVersion ->
        if (!showDialog) return@combine null
        SavedLiquidsState.Dialog(
            id = id,
            name = VapeTextFieldState(
                label = UiText.StringResource(R.string.label_name),
                text = name,
                singleLine = true,
                textAlign = TextAlign.Left
            ),
            liquidParameters = liquid,
            steepingDate = VapeTextFieldState(
                label = UiText.StringResource(R.string.label_steeping_date),
                text = steepingDate?.toFormattedString() ?: "",
            ),
            note = VapeTextFieldState(
                label = UiText.StringResource(R.string.label_note),
                text = note,
                singleLine = false,
                isNumber = false
            ),
            rating = stars,
            imageUri = ImageBoxState(
                imageUri = uri,
                imageUriVersion = uriVersion
            ),
            tempImageUri = liquidRepository.getTempImageUri() ?: return@combine null,
            titleLabel = if (id == 0) UiText.StringResource(R.string.label_new_liquid) else UiText.StringResource(R.string.label_edit_liquid)
        )
    }

    val contentFlow = kotlinx.coroutines.flow.combine(
        liquidRepository.liquidsFlow,
        _sortType,
    ) { liquidsList, sortType ->
        SavedLiquidsState.Content(
            liquids = SavedLiquidsState.Content.LiquidList(
                liquids = liquidsList.map { liquid ->
                    val steepingLabel = liquid.steepingDate?.let { date ->
                        val daysSteeping = ChronoUnit.DAYS.between(date, LocalDate.now())
                        UiText.StringResource(R.string.saved_liquids_steeping_for_days,daysSteeping)
                    }
                    SavedLiquidsState.Content.LiquidList.LiquidCardState(
                        id = liquid.id,
                        name = liquid.name,
                        imageBox = if (liquid.imageUri!=null) ImageBoxState(
                            imageUri = liquid.imageUri,
                            imageUriVersion = 0
                        ) else null,
                        rating = liquid.rating,
                        quantityLabel = UiText.StringResource(R.string.saved_liquids_card_quantity,liquid.quantity) ,
                        pgRatioLabel = if (liquid.additiveRatio > 0) {
                            UiText.StringResource(R.string.saved_liquids_card_pg_vg_a,liquid.pgRatio, liquid.pgRatio - liquid.additiveRatio,liquid.additiveRatio)
                        } else {
                            UiText.StringResource(R.string.saved_liquids_card_pg_vg,liquid.pgRatio, liquid.pgRatio - liquid.additiveRatio,)
                        },
                        nicotineStrengthLabel = UiText.StringResource(R.string.saved_liquids_card_nicotine,liquid.nicotineStrength),
                        aromaLabel = UiText.StringResource(R.string.saved_liquids_card_aroma,liquid.aromaRatio),
                        noteLabel = liquid.note,
                        steepingLabel = steepingLabel

                    )
                }
            ),
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

    fun showEditLiquidDialog(liquid: SavedLiquidsState.Content.LiquidList.LiquidCardState) {
        viewModelScope.launch {
            val liquid = liquidRepository.getLiquidById(liquid.id) ?: return@launch
            dialogId.value = liquid.id
            dialogName.value = liquid.name
            dialogTargetAmountText.value = liquid.quantity.toString()
            dialogTargetAmountPGRatio.value = liquid.pgRatio.toFloat()
            dialogTargetAromaText.value = liquid.aromaRatio.toString()
            dialogTargetAromaType.value = liquid.aromaType
            dialogTargetAdditiveText.value = liquid.additiveRatio.toString()
            dialogTargetNicotineStrength.value = liquid.nicotineStrength.toString()
            dialogNicotineShotStrength.value = liquid.nicotineStrength.toString()
            dialogNicotineShotPGRatio.value = liquid.pgRatio.toFloat()
            dialogSteepingDate.value = liquid.steepingDate
            dialogNote.value = liquid.note
            dialogStars.value = liquid.rating
            dialogImageUri.value = liquid.imageUri
            showEditDialog.value = true
        }
    }

    fun showNewLiquidDialog() {
        viewModelScope.launch {
            dialogId.value = 0
            showEditDialog.value = true
            showEditDialog.value = true
            dialogImageUriVersion.value = dialogImageUriVersion.value + 1
        }
    }

    fun hideEditDialog() {
        viewModelScope.launch {
            showEditDialog.value = false
            dialogId.value = 0
            dialogName.value = ""
            dialogTargetAmountText.value = ""
            dialogTargetAmountPGRatio.value = 50f
            dialogTargetAromaText.value = ""
            dialogTargetAromaType.value = AromaType.PG
            dialogTargetAdditiveText.value = ""
            dialogTargetNicotineStrength.value = ""
            dialogNicotineShotStrength.value = ""
            dialogNicotineShotPGRatio.value = 50f
            dialogSteepingDate.value = null
            dialogNote.value = ""
            dialogImageUri.value = null
            dialogStars.value = 0
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

    fun updateDialogSteepingDate(value: Long?) {
        dialogSteepingDate.value = if (value != null) {
            millisToLocalDate(value)
        } else {
            null
        }
    }

    fun updateDialogNote(value: String) {
        dialogNote.value = value
    }

    fun updateDialogRating(value: Int) {
        dialogStars.value = value
    }

    fun updateImageUri(value: Uri?) {
        dialogImageUri.value = value
    }

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
                imageUri = dialogImageUri.value,
                aromaType = dialogAromaType.value,
            )
            showEditDialog.value = false
            liquidRepository.saveLiquid(liquid)
        }
    }

    fun showDeleteDialog(liquid: SavedLiquidsState.Content.LiquidList.LiquidCardState) {
        viewModelScope.launch {
            showDeleteDialog.value = liquid
        }
    }

    fun deleteLiquid() {
        viewModelScope.launch {
            showDeleteDialog.value?.let {
                val liquid = liquidRepository.getLiquidById(it.id) ?: return@let
                liquidRepository.deleteLiquid(liquid)
                showDeleteDialog.value = null
            }
        }
    }



    fun hideDeleteDialog() {
        viewModelScope.launch {
            showDeleteDialog.value = null
        }
    }
}
