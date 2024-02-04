package com.lahsuak.apps.gradient.ui.screen

import android.graphics.Picture
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.lahsuak.apps.gradient.R
import com.lahsuak.apps.gradient.util.BitmapUtils.createBitmapFromPicture
import com.lahsuak.apps.gradient.util.BitmapUtils.saveToDisk
import com.lahsuak.apps.gradient.util.BitmapUtils.setWallpaper
import com.lahsuak.apps.gradient.util.BitmapUtils.shareBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Preview
@Composable
fun GradientDesignScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val picture = remember { Picture() }

    val colors = remember {
        mutableStateListOf(
            Color.Magenta,
            Color.Blue,
            Color.Yellow,
            Color.Red,
            Color.Green
        )
    }

    var colorCount by remember {
        mutableIntStateOf(colors.size)
    }
    var rotateAngle by rememberSaveable {
        mutableFloatStateOf(0f)
    }
    var isSweepEnable by rememberSaveable {
        mutableStateOf(false)
    }
    var isSweepGradientInside by rememberSaveable {
        mutableStateOf(false)
    }

    fun shareBitmapFromComposable() {
        coroutineScope.launch(Dispatchers.IO) {
            val bitmap = createBitmapFromPicture(picture)
            val uri = bitmap.saveToDisk(context)
            shareBitmap(context, uri)
        }
    }

    var canvasSize by remember { mutableStateOf(Size.Zero) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        // [START android_compose_draw_into_bitmap]
        Box(Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        // Example that shows how to redirect rendering to an Android Picture and then
                        // draw the picture into the original destination
                        val width = canvasSize.width.toInt()
                        val height = canvasSize.height.toInt()
                        onDrawWithContent {
                            val pictureCanvas =
                                androidx.compose.ui.graphics.Canvas(
                                    picture.beginRecording(
                                        width,
                                        height
                                    )
                                )
                            draw(
                                this,
                                this.layoutDirection,
                                pictureCanvas,
                                canvasSize
                            ) {
                                this@onDrawWithContent.drawContent()
                            }
                            picture.endRecording()

                            drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                        }
                    }
            ) {
                RandomBackgroundScreen(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight().onGloballyPositioned { coordinates ->
                        canvasSize = coordinates.size.toSize()
                    },
                    colors = colors,
                    colorCount = {
                        if (colors.size > colorCount) {
                            colorCount + 1
                        } else {
                            colorCount
                        }
                    },
                    rotateAngle = { rotateAngle },
                    isSweepEnable = { isSweepEnable },
                    isSweepGradientInside = { isSweepGradientInside }
                )
            }
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.4f))
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CircularIconButton(
                        iconId = R.drawable.ic_arrow_dual_full,
                        color = Color(0xFF88C6FE)
                    ) {
                        if (isSweepEnable) {
                            isSweepEnable = false
                        }
                        if (rotateAngle >= 360) {
                            rotateAngle = 0f
                        }
                        rotateAngle += 45f
                    }
                    CircularIconButton(
                        iconId = if (isSweepGradientInside) {
                            R.drawable.ic_arrow_double_vertical_in
                        } else {
                            R.drawable.ic_arrow_double_vertical_out
                        },
                        color = Color(0xFF88C6FE)
                    ) {
                        if (!isSweepEnable) {
                            isSweepEnable = true
                        }
                        isSweepGradientInside = !isSweepGradientInside
                    }
                    CircularIconButton(
                        iconId = R.drawable.ic_shuffle,
                        color = Color.Green
                    ) {
                        colorCount = Random.nextInt(2, colors.size)
                    }
                    CircularIconButton(
                        iconId = R.drawable.ic_share,
                        color = Color(0xFFFB8C00),
                        contentDescription = stringResource(R.string.share)
                    ) {
                        shareBitmapFromComposable()
                    }
                }
                ColorsTray(
                    modifier = Modifier,
                    colors = colors,
                    onColorChange = { newColor, colorIndex ->
                        if (colorIndex == -1) {
                            val temp = mutableListOf<Color>()
                            temp.addAll(colors)
                            temp.add(newColor)
                            colors.clear()
                            colors.addAll(temp)
                        } else {
                            val temp = mutableListOf<Color>()
                            temp.addAll(colors)
                            temp.add(colorIndex, newColor)
                            temp.removeAt(colorIndex + 1)
                            colors.clear()
                            colors.addAll(temp)
                        }
                    }
                )
                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            createBitmapFromPicture(picture).setWallpaper(context)
                            withContext(Dispatchers.Main){
                                Toast.makeText(context, "Wallpaper Successfully Set!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(0.7f),
                        contentColor = Color.Black
                    ), modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Set Wallpaper")
                }
            }
        }
        // [END android_compose_draw_into_bitmap]
    }
}

@Composable
fun CircularIconButton(
    @DrawableRes
    iconId: Int,
    contentDescription: String? = null,
    color: Color = Color.White,
    size: Dp = 40.dp,
    onClick: () -> Unit = {},
) {
    IconButton(
        onClick = { onClick() },
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    ) {
        Icon(painter = painterResource(id = iconId), contentDescription, tint = Color.Black)
    }
}
