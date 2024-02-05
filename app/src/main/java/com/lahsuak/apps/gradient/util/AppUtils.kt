package com.lahsuak.apps.gradient.util

import com.lahsuak.apps.gradient.R
import android.app.WallpaperManager
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Picture
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.lahsuak.apps.gradient.util.AppUtils.saveFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileNotFoundException
import java.io.OutputStream
import kotlin.coroutines.resume

object AppUtils {
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

    private fun getContentValues(): ContentValues {
        val fileName = "Gradient_${System.currentTimeMillis()}.png"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    fileName
                )
                put(MediaStore.Images.Media.MIME_TYPE, AppConstants.IMAGE_MIME_TYPE)
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES
                )
            }
        } else {
            val directory = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + Environment.DIRECTORY_PICTURES
            )
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, fileName)
            ContentValues().apply {
                // name of the file
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                // type of the file
                put(MediaStore.Images.Media.MIME_TYPE, AppConstants.IMAGE_MIME_TYPE)
                put(MediaStore.Images.Media.DATA, file.absolutePath)
            }
        }
    }

    fun Bitmap.saveFile(context: Context): Uri? {
        val uri: Uri?
        return try {
            val cv = getContentValues()
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                cv
            )!!
            // open the output stream with the above uri
            val imageOutStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            imageOutStream?.run {
                this@saveFile.compress(Bitmap.CompressFormat.PNG, 100, this)
                flush()
                close()
            }
            uri
        } catch (e: Exception) {
            context.getString(R.string.something_went_wrong)
            null
        }
    }

    fun shareBitmap(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = AppConstants.IMAGE_MIME_TYPE
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ContextCompat.startActivity(
            context, Intent.createChooser(
                intent,
                context.getString(R.string.share_msg)
            ), null
        )
    }

    fun openImageSelectorDialog(context: Context, uri: Uri?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, AppConstants.IMAGE_FORMAT)
            intent.putExtra(AppConstants.IMAGE_MIME_TYPE, AppConstants.IMAGE_FORMAT)
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.view)))
        } catch (e: Exception) {
            Toast.makeText(
                context,
                context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT
            ).show()
        }
    }
}