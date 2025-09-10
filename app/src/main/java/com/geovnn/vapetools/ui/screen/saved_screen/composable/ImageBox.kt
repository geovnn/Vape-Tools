package com.geovnn.vapetools.ui.screen.saved_screen.composable

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter

@Composable
fun ImageBox(
    state: ImageBoxState,
    onClick: (() -> Unit)?,
    onClickDelete: () -> Unit = {  },
) {
    val borderSize = if (state.imageUri == null) 3.dp else 0.dp
    val borderColor = if (state.imageUri == null) Color.LightGray else Color.Transparent
    val painter = rememberAsyncImagePainter(model = state.imageUri)

    LaunchedEffect(state.imageUriVersion) {
        painter.restart()
    }
    Box(
        modifier = Modifier
            .padding(top = 5.dp)
            .border(borderSize, borderColor, RoundedCornerShape(5.dp))
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(5.dp))
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() }) ,
    ) {

        val painterState = painter.state.collectAsState()

        when (painterState.value) {
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                if (onClick!=null) {
                    IconButton(
                        onClick = onClickDelete,
                        modifier = Modifier.align (Alignment.TopEnd),
                        colors = IconButtonDefaults.iconButtonColors()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            tint = MaterialTheme.colorScheme.errorContainer,
                            contentDescription = "Delete liquid"
                        )
                    }
                }
            }
            else -> {
                Image(
                    modifier = Modifier
                        .padding(50.dp)
                        .fillMaxSize(),
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Add a photo",
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                    colorFilter = ColorFilter.tint(Color.LightGray),
                )

            }
        }

    }
}

data class ImageBoxState(
    val imageUri: Uri? = null,
    val imageUriVersion: Int = 0,
)