package com.lahsuak.apps.gradient.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircularIconButton(
    modifier: Modifier =Modifier,
    @DrawableRes
    iconId: Int,
    contentDescription: String? = null,
    color: Color = Color.White,
    size: Dp = 40.dp,
    onClick: () -> Unit = {},
) {
    IconButton(
        onClick = { onClick() },
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    ) {
        Icon(painter = painterResource(id = iconId), contentDescription, tint = Color.Black)
    }
}
