package com.edu.achadosufc.data.repository

import com.edu.achadosufc.data.model.UploadResponse
import com.edu.achadosufc.data.service.FileService
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FileRepository {

    private val api: FileService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(FileService::class.java)
    }

    suspend fun uploadFile(filePart: MultipartBody.Part): UploadResponse {
        try {
            val response: Response<UploadResponse> = api.uploadFile(filePart)

            if (response.isSuccessful) {
                return response.body() ?: throw Exception("Resposta de upload vazia do servidor.")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage =
                    "Erro no upload: Código ${response.code()}, Mensagem: ${response.message()}, Corpo do Erro: $errorBody"

                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            throw Exception("Error uploading file: ${e.message}")
        }
    }

}