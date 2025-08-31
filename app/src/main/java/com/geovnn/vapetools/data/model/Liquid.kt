package com.geovnn.vapetools.data.model

import android.net.Uri
import java.time.LocalDate
import androidx.core.net.toUri

data class Liquid(
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val pgRatio: Int,
    val additiveRatio: Int,
    val aromaRatio: Int,
    val aromaType: AromaType,
    val nicotineStrength: Double,
    val steepingDate: LocalDate?,
    val note: String,
    val rating: Int,
    val imageUri: Uri?,
) {

}
