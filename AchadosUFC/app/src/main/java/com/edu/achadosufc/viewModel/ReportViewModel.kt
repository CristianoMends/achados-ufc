package com.edu.achadosufc.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel(application: Application) : AndroidViewModel(application) {


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun submitReport(
        title: String,
        description: String,
        location: String,
        isLost: Boolean,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = true
           // val result = repository.sendReport(title, description, location, isLost, imageUri)
            _isLoading.value = false
            _success.value = result
        }
    }

    fun resetSuccess() {
        _success.value = false
    }
}
