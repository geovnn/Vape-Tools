package com.geovnn.vapetools.ui.screen.saved_screen.screen

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val context = LocalContext.current
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
                    contentDescription = "Add liquid"
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
                items = uiState.content.liquids,
                key = { item -> item.id }
            ) { liquid ->
                var isExpanded by remember { mutableStateOf(false) }
//                val extendedPath = File(context.filesDir, liquid.imageUri).absolutePath
//                val bitmap by remember { mutableStateOf(BitmapFactory.decodeFile(extendedPath)) }
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
                                selectedStar = liquid.rating,
                                getValue = {},
                                clickable = false
                            )
                            Text(
                                textAlign = TextAlign.Right,
                                modifier =Modifier
                                    .weight(1f),
                                text = if(liquid.steepingDate!=null) {
                                    liquid.steepingDate.toString() +" days steeping"
                                } else {
                                    ""
                                }
                            )
                        }
                        Row {
                            Text(
                                textAlign = TextAlign.Left,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .weight(1f),
                                text = liquid.name,
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
                                    Text(text = "Quantity: "+liquid.quantity.toString()+" ml")
                                    Text(text = liquid.pgRatio.toString()+"PG/"+(100-liquid.pgRatio-liquid.additiveRatio).toString()+"VG/"+liquid.additiveRatio.toString()+"Additive")
                                    Text(text = liquid.aromaRatio.toString()+"% Aroma")
                                    Text(text = "Nicotine: " + liquid.nicotineStrength.toString() + " mg/ml")
                                    if (liquid.note!="") {
                                        Text(text = "Note: "+liquid.note)
                                    }
                                    if (liquid.imageUri!=null) {
                                        ImageBox(imageUri = liquid.imageUri, onClick={})
                                    }
                                    Row(modifier=Modifier
                                        .align(Alignment.Start)){
                                        IconButton(
                                            onClick = { viewModel.showEditLiquidDialog(liquid) },
                                            modifier = Modifier
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit liquid"
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                viewModel.showDeleteDialog(liquid)
                                            },
                                            modifier = Modifier
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteForever,
                                                contentDescription = "Delete liquid"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    uiState.deleteDialog?.let {
        AlertDialog(
            title = {
                Text(text = "Delete liquid")
            },
            text = {
                Text(text = "Are you sure you want to delete this liquid?")
            },
            onDismissRequest = { viewModel::hideDeleteDialog },
            confirmButton = {
                TextButton(onClick = viewModel::deleteLiquid) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideDeleteDialog ) {
                    Text(text = "Cancel")
                }
            },
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
            updatePhoto = viewModel::updateEditDialogImage,
            onSaveButtonClick = viewModel::saveLiquid,
            updateAromaType = viewModel::updateAromaType,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddLiquidDialog(
    modifier: Modifier = Modifier,
//    liquid: Liquid?,
    state: SavedLiquidsState.Dialog,
    closeDialog: () -> Unit,
    updateName: (String) -> Unit,
    updateQuantity: (String) -> Unit,
    updatePGRatio: (Float) -> Unit,
    updateAdditive: (String) -> Unit,
    updateAroma: (String) -> Unit,
    updateNicotineStrength: (String) -> Unit,
    updateSteepingDate: (String) -> Unit,
    updateNote: (String) -> Unit,
    updateRating: (Int) -> Unit,
    updatePhoto: (Bitmap) -> Unit,
    onSaveButtonClick: () -> Unit,
    updateAromaType: (AromaType) -> Unit,
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val filename by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var liquidId by remember { mutableIntStateOf(0) }
    var liquidName by remember { mutableStateOf("") }
    var liquidQuantity by remember { mutableStateOf("") }
    var liquidPgRatio by remember { mutableIntStateOf(0) }
    var liquidAromaRatio by remember { mutableStateOf("") }
    var liquidAdditiveRatio by remember { mutableStateOf("") }
    var liquidNicotineStrength by remember { mutableStateOf("") }
    var liquidSteepingDate by remember { mutableStateOf("") }
    var liquidNote by remember { mutableStateOf("") }
    var liquidRating by remember { mutableIntStateOf(0) }
    var liquidImageUri by remember { mutableStateOf("") }
    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {

        }



//    LaunchedEffect(true) {
//        tempImageUri = getTempImageUri
//        if(liquid!=null){
//            liquidId=liquid.id
//            liquidName=liquid.name
//            liquidQuantity=liquid.quantity.toString()
//            liquidPgRatio=liquid.pgRatio
//            liquidAdditiveRatio=liquid.additiveRatio.toString()
//            liquidAromaRatio=liquid.aromaRatio.toString()
//            liquidNicotineStrength=liquid.nicotineStrength.toString()
//            liquidSteepingDate=liquid.steepingDate
//            liquidNote=liquid.note
//            liquidRating=liquid.rating
//            liquidImageUri=liquid.imageUri
//            bitmap = if (liquidImageUri!="") {
//                try {
//                    val filepath = File(context.filesDir, liquidImageUri).absolutePath
//                    BitmapFactory.decodeFile(filepath)
//                } catch (e: Exception) {
//                    null
//                } finally {
//                    isLoaded=true
//                }
//            } else {
//                isLoaded=true
//                null
//            }
//        } else {
//            liquidId=0
//            liquidName=""
//            liquidQuantity=""
//            liquidPgRatio=50
//            liquidAdditiveRatio=""
//            liquidAromaRatio=""
//            liquidNicotineStrength=""
//            liquidSteepingDate=""
//            liquidNote=""
//            liquidRating=0
//            liquidImageUri=""
//            bitmap=null
//            isLoaded=true
//        }
//    }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = closeDialog,
        title = if (state.id==0) {
            { Text(text = "Add liquid") }
        }else {
            { Text(text = "Edit liquid") }
        },
        text = {
            if (isLoaded) {
                Column(
                    modifier = Modifier
                        .verticalScroll(state = scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

//                        SliderTextField (
//                            label = "Total quantity",
//                            measure = "ml",
//                            additiveRatio = liquidAdditiveRatio,
//                            valueVar = liquidQuantity,
//                            getValue = {liquidQuantity=it},
//                            focus = focusManager,
//                            getSliderValue = {liquidPgRatio=it.toInt()},
//                            pgRatio = liquidPgRatio
//                        )
//                    Row {
//                        OutlinedTextField(
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(end = 4.dp),
//                            value = liquidAromaRatio,
//                            onValueChange = {liquidAromaRatio=it},
//                            label = {
//                                Text(text = "Aroma")
//                            },
//                            suffix = {
//                                Text(text = "%")
//                            },
//                            keyboardOptions = KeyboardOptions(
//                                keyboardType = KeyboardType.Number,
//                                imeAction = ImeAction.Next
//                            ),
//                        )
//                        OutlinedTextField(
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(start = 4.dp),
//                            value = liquidAdditiveRatio,
//                            onValueChange = {liquidAdditiveRatio=it},
//                            label = {
//                                Text(text = "Additive (e.g. water)")
//                            },
//                            suffix = {
//                                Text(text = "%")
//                            },
//                            keyboardOptions = KeyboardOptions(
//                                keyboardType = KeyboardType.Number,
//                                imeAction = ImeAction.Next
//                            ),
//                        )
//                    }
//                    OutlinedTextField(
//                        value = liquidNicotineStrength,
//                        onValueChange = {liquidNicotineStrength=it},
//                        label = {
//                            Text(text = "Nicotine strength")
//                        },
//                        suffix = {
//                            Text(text = "mg/ml")
//                        },
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Number,
//                            imeAction = ImeAction.Next
//                        ),
//                    )
                    VapeTextField(
                        state = state.name,
                        onValueChange = updateName,
                        onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) },
                    )
                    LiquidParameters(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
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
                            state = state.steepingDate,
                            onValueChange = updateSteepingDate,
                            onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) },
                        )
//                        OutlinedTextField(
//                            value = formatMillisToDate(state.steepingDate),
//                            onValueChange = {
//                            },
//                            label = {
//                                Text(text = "Steeping date")
//                            },
//                            enabled=false,
//                            colors = OutlinedTextFieldDefaults.colors(
//                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
//                                disabledContainerColor = Color.Transparent,
//                                disabledBorderColor = MaterialTheme.colorScheme.outline,
//                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
//                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                                disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                                disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                                disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant
//                            ),
//                            modifier = Modifier
//                                .clickable { openDialog.value = true }
//                                .weight(4f),
//                        )
                        IconButton(
                            onClick = { updateSteepingDate("") },
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)) {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = "Delete"
                            )
                        }
                    }
                    VapeTextField(
                        state = state.note,
                        onValueChange = updateNote,
                        onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) },
                    )
//                    OutlinedTextField(
//                        value = liquidNote,
//                        onValueChange = {liquidNote=it},
//                        label = {
//                            Text(text = "Note")
//                        },
//                        )
                    FiveStarRatingSelector(
                        modifier= Modifier,
                        starSize = 40.dp,
                        selectedStar = state.rating,
                        getValue = updateRating,
                        clickable = true
                    )
                    ImageBox(
                        imageUri = state.imageUri,
                        onClick = {
                            if (cameraPermissionState.status.isGranted) {
                                tempImageUri?.let { launcher.launch(it) }
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSaveButtonClick
            ) {
                Text("Save")
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
                        updateSteepingDate(datePickerState.selectedDateMillis.toString())
                        showDatePicker.value = false
                    },
                    enabled = confirmEnabled
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

