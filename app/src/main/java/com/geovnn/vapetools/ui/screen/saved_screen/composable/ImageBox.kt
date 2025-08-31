package com.geovnn.vapetools.ui.screen.saved_screen.composable

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ImageBox(
    onClick: () -> Unit,
    imageUri: Uri?
) {
    val borderSize = if (imageUri==null) {3.dp} else {0.dp}
    val borderColor = if (imageUri==null) {
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
            { onClick() })
    {
        if(imageUri!=null) {
            Image(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(12.dp))
                    .fillMaxWidth()
                    .fillMaxHeight(),
                painter = rememberAsyncImagePainter(imageUri),
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