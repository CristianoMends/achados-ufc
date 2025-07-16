package com.edu.achadosufc.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream

class FileUtils {
    companion object {
        fun prepareResizedFilePart(imageUri: Uri, applicationContext: Context): MultipartBody.Part {

            val resizedBitmap = decodeSampledBitmapFromUri(applicationContext, imageUri, 1080, 1080)
                ?: throw Exception("Não foi possível processar a imagem.")


            val imageFile = convertBitmapToFile(applicationContext, resizedBitmap)


            val mimeType = applicationContext.contentResolver.getType(imageUri) ?: "image/jpeg"
            val requestFile = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())


            return MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
        }


        private fun convertBitmapToFile(context: Context, bitmap: Bitmap): File {
            val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpeg")
            tempFile.outputStream().use { out ->

                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            return tempFile
        }


        private fun decodeSampledBitmapFromUri(
            context: Context,
            uri: Uri,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap? {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            return BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, this)
                inputStream?.close()

                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

                inJustDecodeBounds = false
                val newInputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(newInputStream, null, this)
                newInputStream?.close()
                bitmap
            }
        }


        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val (height: Int, width: Int) = options.run { outHeight to outWidth }
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }
    }
}