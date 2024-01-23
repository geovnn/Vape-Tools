package com.geovnn.vapetools.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Liquid(
    val name: String,
    val quantity: Int,
    val pgRatio: Int,
    val additiveRatio: Int,
    val aromaRatio: Int,
    val nicotineStrength: Double,
    val steepingDate: String,
    val note: String,
    val rating: Int,
    val imageUri: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
