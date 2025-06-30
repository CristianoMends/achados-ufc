package com.edu.achadosufc.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class ReportViewModel(
    private val itemRepository: ItemRepository,
    private val fileRepository: FileRepository,
    private val applicationContext: Context
) : ViewModel() {

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
                if (imageUri != null) {
                    val filePart = prepareFilePart(imageUri)
                    if (filePart != null) {
                        fileRepository.uploadFile(filePart)
                    }

                }
            } catch (e: Exception) {
                _error.value = "Erro ao enviar imagem: ${e.message}"
                _isLoading.value = false
                return@launch
            }

            try {
                itemRepository.create(
                    ItemRequest(
                        title = title,
                        description = description,
                        location = location,
                        isFound = isLost,
                    )
                )
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

    private fun prepareFilePart(fileUri: Uri): MultipartBody.Part? {
        val contentResolver = applicationContext.contentResolver
        val inputStream = contentResolver.openInputStream(fileUri)

        if (inputStream == null) {
            _error.value = "Não foi possível ler o arquivo de imagem."
            return null
        }

        val file =
            File(applicationContext.cacheDir, "upload_temp_file_${System.currentTimeMillis()}")
        file.createNewFile()
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()

        val mimeType = contentResolver.getType(fileUri) ?: "image/*"
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }
}