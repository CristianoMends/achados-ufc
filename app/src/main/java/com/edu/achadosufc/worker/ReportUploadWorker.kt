package com.edu.achadosufc.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edu.achadosufc.data.SessionManager
import com.edu.achadosufc.data.repository.ItemRepository
import com.edu.achadosufc.utils.FileUtils

class ReportUploadWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val itemRepository: ItemRepository,
    private val sessionManager: SessionManager
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {

        val title = inputData.getString("TITLE") ?: return Result.failure()
        val description = inputData.getString("DESCRIPTION") ?: return Result.failure()
        val location = inputData.getString("LOCATION") ?: return Result.failure()
        val isFound = inputData.getBoolean("IS_FOUND", false)
        val imageUriString = inputData.getString("IMAGE_URI") ?: return Result.failure()
        val imageUri = Uri.parse(imageUriString)

        return try {
            val filePart = FileUtils.prepareResizedFilePart(imageUri, applicationContext)
            val token = "Bearer " + sessionManager.fetchAuthToken()

            val res = itemRepository.create(
                title = title,
                description = description,
                location = location,
                file = filePart,
                isFound = isFound,
                token = token
            )

            Result.success()

        } catch (e: retrofit2.HttpException) {
            if (e.code() >= 500) Result.retry() else Result.failure()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}