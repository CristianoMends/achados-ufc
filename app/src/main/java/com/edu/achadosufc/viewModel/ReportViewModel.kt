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
import com.edu.achadosufc.utils.FileUtils
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
                imageUri?.let { FileUtils.prepareResizedFilePart(it, applicationContext) }?.let {
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
}