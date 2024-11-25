package com.example.jetdrivedemoapi.ui.components.common.wrapper

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
@ExperimentalMaterial3Api
fun IconWithoutDesc(
    painter: Painter,
    tint: ColorProducer?,
    modifier: Modifier = Modifier
) {
    Icon(
        painter,
        tint,
        "",
        modifier
    )
}

@Composable
@ExperimentalMaterial3Api
fun IconWithoutDesc(
    imageVector: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector,
        "",
        modifier,
        tint
    )
}

@Composable
@ExperimentalMaterial3Api
fun IconWithoutDesc(
    bitmap: ImageBitmap,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Icon(
        bitmap,
        "",
        modifier,
        tint
    )
}

