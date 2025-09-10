package com.geovnn.vapetools.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.geovnn.vapetools.data.db.LiquidDto
import com.geovnn.vapetools.data.db.LiquidDao
import com.geovnn.vapetools.data.model.Liquid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.IOException

class LiquidRepository(
    private val liquidDao: LiquidDao,
    val context: Context
) {

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    val liquidsFlow: Flow<List<Liquid>> = liquidDao.getLiquidsOrderedByName()
        .map { liquids ->
            liquids.map { it.toLiquid() }
        }

    suspend fun getLiquidById(id: Int): Liquid? {
        return liquidDao.getLiquidById(id)?.toLiquid()
    }

    suspend fun saveLiquid(liquid: Liquid) {
        if (liquid.id == 0) {
            insertNewLiquid(liquid)
        } else {
            updateLiquid(liquid)
        }
    }

    fun clearTempImage() {
        createNewTempImage()
    }


    suspend fun deleteLiquid(liquid: Liquid) {
        val liquidDto = liquidDao.getLiquidById(liquid.id)
        liquidDao.deleteLiquid(liquidDto!!)
    }
    // INTERNAL METHODS

    private suspend fun insertNewLiquid(liquid: Liquid) {
        val imageUri = liquid.imageUri
        val hasImageToSave = when (imageUri) {
            null -> false  // Liquid has no image
            getTempImageUri() -> true
            else -> throw Exception("New liquid has invalid imageUri")
        }
        val newUri = if (hasImageToSave) copyTempImageToLiquidDirectory() else null
        liquidDao.upsertLiquid(LiquidDto.fromLiquid(liquid.copy(imageUri=newUri)))

    }

    private suspend fun updateLiquid(liquid: Liquid) {
        val newImageUri = liquid.imageUri
        val hasImageToSave = when (newImageUri) {
            getTempImageUri() -> true
            else -> false
        }

        val newUri = if (hasImageToSave) {
            val oldImageUri = getLiquidById(liquid.id)!!.imageUri
            val uriToDelete = oldImageUri?.takeIf { oldImageUri != newImageUri }
            deleteFile(uriToDelete)
            copyTempImageToLiquidDirectory()
        } else null

        liquidDao.upsertLiquid(LiquidDto.fromLiquid(liquid.copy(imageUri = newUri)))
    }


    // FILE MANIPULATION METHODS

    companion object {
        private const val LIQUID_IMAGES_DIRECTORY_NAME = "images"
        private const val LIQUID_IMAGES_EXTENSION = "png"
    }

    private fun getNewImageFileName(): String {
        return "img_${System.currentTimeMillis()}.$LIQUID_IMAGES_EXTENSION"
    }

    private fun copyTempImageToLiquidDirectory(): Uri? {
        val tempImageUri = getTempImageUri()
        if (isTempImageValidBitmap() && tempImageUri != null) {
            try {
                val imagesDir = File(context.filesDir, LIQUID_IMAGES_DIRECTORY_NAME)
                if (!imagesDir.exists()) imagesDir.mkdirs()
                val newFile = File(imagesDir, getNewImageFileName())
                context.contentResolver.openInputStream(tempImageUri)?.use { inputStream ->
                    newFile.outputStream().use { outputStream ->
                        val buffer = ByteArray(8 * 1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                        outputStream.flush()
                    }
                }
                return newFile.toUri()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun isTempImageValidBitmap(): Boolean {
        return try {
            context.contentResolver.openInputStream(getTempImageUri()!!)?.use { inputStream ->
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(inputStream, null, options)
                options.outWidth > 0 && options.outHeight > 0
            } ?: false
        } catch (_: Exception) {
            false
        }
    }

    fun getTempImageUri(): Uri? {
        val file = File(context.cacheDir, "temp_image.png")
        return if (file.exists()) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } else createNewTempImage()
    }

    private fun createNewTempImage(): Uri? {
        val file = File(context.cacheDir, "temp_image.png")
        try {
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
                .buildUpon()
                ?.appendQueryParameter("v", System.currentTimeMillis().toString())
                ?.build()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun deleteFile(uri: Uri?) {
        if (uri == null) return
        try {
            val file = File(uri.path ?: return)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}