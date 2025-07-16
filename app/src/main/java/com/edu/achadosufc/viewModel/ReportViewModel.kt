package com.edu.achadosufc.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.data.SessionManager
import com.edu.achadosufc.data.model.ItemRequest
import com.edu.achadosufc.data.repository.FileRepository
import com.edu.achadosufc.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream

class ReportViewModel(
    private val itemRepository: ItemRepository,
    private val applicationContext: Context
) : ViewModel() {

    private val sessionManager = SessionManager(applicationContext)
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success = _success.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun submitReport(
        title: String,
        description: String,
        location: String,
        isLost: Boolean,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                imageUri?.let { prepareResizedFilePart(it) }?.let {
                    itemRepository.create(
                        title = title,
                        description = description,
                        location = location,
                        file = it,
                        isFound = isLost,
                        token = ("Bearer " + sessionManager.fetchAuthToken()) ?: ""
                    )
                }
                _success.value = true

            } catch (e: Exception) {
                _error.value = "Erro ao publicar: ${e.message}"
                _success.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSuccessState() {
        _success.value = false
    }

    fun clearError() {
        _error.value = null
    }

    private fun prepareResizedFilePart(imageUri: Uri): MultipartBody.Part {

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


    private fun decodeSampledBitmapFromUri(context: Context, uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
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


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
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