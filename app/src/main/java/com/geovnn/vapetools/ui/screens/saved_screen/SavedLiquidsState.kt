package com.geovnn.vapetools.ui.screens.saved_screen

import com.geovnn.vapetools.data.db.Liquid
import com.geovnn.vapetools.data.db.SortType

data class SavedLiquidsState(
    val liquids: List<Liquid> = emptyList(),

    val name: String = "",
    val quantity: String = "",
    val pgRatio: Int = 50,
    val additiveRatio: String = "",
    val aromaRatio: String = "",
    val nicotineStrength: String = "",
    val steepingDate: String = "",
    val note: String = "",
    val rating: Int = 0,
    val id: Int = 0,
    val imageUri: String = "",

    val sortType: SortType = SortType.NAME
)
