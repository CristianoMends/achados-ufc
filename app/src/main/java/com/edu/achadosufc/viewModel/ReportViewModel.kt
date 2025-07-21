package com.edu.achadosufc.viewModel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.edu.achadosufc.worker.ReportUploadWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReportViewModel(
    private val applicationContext: Context
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success = _success.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun submitReport(
        title: String,
        description: String,
        location: String,
        isFound: Boolean,
        imageUri: Uri?
    ) {
        if (title.isBlank() || description.isBlank() || location.isBlank() || imageUri == null) {
            _message.value = "Todos os campos são obrigatórios."
            _success.value = false
            return
        }

        _isLoading.value = true
        _message.value = null

        val workData = workDataOf(
            "TITLE" to title,
            "DESCRIPTION" to description,
            "LOCATION" to location,
            "IS_FOUND" to isFound,
            "IMAGE_URI" to imageUri.toString()
        )

        // Define as restrições: o trabalho só será executado quando houver internet
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Cria a requisição de trabalho
        val uploadWorkRequest = OneTimeWorkRequestBuilder<ReportUploadWorker>()
            .setInputData(workData)
            .setConstraints(constraints)
            .build()

        // Agenda o trabalho com o WorkManager
        WorkManager.getInstance(applicationContext).enqueue(uploadWorkRequest)

        // FEEDBACK DE SUCESSO IMEDIATO PARA O USUÁRIO!
        _isLoading.value = false
        _success.value = true
        _message.value = "Publicação enviada para fila de upload. Você receberá uma notificação quando for processada."

    }

    fun resetSuccessState() {
        _success.value = false
    }

    fun clearError() {
        _message.value = null
    }
}