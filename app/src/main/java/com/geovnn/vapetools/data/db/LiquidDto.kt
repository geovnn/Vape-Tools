package com.geovnn.vapetools.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.geovnn.vapetools.data.model.AromaType
import com.geovnn.vapetools.data.model.Liquid
import java.time.LocalDate
import androidx.core.net.toUri
import androidx.room.ColumnInfo

@Entity
data class LiquidDto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val pgRatio: Int,
    val additiveRatio: Int,
    @ColumnInfo(defaultValue = "PG") // Or whatever default makes sense
    val aromaType: String,
    val aromaRatio: Int,
    val nicotineStrength: Double,
    val steepingDate: String?,
    val note: String,
    val rating: Int,
    val imageUri: String?,
) {
    companion object {
        fun fromLiquid(liquid: Liquid): LiquidDto {
            fun LocalDate.serializeToString(): String? {
                return try {
                    this.toString() // ISO-8601 format
                } catch (_: Exception) {
                    null
                }
            }

            return LiquidDto(
                id = liquid.id,
                name = liquid.name,
                quantity = liquid.quantity,
                pgRatio = liquid.pgRatio,
                additiveRatio = liquid.additiveRatio,
                aromaRatio = liquid.aromaRatio,
                aromaType = liquid.aromaType.name,
                nicotineStrength = liquid.nicotineStrength,
                steepingDate = liquid.steepingDate?.serializeToString(),
                note = liquid.note,
                rating = liquid.rating,
                imageUri = liquid.imageUri?.toString(),
            )
        }
    }

        fun toLiquid(): Liquid {
            fun String.serializeFromString(): LocalDate? {
                return try {
                    LocalDate.parse(this) // ISO-8601 format
                } catch (_: Exception) {
                    null
                }
            }
        return Liquid(
            id = id,
            name = name,
            quantity = quantity,
            pgRatio = pgRatio,
            additiveRatio = additiveRatio,
            aromaRatio = aromaRatio,
            aromaType = AromaType.valueOf(aromaType),
            nicotineStrength = nicotineStrength,
            steepingDate = steepingDate?.serializeFromString(),
            note = note,
            rating = rating,
            imageUri = imageUri?.toUri(),
        )
    }

}
