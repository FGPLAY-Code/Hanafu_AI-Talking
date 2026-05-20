package com.hanafu.app.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ML Kit 文字识别工具
 */
object OcrHelper {

    private const val MAX_IMAGE_SIZE = 2048

    private val recognizer by lazy {
        TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    }

    suspend fun recognizeText(context: Context, imageUri: Uri): String = withContext(Dispatchers.IO) {
        val bitmap = decodeBitmap(context, imageUri)
        val rotation = getRotation(context, imageUri)
        val inputImage = InputImage.fromBitmap(bitmap, rotation)
        val result = Tasks.await(recognizer.process(inputImage))
        result.text
    }

    private fun decodeBitmap(context: Context, uri: Uri): android.graphics.Bitmap {
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, opts)
        }

        val (origW, origH) = opts.outWidth to opts.outHeight
        var sampleSize = 1
        while (origW / sampleSize > MAX_IMAGE_SIZE || origH / sampleSize > MAX_IMAGE_SIZE) {
            sampleSize *= 2
        }

        val realOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, realOpts)
        } ?: throw Exception("无法解码图片")
    }

    private fun getRotation(context: Context, uri: Uri): Int {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return 0
            val exif = ExifInterface(inputStream)
            inputStream.close()
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }
}
