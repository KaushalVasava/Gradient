package com.lahsuak.apps.gradient.ui.screen

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.graphics.Bitmap
import android.graphics.Picture
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.lahsuak.apps.gradient.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
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
            Color.Green,
//            Color.LightGray,
//            Color.Cyan,
//            Color.Black,
//            Color.White
        )
    }

    var colorCount by rememberSaveable {
        mutableIntStateOf(5)
    }
//    var colorCount2 by rememberSaveable {
//        mutableIntStateOf(4)
//    }
    var rotateAngle by rememberSaveable {
        mutableFloatStateOf(0f)
    }
    var isSweepEnable by rememberSaveable {
        mutableStateOf(false)
    }
    var isSweepGradientInside by rememberSaveable {
        mutableStateOf(false)
    }

    // This logic should live in your ViewModel - trigger a side effect to invoke URI sharing.
    // checks permissions granted, and then saves the bitmap from a Picture that is already capturing content
    // and shares it with the default share sheet.
    fun shareBitmapFromComposable() {
        coroutineScope.launch(Dispatchers.IO) {
            val bitmap = createBitmapFromPicture(picture)
            val uri = bitmap.saveToDisk(context)
            shareBitmap(context, uri)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                shareBitmapFromComposable()
            }) {
                Icon(Icons.Default.Share, "share")
            }
        }
    ) { padding ->
        // [START android_compose_draw_into_bitmap]
        Box(Modifier.padding(padding)) {
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
                            draw(this, this.layoutDirection, pictureCanvas, this.size) {
                                this@onDrawWithContent.drawContent()
                            }
                            picture.endRecording()

                            drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                        }
                    }
            ) {
                RandomBackgroundScreen(
                    colors,
                    colorCount = { colorCount },
                    rotateAngle = { rotateAngle },
                    isSweepEnable = { isSweepEnable },
                    isSweepGradientInside = { isSweepGradientInside }
                )
            }
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = {
                            if (isSweepEnable) {
                                isSweepEnable = false
                            }
                            if (rotateAngle >= 360) {
                                rotateAngle = 0f
                            }
                            rotateAngle += 45f
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Green)
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_arrow_dual_full), null,
                            modifier = Modifier.rotate(rotateAngle)
                        )
                    }
                    IconButton(
                        onClick = {
                            if (!isSweepEnable) {
                                isSweepEnable = true
                            }
                            isSweepGradientInside = !isSweepGradientInside
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Green)
                    ) {
                        Icon(
                            painterResource(
                                id = if (isSweepGradientInside) {
                                    R.drawable.ic_arrow_double_vertical_in
                                } else {
                                    R.drawable.ic_arrow_double_vertical_out
                                }
                            ), null
                        )
                    }
                    IconButton(
                        onClick = { colorCount = Random.nextInt(2, 4) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Green)
                    ) {
                        Icon(painterResource(id = R.drawable.ic_shuffle), null)
                    }
                }
                ColorsTray(
                    modifier = Modifier,
                    colors
                )
                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            createBitmapFromPicture(picture).setWallpaper(context)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(0.6f),
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

fun Bitmap.setWallpaper(context: Context) {
    val wallpaperManager = WallpaperManager.getInstance(context)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        wallpaperManager.setBitmap(this, null, true, WallpaperManager.FLAG_SYSTEM)
        wallpaperManager.setWallpaperOffsetSteps(1F, 1F)
    } else {
        wallpaperManager.setBitmap(this)
    }
}

private fun createBitmapFromPicture(picture: Picture): Bitmap {
    val bitmap = Bitmap.createBitmap(
        picture.width,
        picture.height,
        Bitmap.Config.ARGB_8888
    )

    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    canvas.drawPicture(picture)
    return bitmap
}

private suspend fun Bitmap.saveToDisk(context: Context): Uri {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "screenshot-${System.currentTimeMillis()}.png"
    )

    file.writeBitmap(this, Bitmap.CompressFormat.PNG, 100)

    return scanFilePath(context, file.path) ?: throw Exception("File could not be saved")
}


private suspend fun scanFilePath(context: Context, filePath: String): Uri? {
    return suspendCancellableCoroutine { continuation ->
        MediaScannerConnection.scanFile(
            context,
            arrayOf(filePath),
            arrayOf("image/png")
        ) { _, scannedUri ->
            if (scannedUri == null) {
                continuation.cancel(Exception("File $filePath could not be scanned"))
            } else {
                continuation.resume(scannedUri)
            }
        }
    }
}

private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

private fun shareBitmap(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(context, createChooser(intent, "Share your image"), null)
}
