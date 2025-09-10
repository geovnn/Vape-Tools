package com.geovnn.vapetools.ui.screen.saved_screen.screen

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.geovnn.vapetools.R
import com.geovnn.vapetools.data.model.AromaType
import com.geovnn.vapetools.ui.common.composable.LiquidParameters
import com.geovnn.vapetools.ui.common.composable.VapeTextField
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBar
import com.geovnn.vapetools.ui.screen.saved_screen.composable.FiveStarRatingSelector
import com.geovnn.vapetools.ui.screen.saved_screen.composable.ImageBox
import com.geovnn.vapetools.ui.screen.saved_screen.state.SavedLiquidsState
import com.geovnn.vapetools.ui.screen.saved_screen.viewmodel.SavedLiquidsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    viewModel: SavedLiquidsViewModel,
    uiState: SavedLiquidsState,
    openDrawer: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            VapeTopAppBar(
                modifier = Modifier,
                state = uiState.topAppBar,
                openDrawer = openDrawer,
                actions = { }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showNewLiquidDialog) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.label_new_liquid)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = uiState.content.liquids.liquids,
                key = { item -> item.id }
            ) { liquid ->
                LiquidCard(
                    state = liquid,
                    onEditClick = { viewModel.showEditLiquidDialog(liquid) },
                    onDeleteClick = { viewModel.showDeleteDialog(liquid) },
                )
            }
        }
    }


    uiState.deleteDialog?.let {
        DeleteDialog(
            state = it,
            onDismiss = viewModel::hideDeleteDialog,
            onConfirm = viewModel::deleteLiquid
        )
    }

    uiState.editDialog?.let { dialog ->
        AddLiquidDialog(
            state = dialog,
            closeDialog = viewModel::hideEditDialog,
            updateName = viewModel::updateDialogName,
            updateQuantity = viewModel::updateDialogAmount,
            updatePGRatio = viewModel::updateDialogTargetPgRatio,
            updateAdditive = viewModel::updateDialogAdditiveRatio,
            updateAroma = viewModel::updateDialogAromaAmount,
            updateNicotineStrength = viewModel::updateDialogNicotine,
            updateSteepingDate = viewModel::updateDialogSteepingDate,
            updateNote = viewModel::updateDialogNote,
            updateRating = viewModel::updateDialogRating,
            updateImageUri = viewModel::updateImageUri,
            onSaveButtonClick = viewModel::saveLiquid,
            updateAromaType = viewModel::updateAromaType,
            onDeleteButtonClick = { viewModel.updateImageUri(null) },
        )
    }
}

@Composable
fun LiquidCard(
    state: SavedLiquidsState.Content.LiquidList.LiquidCardState,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded}
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                FiveStarRatingSelector(
                    modifier =Modifier
                        .weight(1f),
                    starSize = 22.dp,
                    selectedStar = state.rating,
                    clickable = false
                )
                Text(
                    textAlign = TextAlign.Right,
                    modifier =Modifier.weight(1f),
                    text = state.steepingLabel?.asString() ?: ""
                )
            }
            Row {
                Text(
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .weight(1f),
                    text = state.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Column(
                modifier=Modifier
                    .fillMaxWidth()
            ){
                AnimatedVisibility(visible = isExpanded){
                    Column(modifier=Modifier
                        .fillMaxWidth()){
                        Text(text = state.quantityLabel.asString())
                        Text(text = state.pgRatioLabel.asString())
                        Text(text = state.aromaLabel.asString())
                        Text(text = state.nicotineStrengthLabel.asString())
                        if (state.noteLabel.isNotBlank()) {
                            Text(text = state.noteLabel)
                        }
                        if (state.imageBox!=null) {
                            ImageBox(
                                state = state.imageBox,
                                onClick = null,
                            )
                        }
                        Row(modifier=Modifier
                            .align(Alignment.Start)){
                            IconButton(
                                onClick = onEditClick,
                                modifier = Modifier
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.label_edit_liquid)
                                )
                            }
                            IconButton(
                                onClick = onDeleteClick,
                                modifier = Modifier
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = stringResource(R.string.saved_liquids_delete_description)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddLiquidDialog(
    modifier: Modifier = Modifier,
    state: SavedLiquidsState.Dialog,
    closeDialog: () -> Unit,
    updateName: (String) -> Unit,
    updateQuantity: (String) -> Unit,
    updatePGRatio: (Float) -> Unit,
    updateAdditive: (String) -> Unit,
    updateAroma: (String) -> Unit,
    updateNicotineStrength: (String) -> Unit,
    updateSteepingDate: (Long?) -> Unit,
    updateNote: (String) -> Unit,
    updateRating: (Int) -> Unit,
    updateImageUri: (uri: Uri?) -> Unit,
    onSaveButtonClick: () -> Unit,
    updateAromaType: (AromaType) -> Unit,
    onDeleteButtonClick: () -> Unit,

) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                updateImageUri(state.tempImageUri)
            }
        }


    AlertDialog(
        modifier = modifier,
        onDismissRequest = closeDialog,
        title = { Text(text = state.titleLabel.asString()) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(state = scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VapeTextField(
                    state = state.name,
                    onValueChange = updateName,
                    onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) },
                )
                LiquidParameters(
                    modifier = Modifier
                        .fillMaxWidth(),
                    state = state.liquidParameters,
                    onChangeAmount = updateQuantity,
                    onSliderChangeAmount = updatePGRatio,
                    onAromaValueChange = updateAroma,
                    onAromaTypeChange = updateAromaType,
                    onAdditiveValueChange = updateAdditive,
                    onTargetNicotineValueChange = updateNicotineStrength,
                )
                Row {
                    VapeTextField(
                        modifier = Modifier,
                        state = state.steepingDate,
                        onValueChange = {  },
                        onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) },
                        onClick = { showDatePicker.value = true },
                    )
                    IconButton(
                        onClick = { updateSteepingDate(null) },
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = stringResource(R.string.label_delete)
                        )
                    }
                }
                VapeTextField(
                    state = state.note,
                    onValueChange = updateNote,
                    onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) },
                )

                FiveStarRatingSelector(
                    modifier= Modifier,
                    starSize = 40.dp,
                    selectedStar = state.rating,
                    onStarClick = updateRating,
                    clickable = true
                )
                ImageBox(
                    state = state.imageUri,
                    onClick = {
                        if (cameraPermissionState.status.isGranted) {
                            launcher.launch(state.tempImageUri)
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    },
                    onClickDelete = onDeleteButtonClick,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSaveButtonClick
            ) {
                Text(stringResource(R.string.label_save))
            }
        },
    )

    if (showDatePicker.value) {
        val confirmEnabled by remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
        DatePickerDialog(
            onDismissRequest = {
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        updateSteepingDate(datePickerState.selectedDateMillis)
                        showDatePicker.value = false
                    },
                    enabled = confirmEnabled
                ) {
                    Text(stringResource(R.string.label_okay))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker.value = false
                    }
                ) {
                    Text(stringResource(R.string.label_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DeleteDialog(
    state: SavedLiquidsState.DeleteDialog,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = state.title)
        },
        text = {
            Text(text = state.description.asString())
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = state.confirmButtonLabel.asString())
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss ) {
                Text(text = state.cancelButtonLabel.asString())
            }
        },
    )
}