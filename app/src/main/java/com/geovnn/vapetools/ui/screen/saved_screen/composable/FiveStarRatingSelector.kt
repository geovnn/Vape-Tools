package com.geovnn.vapetools.ui.screen.saved_screen.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun FiveStarRatingSelector(
    modifier: Modifier,
    starSize: Dp,
    selectedStar: Int,
    getValue: (Int) -> Unit,
    clickable:Boolean
) {
    val defaultStarColor: Color = LocalContentColor.current.copy()
    val star1Color: Color
    val star2Color: Color
    val star3Color: Color
    val star4Color: Color
    val star5Color: Color
    when (selectedStar) {
        5 -> {
            star1Color = Color.Yellow
            star2Color = Color.Yellow
            star3Color = Color.Yellow
            star4Color = Color.Yellow
            star5Color = Color.Yellow
        }
        4 -> {
            star1Color = Color.Yellow
            star2Color = Color.Yellow
            star3Color = Color.Yellow
            star4Color = Color.Yellow
            star5Color = defaultStarColor
        }
        3 -> {
            star1Color = Color.Yellow
            star2Color = Color.Yellow
            star3Color = Color.Yellow
            star4Color = defaultStarColor
            star5Color = defaultStarColor
        }
        2 -> {
            star1Color = Color.Yellow
            star2Color = Color.Yellow
            star3Color = defaultStarColor
            star4Color = defaultStarColor
            star5Color = defaultStarColor
        }
        1 -> {
            star1Color = Color.Yellow
            star2Color = defaultStarColor
            star3Color = defaultStarColor
            star4Color = defaultStarColor
            star5Color = defaultStarColor
        }
        else -> {
            star1Color = defaultStarColor
            star2Color = defaultStarColor
            star3Color = defaultStarColor
            star4Color = defaultStarColor
            star5Color = defaultStarColor
        }
    }

    Row(modifier = modifier) {
        Icon(
            imageVector = Icons.Default.Star,
            tint = star1Color,
            contentDescription = null,
            modifier = if(clickable) {
                Modifier
                    .size(starSize)
                    .weight(1f)
                    .clickable { getValue(1) }
            } else {
                Modifier
                    .size(starSize)
                    .weight(1f)
            }
        )
        Icon(
            tint = star2Color,
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = if(clickable) {
                Modifier
                    .size(starSize)
                    .weight(1f)
                    .clickable { getValue(2) }
            } else {
                Modifier
                    .size(starSize)
                    .weight(1f)
            }
        )
        Icon(
            tint = star3Color,
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = if(clickable) {
                Modifier
                    .size(starSize)
                    .weight(1f)
                    .clickable { getValue(3) }
            } else {
                Modifier
                    .size(starSize)
                    .weight(1f)
            }
        )
        Icon(
            tint = star4Color,
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = if(clickable) {
                Modifier
                    .size(starSize)
                    .weight(1f)
                    .clickable { getValue(4) }
            } else {
                Modifier
                    .size(starSize)
                    .weight(1f)
            }
        )
        Icon(
            tint = star5Color,
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = if(clickable) {
                Modifier
                    .size(starSize)
                    .weight(1f)
                    .clickable { getValue(5) }
            } else {
                Modifier
                    .size(starSize)
                    .weight(1f)
            }
        )
    }
}

