package com.lahsuak.apps.gradient.util

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Picture
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

object BitmapUtils {
    fun Bitmap.setWallpaper(context: Context) {
        val wallpaperManager = WallpaperManager.getInstance(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            wallpaperManager.setBitmap(this, null, true, WallpaperManager.FLAG_SYSTEM)
            wallpaperManager.setWallpaperOffsetSteps(1F, 1F)
        } else {
            wallpaperManager.setBitmap(this)
        }
    }

    fun createBitmapFromPicture(picture: Picture): Bitmap {
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

    suspend fun Bitmap.saveToDisk(context: Context): Uri {
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

    fun shareBitmap(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ContextCompat.startActivity(context, Intent.createChooser(intent, "Share your image"), null)
    }
}