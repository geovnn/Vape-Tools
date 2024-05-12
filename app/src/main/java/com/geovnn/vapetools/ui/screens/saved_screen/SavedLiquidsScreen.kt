package com.geovnn.vapetools.ui.screens.saved_screen

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.geovnn.vapetools.data.db.Liquid
import com.geovnn.vapetools.data.db.LiquidEvent
import com.geovnn.vapetools.formatMillisToDate
import com.geovnn.vapetools.getDaysSinceDateInMillis
import com.geovnn.vapetools.ui.screens.liquid_screen.SliderTextInput
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    drawerState: DrawerState,
    savedLiquidsViewModel: SavedLiquidsViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val savedLiquidsUiState by savedLiquidsViewModel.state.collectAsState()
    var selectedItem by remember { mutableStateOf<Liquid?>(null) }
    var showDeleteDialog by remember { mutableStateOf (false) }
    var showEditDialog by remember { mutableStateOf (false) }
    var showAddDialog by remember { mutableStateOf (false) }
    var expandedItem by remember { mutableIntStateOf(0) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Saved liquids")
                },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showAddDialog=true
            }) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Add liquid"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()

                ,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                savedLiquidsUiState.liquids,
                key = { item ->
                    item.id
                }
            ) { liquid ->
                val extendedPath = File(context.filesDir, liquid.imageUri).absolutePath
                val bitmap by remember { mutableStateOf(BitmapFactory.decodeFile(extendedPath)) }
                Card(
                    modifier = Modifier
                        .padding(8.dp),
                    onClick = {}
                ) {
                    Column(
                        modifier = Modifier
                            .clickable { expandedItem = if (expandedItem==liquid.id) {
                                0
                            } else {
                                liquid.id
                            } }
                            .fillMaxWidth()
                            .padding(all = 16.dp)
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
                                text = if(liquid.steepingDate!="") {
                                    getDaysSinceDateInMillis(liquid.steepingDate.toLongOrNull()) +" days steeping"
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
                            AnimatedVisibility(visible = expandedItem==liquid.id){
                                Column(modifier=Modifier
                                    .fillMaxWidth()){
                                    Text(text = "Quantity: "+liquid.quantity.toString()+" ml")
                                    Text(text = liquid.pgRatio.toString()+"PG/"+(100-liquid.pgRatio-liquid.additiveRatio).toString()+"VG/"+liquid.additiveRatio.toString()+"Additive")
                                    Text(text = liquid.aromaRatio.toString()+"% Aroma")
                                    Text(text = "Nicotine: " + liquid.nicotineStrength.toString() + " mg/ml")
                                    if (liquid.note!="") {
                                        Text(text = "Note: "+liquid.note)
                                    }
                                    if (bitmap!=null) {
                                        ImageBox(imageBitmap = bitmap, onClick={})
                                    }
                                    Row(modifier=Modifier
                                        .align(Alignment.Start)){
                                        IconButton(
                                            onClick = {
                                                selectedItem=liquid
                                                showEditDialog=true
                                            },
                                            modifier = Modifier
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit liquid"
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                selectedItem=liquid
                                                showDeleteDialog=true
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

    if(showDeleteDialog) {
        AlertDialog(
            title = {
                Text(text = "Delete liquid")
            },
            text = {
                Text(text = "Are you sure you want to delete this liquid?")
            },
            onDismissRequest = { showDeleteDialog=false },
            confirmButton = {
                TextButton(onClick = {
                    expandedItem=0
                    selectedItem?.let {
                        LiquidEvent.DeleteLiquid(
                            it
                        )
                    }?.let { savedLiquidsViewModel.updateDatabase(it) }
                    showDeleteDialog=false
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {showDeleteDialog=false})
                { Text(text = "Cancel") }
            },
        )
    }

    if(showAddDialog) {
        AddLiquidDialog(
            editLiquid = null,
            closeDialog = {showAddDialog=false},
            updateName = {savedLiquidsViewModel.updateName(it)},
            updateQuantity = {savedLiquidsViewModel.updateQuantity(it)},
            getSliderValue = {savedLiquidsViewModel.updatePgRatio(it.toInt())},
            updateAdditive = {savedLiquidsViewModel.updateAdditiveRatio(it)},
            updateAroma = {savedLiquidsViewModel.updateAromaRatio(it)},
            updateNicotineStrength = {savedLiquidsViewModel.updateNicotineStrength(it)},
            updateSteepingDate = {savedLiquidsViewModel.updateSteepingDate(it)},
            updateNote = {savedLiquidsViewModel.updateNote(it)},
            updateRating = {savedLiquidsViewModel.updateRating(it)},
            updatePhotoUri = {savedLiquidsViewModel.updateImageUri(it)},
            onSaveButtonClick = {if(it) {
                savedLiquidsViewModel.saveLiquid()
                showAddDialog=false
            } },
            updateId = {savedLiquidsViewModel.updateId(it)},
            getTempImageUri = savedLiquidsViewModel.createFileAndReturnUri(context, "temp_image.jpg"),
        ) { uri ->
            savedLiquidsViewModel.getBitmapFromUri(context, uri)
        }
    }

    if(showEditDialog) {
        AddLiquidDialog(
            editLiquid = selectedItem,
            closeDialog = {showEditDialog=false},
            updateName = {savedLiquidsViewModel.updateName(it)},
            updateQuantity = {savedLiquidsViewModel.updateQuantity(it)},
            getSliderValue = {savedLiquidsViewModel.updatePgRatio(it.toInt())},
            updateAdditive = {savedLiquidsViewModel.updateAdditiveRatio(it)},
            updateAroma = {savedLiquidsViewModel.updateAromaRatio(it)},
            updateNicotineStrength = {savedLiquidsViewModel.updateNicotineStrength(it)},
            updateSteepingDate = {savedLiquidsViewModel.updateSteepingDate(it)},
            updateNote = {savedLiquidsViewModel.updateNote(it)},
            updateRating = {savedLiquidsViewModel.updateRating(it)},
            updatePhotoUri = {savedLiquidsViewModel.updateImageUri(it)},
            onSaveButtonClick = {if(it) {
                savedLiquidsViewModel.saveLiquid()
                showEditDialog=false
            } },
            updateId = {savedLiquidsViewModel.updateId(it)},
            getTempImageUri = savedLiquidsViewModel.createFileAndReturnUri(context, "temp_image.jpg")
        ) { uri ->
            savedLiquidsViewModel.getBitmapFromUri(context, uri)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddLiquidDialog(
    modifier: Modifier = Modifier,
    editLiquid: Liquid?,
    closeDialog: (Boolean) -> Unit,
    updateName: (String) -> Unit,
    updateQuantity: (String) -> Unit,
    getSliderValue: (Double) -> Unit,
    updateAdditive: (String) -> Unit,
    updateAroma: (String) -> Unit,
    updateNicotineStrength: (String) -> Unit,
    updateSteepingDate: (String) -> Unit,
    updateNote: (String) -> Unit,
    updateRating: (Int) -> Unit,
    updatePhotoUri: (String) -> Unit,
    onSaveButtonClick: (Boolean) -> Unit,
    updateId: (Int) -> Unit,
    getTempImageUri: Uri?,
    getBitmapFromUri: (Any) -> Bitmap?
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val openDialog = remember { mutableStateOf(false) }
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
            if (it){
                bitmap = tempImageUri?.let { it1 -> getBitmapFromUri(it1)?.let { it2 ->
                    rotateImage(
                        it2,90f)
                } }
            }
        }

    fun generateUniqueFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return "$timeStamp$uuid.jpg"
    }

    LaunchedEffect(true) {
        tempImageUri = getTempImageUri
        if(editLiquid!=null){
            liquidId=editLiquid.id
            liquidName=editLiquid.name
            liquidQuantity=editLiquid.quantity.toString()
            liquidPgRatio=editLiquid.pgRatio
            liquidAdditiveRatio=editLiquid.additiveRatio.toString()
            liquidAromaRatio=editLiquid.aromaRatio.toString()
            liquidNicotineStrength=editLiquid.nicotineStrength.toString()
            liquidSteepingDate=editLiquid.steepingDate
            liquidNote=editLiquid.note
            liquidRating=editLiquid.rating
            liquidImageUri=editLiquid.imageUri
            bitmap = if (liquidImageUri!="") {
                try {
                    val filepath = File(context.filesDir, liquidImageUri).absolutePath
                    BitmapFactory.decodeFile(filepath)
                } catch (e: Exception) {
                    null
                } finally {
                    isLoaded=true
                }
            } else {
                isLoaded=true
                null
            }
        } else {
            liquidId=0
            liquidName=""
            liquidQuantity=""
            liquidPgRatio=50
            liquidAdditiveRatio=""
            liquidAromaRatio=""
            liquidNicotineStrength=""
            liquidSteepingDate=""
            liquidNote=""
            liquidRating=0
            liquidImageUri=""
            bitmap=null
            isLoaded=true
        }
    }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            closeDialog(true)
        },
        title = if (editLiquid!=null) {
            { Text(text = "Edit liquid") }
        }else {
            { Text(text = "Add liquid") }
        },
        text = {
            if (isLoaded) {
                Column(
                    modifier = Modifier
                        .verticalScroll(state = scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isLoaded) {
                        OutlinedTextField(
                            value = liquidName,
                            onValueChange = {
                                if (nameError) {
                                    nameError=false
                                }
                                liquidName=it
                            },
                            label = {
                                Text(text = "Name")
                            },
                            isError = nameError,
                            supportingText = {
                                if (nameError) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "Required",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            trailingIcon = {
                                if (nameError) {
                                    Icon(Icons.Filled.Error,"error", tint= MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                        SliderTextInput(
                            label = "Total quantity",
                            measure = "ml",
                            additiveRatio = liquidAdditiveRatio,
                            valueVar = liquidQuantity,
                            getValue = {liquidQuantity=it},
                            focus = focusManager,
                            getSliderValue = {liquidPgRatio=it.toInt()},
                            pgRatio = liquidPgRatio
                        )
                        Row {
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp),
                                value = liquidAromaRatio,
                                onValueChange = {liquidAromaRatio=it},
                                label = {
                                    Text(text = "Aroma")
                                },
                                suffix = {
                                    Text(text = "%")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                            )
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 4.dp),
                                value = liquidAdditiveRatio,
                                onValueChange = {liquidAdditiveRatio=it},
                                label = {
                                    Text(text = "Additive (e.g. water)")
                                },
                                suffix = {
                                    Text(text = "%")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                            )
                        }
                        OutlinedTextField(
                            value = liquidNicotineStrength,
                            onValueChange = {liquidNicotineStrength=it},
                            label = {
                                Text(text = "Nicotine strength")
                            },
                            suffix = {
                                Text(text = "mg/ml")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                        )
                        Row {
                            OutlinedTextField(
                                value = formatMillisToDate(liquidSteepingDate.toLongOrNull()),
                                onValueChange = {
                                },
                                label = {
                                    Text(text = "Steeping date")
                                },
                                enabled=false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledContainerColor = Color.Transparent,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier
                                    .clickable { openDialog.value = true }
                                    .weight(4f),
                            )
                            IconButton(
                                onClick = { liquidSteepingDate="" },
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)) {
                                Icon(
                                    imageVector = Icons.Default.Backspace,
                                    contentDescription = "Delete"
                                )
                            }
                        }
                        OutlinedTextField(
                            value = liquidNote,
                            onValueChange = {liquidNote=it},
                            label = {
                                Text(text = "Note")
                            },
                        )
                        FiveStarRatingSelector(
                            modifier= Modifier,
                            starSize = 40.dp,
                            selectedStar = liquidRating,
                            getValue = { liquidRating=it },
                            clickable = true
                        )
                        ImageBox(
                            imageBitmap = bitmap,
                            onClick = { isClicked ->
                                if (isClicked) {
                                    if (cameraPermissionState.status.isGranted) {
                                        launcher.launch(tempImageUri)
                                    } else {
                                        cameraPermissionState.launchPermissionRequest()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (isLoaded) {
                TextButton(
                    onClick = {
                        if (liquidName.isNotBlank()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if (bitmap != null) {
                                    if (filename != "") {
                                        try {
                                            File(context.filesDir, filename).delete()
                                        } catch (_: Exception) {
                                        }
                                    }
                                    val path = generateUniqueFileName()
                                    try {
                                        val fileOutputStream =
                                            context.openFileOutput(path, Context.MODE_PRIVATE)
                                        bitmap?.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            100,
                                            fileOutputStream
                                        )
                                        withContext(Dispatchers.IO) {
                                            fileOutputStream.close()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    liquidImageUri = path
                                } else {
                                    liquidImageUri = ""
                                }
                                updateId(liquidId)
                                updateName(liquidName)
                                updateQuantity(liquidQuantity)
                                getSliderValue(liquidPgRatio.toDouble())
                                updateAroma(liquidAromaRatio)
                                updateAdditive(liquidAdditiveRatio)
                                updateNicotineStrength(liquidNicotineStrength)
                                updateSteepingDate(liquidSteepingDate)
                                updateNote(liquidNote)
                                updateRating(liquidRating)
                                updatePhotoUri(liquidImageUri)
                                onSaveButtonClick(true)
                            }
                        } else {
                            nameError=true
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        },
    )

    if (openDialog.value) {
        val confirmEnabled by remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
        DatePickerDialog(
            onDismissRequest = {
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        liquidSteepingDate=datePickerState.selectedDateMillis.toString()
                        openDialog.value = false
                    },
                    enabled = confirmEnabled
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
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

@Composable
fun FiveStarRatingSelector(
    modifier: Modifier,
    starSize: Dp,
    selectedStar: Int,
    getValue: (Int) -> Unit,
    clickable:Boolean
) {
    val defaultStarColor: Color = LocalContentColor.current.copy()
    val star1Color: Color
    val star2Color: Color
    val star3Color: Color
    val star4Color: Color
    val star5Color: Color
    when (selectedStar) {
        5 -> {
            star1Color = Color.Yellow
            star2Color = Color.Yellow
            star3Color = Color.Yellow
            star4Color = Color.Yellow
            star5Color = Color.Yellow
        }
        4 -> {
            star1Color = Color.Yellow
            star2Color = Color.Yellow
            star3Color = Color.Yellow
            star4Color = Color.Yellow
            star5Color = defaultStarColor
        }
        3 -> {
            star1Color = Color.Yellow
            star2Color = Color.Yellow
            star3Color = Color.Yellow
            star4Color = defaultStarColor
            star5Color = defaultStarColor
        }
        2 -> {
            star1Color = Color.Yellow
            star2Color = Color.Yellow
            star3Color = defaultStarColor
            star4Color = defaultStarColor
            star5Color = defaultStarColor
        }
        1 -> {
            star1Color = Color.Yellow
            star2Color = defaultStarColor
            star3Color = defaultStarColor
            star4Color = defaultStarColor
            star5Color = defaultStarColor
        }
        else -> {
            star1Color = defaultStarColor
            star2Color = defaultStarColor
            star3Color = defaultStarColor
            star4Color = defaultStarColor
            star5Color = defaultStarColor
        }
    }

    Row(modifier = modifier) {
        Icon(
            imageVector = Icons.Default.Star,
            tint = star1Color,
            contentDescription = null,
            modifier = if(clickable) {
                Modifier
                    .size(starSize)
                    .weight(1f)
                    .clickable { getValue(1) }
            } else {
                Modifier
                    .size(starSize)
                    .weight(1f)
            }
        )
        Icon(
            tint = star2Color,
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = if(clickable) {
                Modifier
                    .size(starSize)
                    .weight(1f)
                    .clickable { getValue(2) }
            } else {
                Modifier
                    .size(starSize)
                    .weight(1f)
            }
        )
        Icon(
            tint = star3Color,
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = if(clickable) {
                Modifier
                    .size(starSize)
                    .weight(1f)
                    .clickable { getValue(3) }
            } else {
                Modifier
                    .size(starSize)
                    .weight(1f)
            }
        )
        Icon(
            tint = star4Color,
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = if(clickable) {
                Modifier
                    .size(starSize)
                    .weight(1f)
                    .clickable { getValue(4) }
            } else {
                Modifier
                    .size(starSize)
                    .weight(1f)
            }
        )
        Icon(
            tint = star5Color,
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = if(clickable) {
                Modifier
                    .size(starSize)
                    .weight(1f)
                    .clickable { getValue(5) }
            } else {
                Modifier
                    .size(starSize)
                    .weight(1f)
            }
        )
    }
}

@Composable
fun ImageBox(
    onClick: (Boolean) -> Unit,
    imageBitmap: Bitmap?
) {
    val borderSize = if (imageBitmap==null) {3.dp} else {0.dp}
    val borderColor = if (imageBitmap==null) {
        Color.LightGray} else {
        Color.Transparent}
    Box(
        modifier = Modifier
            .padding(top = 5.dp)
            .border(borderSize, borderColor, RoundedCornerShape(5.dp))
            .fillMaxWidth()
            .height(200.dp)
            .clip(shape = RoundedCornerShape(5.dp))
            .clickable
            { onClick(true) })
    {
        if(imageBitmap!=null) {
            Image(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(12.dp))
                    .fillMaxWidth()
                    .fillMaxHeight(),
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                modifier = Modifier
                    .padding(50.dp)
                    .fillMaxSize(1f),
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Add a photo",
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                colorFilter = ColorFilter.tint(Color.LightGray),
            )
        }
    }
}