package com.geovnn.vapetools.ui.screens.saved_screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geovnn.vapetools.data.db.SortType
import com.geovnn.vapetools.data.db.Liquid
import com.geovnn.vapetools.data.db.LiquidDao
import com.geovnn.vapetools.data.db.LiquidEvent
import com.geovnn.vapetools.safeStringToDouble
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class SavedLiquidsViewModel(
    private val dao: LiquidDao
): ViewModel() {
    private val _uiState = MutableStateFlow(SavedLiquidsState())
    private val _sortType = MutableStateFlow(SortType.NAME)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _liquids = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.NAME -> dao.getLiquidsOrderedByName()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val state = combine(_uiState, _sortType, _liquids) { state, sortType, liquids ->
        state.copy(
            liquids = liquids,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SavedLiquidsState())

    fun updateName(string: String) {
        _uiState.update { currentState ->
            currentState.copy(
                name = string
            )
        }
    }

    fun updateQuantity(string: String) {
        val normalizedString = safeStringToDouble(string)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    quantity = string
                )
            }
        }
    }

    fun updatePgRatio(int: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                pgRatio = int
            )
        }
    }

    fun updateAdditiveRatio(string: String) {
        val normalizedString = safeStringToDouble(string)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    additiveRatio = string
                )
            }
        }
    }

    fun updateAromaRatio(string: String) {
        val normalizedString = safeStringToDouble(string)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    aromaRatio = string
                )
            }
        }
    }

    fun updateNicotineStrength(string: String) {
        val normalizedString = safeStringToDouble(string)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    nicotineStrength = string
                )
            }
        }
    }

    fun updateSteepingDate(string: String) {
        val normalizedString = safeStringToDouble(string)
        if (normalizedString!=null) {
            _uiState.update { currentState ->
                currentState.copy(
                    steepingDate = string
                )
            }
        }
    }

    fun updateNote(string: String) {
        _uiState.update { currentState ->
            currentState.copy(
                note = string
            )
        }
    }

    fun updateRating(int: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                rating = int
            )
        }
    }

    fun updateId(int: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                id = int
            )
        }
    }

    fun updateImageUri(string: String) {
        _uiState.update { currentState ->
            currentState.copy(
                imageUri = string
            )
        }
    }

    fun updateDatabase(event: LiquidEvent) {
        when(event) {
            is LiquidEvent.DeleteLiquid -> {
                viewModelScope.launch {
                    dao.deleteLiquid(event.liquid)
                }
            }

            is LiquidEvent.SortLiquids -> {
                _sortType.value = event.sortType
            }

            is LiquidEvent.SaveLiquid -> {
                val name = _uiState.value.name
                var quantity = _uiState.value.quantity
                val pgRatio = _uiState.value.pgRatio
                var additiveRatio = _uiState.value.additiveRatio
                var aromaRatio = _uiState.value.aromaRatio
                var nicotineStrength = _uiState.value.nicotineStrength
                val steepingDate = _uiState.value.steepingDate
                val note = _uiState.value.note
                val rating = _uiState.value.rating
                val id = _uiState.value.id
                val imageUri = _uiState.value.imageUri
                if(name.isBlank()) {
                    return
                }
                if (quantity.toIntOrNull()==null) {
                    quantity="0"
                }
                if (additiveRatio.toIntOrNull()==null) {
                    additiveRatio="0"
                }
                if (aromaRatio.toIntOrNull()==null) {
                    aromaRatio="0"
                }
                if (nicotineStrength.toDoubleOrNull()==null) {
                    nicotineStrength="0"
                }

                val liquid = Liquid(
                    name = name,
                    quantity = quantity.toInt(),
                    pgRatio = pgRatio,
                    additiveRatio = additiveRatio.toInt(),
                    aromaRatio = aromaRatio.toInt(),
                    nicotineStrength = nicotineStrength.toDouble(),
                    steepingDate = steepingDate,
                    note = note,
                    rating = rating,
                    id = id,
                    imageUri = imageUri
                )

                viewModelScope.launch {
                    dao.upsertLiquid(liquid)
                }

                _uiState.update { it.copy(
                    name = name,
                    quantity = quantity,
                    pgRatio = pgRatio,
                    additiveRatio = additiveRatio,
                    aromaRatio = aromaRatio,
                    nicotineStrength = nicotineStrength,
                    steepingDate = steepingDate,
                    note = note,
                    rating = rating,
                    id = id,
                    imageUri = imageUri,
                )}
            }
        }
    }

    fun saveLiquid(
    ) {
        updateDatabase(LiquidEvent.SaveLiquid)
    }

    fun createFileAndReturnUri(context: Context, fileName: String): Uri? {
        val filesDir = context.cacheDir
        val file = File(filesDir, fileName)
        try {
            // Check if the file exists; if not, create it
            if (!file.exists()) {
                file.createNewFile()
            }

            // Get the URI for the file using FileProvider to share it (if needed)

            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun getBitmapFromUri(context: Context, uri: Any): Bitmap? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri as Uri)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }
}