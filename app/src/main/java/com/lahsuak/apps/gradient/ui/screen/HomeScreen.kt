package com.lahsuak.apps.gradient.ui.screen

import android.graphics.Picture
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lahsuak.apps.gradient.R
import com.lahsuak.apps.gradient.ui.component.CircularIconButton
import com.lahsuak.apps.gradient.ui.component.ColorsTray
import com.lahsuak.apps.gradient.util.AppUtils
import com.lahsuak.apps.gradient.util.AppUtils.createBitmapFromPicture
import com.lahsuak.apps.gradient.util.AppUtils.saveFile
import com.lahsuak.apps.gradient.util.AppUtils.setWallpaper
import com.lahsuak.apps.gradient.util.AppUtils.shareBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Preview
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val picture = remember { Picture() }

    val randomColors = remember {
        mutableStateListOf(
            Color.Magenta,
            Color.Blue,
            Color.Yellow,
            Color.Red,
            Color.Green,
            Color.Cyan,
            Color.Black,
            Color.White,
            Color.DarkGray,
            Color(0xFFFB8C00),
            Color(0xFF039BE5),
            Color(0xFF5300FC),
            Color(0xFF5300FC),
        )
    }
    val colors = remember {
        mutableStateListOf(
            Color(0xFFF4511E),
            Color.White,
            Color.Green
        )
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
    var isShuffleEnable by rememberSaveable {
        mutableStateOf(false)
    }
    var randomColor by rememberSaveable {
        mutableIntStateOf(2)
    }

    fun shareBitmapFromComposable() {
        coroutineScope.launch(Dispatchers.IO) {
            val bitmap = createBitmapFromPicture(picture)
            val uri = bitmap.saveFile(context)
            uri?.let {
                shareBitmap(context, it)
            }
        }
    }

    fun saveBitmapFromComposable() {
        coroutineScope.launch(Dispatchers.IO) {
            val bitmap = createBitmapFromPicture(picture)
            val uri = bitmap.saveFile(context)
            withContext(Dispatchers.Main) {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = context.getString(R.string.save_success_msg),
                    actionLabel = context.getString(R.string.view),
                    duration = SnackbarDuration.Long
                )
                if(snackbarResult == SnackbarResult.ActionPerformed){
                     AppUtils.openImageSelectorDialog(context, uri)
                }
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {

        Box(Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        // Example that shows how to redirect rendering to an Android Picture and then
                        // draw the picture into the original destination
                        val width = this.size.width.toInt()
                        val height = this.size.height.toInt()
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
                                this.size
                            ) {
                                this@onDrawWithContent.drawContent()
                            }
                            picture.endRecording()
                            drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                        }
                    }
            ) {
                WallpaperGeneratorScreen(
                    modifier = Modifier.fillMaxSize(),
                    colors = if (isShuffleEnable) randomColors else colors,
                    colorCount = {
                        if (isShuffleEnable) {
                            randomColor
                        } else {
                            colors.size
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
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CircularIconButton(
                        modifier = Modifier.rotate(rotateAngle),
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
                        isShuffleEnable = false
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
                        isShuffleEnable = false
                    }
                    CircularIconButton(
                        iconId = R.drawable.ic_shuffle,
                        color = Color(0xFF88C6FE)
                    ) {
                        if (isShuffleEnable) {
                            randomColor = Random.nextInt(2, randomColors.size)
                        }
                        isShuffleEnable = true
                    }
                    CircularIconButton(
                        iconId = R.drawable.ic_share,
                        color = Color(0xFFFB8C00),
                        contentDescription = stringResource(R.string.share)
                    ) {
                        shareBitmapFromComposable()
                    }
                    CircularIconButton(
                        iconId = R.drawable.ic_save,
                        color = Color.Green,
                        contentDescription = stringResource(R.string.save)
                    ) {
                        saveBitmapFromComposable()
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
                    },
                    onRemoveClick = { index ->
                        colors.removeAt(index)
                    }
                )
                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            val bitmap = createBitmapFromPicture(picture)
                            bitmap.setWallpaper(context)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.set_wallpaper_msg),
                                    Toast.LENGTH_SHORT
                                ).show()
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
                    Text(stringResource(R.string.set_wallpaper))
                }
            }
        }
    }
}