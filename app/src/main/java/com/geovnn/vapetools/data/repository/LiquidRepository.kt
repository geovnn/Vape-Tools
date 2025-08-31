package com.geovnn.vapetools.data.repository

import android.graphics.Bitmap
import com.geovnn.vapetools.data.db.LiquidDto
import com.geovnn.vapetools.data.db.LiquidDao
import com.geovnn.vapetools.data.model.Liquid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LiquidRepository(
    private val liquidDao: LiquidDao,
    val fileRepository: FileRepository
) {

    val tempImageUri = fileRepository.tempImageUri
    fun getAllLiquids(): Flow<List<Liquid>> {
        return liquidDao.getLiquidsOrderedByName().map { liquids ->
            liquids.map { it.toLiquid() }
        }
    }

    fun setTempImage(image: Bitmap) {
        fileRepository.setTempImage(image)
    }

//    suspend fun getLiquidById(id: Int): Liquid? {
//        return liquidDao.getLiquidById(id)
//    }

    suspend fun upsertLiquid(liquid: Liquid) {
        when (liquid.imageUri) {
            null -> {
                // Nessuna immagine da gestire
            }
            fileRepository.tempImageUri.first() -> {
                fileRepository.moveTempImageToPermanentLocation(liquid)
            }
            else -> {
                val existingLiquid = liquidDao.getLiquidById(liquid.id)
                if (existingLiquid?.imageUri != liquid.imageUri) {
                    existingLiquid?.imageUri?.let { fileRepository.deleteImage(it) }
                }
            }
        }
        liquidDao.upsertLiquid(LiquidDto.fromLiquid(liquid))
        fileRepository.clearTempImage()
    }

    suspend fun deleteLiquid(liquid: Liquid) {
        val liquidDto = liquidDao.getLiquidById(liquid.id)
        if (liquidDto!=null) {
            liquidDao.deleteLiquid(liquidDto)
        }
    }

//    suspend fun updateLiquid(liquid: Liquid) {
//        liquidDao.updateLiquid(liquid)
//    }

    // Aggiungi qui altri metodi se necessario, per esempio per query specifiche
}