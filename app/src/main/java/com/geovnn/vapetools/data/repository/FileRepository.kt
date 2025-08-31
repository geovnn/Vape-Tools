package com.geovnn.vapetools.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import com.geovnn.vapetools.data.db.LiquidDto
import com.geovnn.vapetools.data.model.Liquid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FileRepository(val context: Context) {
    val tempImageUri = flow {
        emit(createTempImageUri())
    }

    fun clearTempImage() {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            val tempUri = tempImageUri.first()
            if (tempUri != null) {
                deleteImage(tempUri)
            }
        }
    }
    private fun createTempImageUri(): Uri? {
        val filesDir = context.cacheDir
        val file = File(filesDir, "temp_image.png")
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
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun setTempImage(image: Bitmap): File {
        val path = generateUniqueFileName()
        val fileOutputStream = context.openFileOutput(path, Context.MODE_PRIVATE)
        image.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            fileOutputStream
        )
        fileOutputStream.close()
        return File(context.filesDir, path)
    }

    fun generateUniqueFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return "$timeStamp$uuid.jpg"
    }
//    fun getBitmapFromUri(uri: Any): Bitmap? {
//        var inputStream: InputStream? = null
//        try {
//            inputStream = context.contentResolver.openInputStream(uri as Uri)
//            return BitmapFactory.decodeStream(inputStream)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            try {
//                inputStream?.close()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//        return null
//    }

    fun deleteImage(uri: Uri) {
        try {
            val file = File(uri.path ?: return)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun moveTempImageToPermanentLocation(liquid: Liquid) {
        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            val tempUri = tempImageUri.first()
            val tempFile = File(tempUri?.path ?: return@launch)
            if (!tempFile.exists()) {
                return@launch
            }

            val imagesDir = File(context.filesDir, "images")
            if (!imagesDir.exists()) {
                imagesDir.mkdir()
            }

            val permanentFile = File(imagesDir, "liquid_image_${liquid.id}.png")
            if (permanentFile.exists()) {
                permanentFile.delete()
            }

            tempFile.renameTo(permanentFile)
        }

    }
}