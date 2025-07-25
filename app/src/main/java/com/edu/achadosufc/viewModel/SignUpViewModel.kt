package com.edu.achadosufc.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.data.model.UserRequest
import com.edu.achadosufc.data.repository.FileRepository
import com.edu.achadosufc.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class SignUpViewModel(
    private val uploadRepository: FileRepository,
    private val userRepository: UserRepository,
    private val applicationContext: Context,
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _surname = MutableStateFlow("")
    val surname: StateFlow<String> = _surname

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _signUpSuccess = MutableStateFlow(false)
    val signUpSuccess: StateFlow<Boolean> = _signUpSuccess


    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
        _error.value = null
    }

    fun clearErrorMessage() {
        _error.value = null
    }

    fun onNameChanged(value: String) {
        _name.value = value; _error.value = null
    }

    fun onSurnameChanged(value: String) {
        _surname.value = value; _error.value = null
    }

    fun onEmailChanged(value: String) {
        _email.value = value; _error.value = null
    }

    fun onPasswordChanged(value: String) {
        _password.value = value; _error.value = null
    }

    fun onConfirmPasswordChanged(value: String) {
        _confirmPassword.value = value; _error.value = null
    }

    fun onPhoneChanged(value: String) {
        _phone.value = value; _error.value = null
    }


    private fun prepareFilePart(fileUri: Uri): MultipartBody.Part? {
        val contentResolver = applicationContext.contentResolver
        val inputStream = contentResolver.openInputStream(fileUri)

        if (inputStream == null) {
            _error.value = "Não foi possível ler o arquivo de imagem."
            return null
        }


        val fileName = contentResolver.query(fileUri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex("_display_name")
            if (nameIndex != -1 && cursor.moveToFirst()) {
                cursor.getString(nameIndex)
            } else {
                null
            }
        } ?: "upload_temp_file_${System.currentTimeMillis()}.jpg"

        val file = File(applicationContext.cacheDir, fileName)
        file.createNewFile()

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()

        val mimeType = contentResolver.getType(fileUri) ?: "image/*"
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())


        return MultipartBody.Part.createFormData("file", fileName, requestFile)
    }


    fun signUp() {
        if (_name.value.isBlank() ||
            _email.value.isBlank() || _password.value.isBlank() ||
            _confirmPassword.value.isBlank()
        ) {
            _error.value = "Por favor, preencha todos os campos obrigatórios."
            return
        }

        if (_password.value != _confirmPassword.value) {
            _error.value = "As senhas não coincidem."
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            _error.value = "Formato de e-mail inválido."
            return
        }

        _loading.value = true
        _error.value = null
        _signUpSuccess.value = false

        viewModelScope.launch {
            try {
                var finalImageUrl: String? = null

                _selectedImageUri.value?.let { uri ->
                    val filePart = prepareFilePart(uri)
                    if (filePart == null) {
                        _error.value = "Não foi possível preparar o arquivo de imagem para upload."
                        _loading.value = false
                        return@launch
                    }

                    val uploadResult = uploadRepository.uploadFile(filePart)
                    finalImageUrl = uploadResult.url
                    if (finalImageUrl.isNullOrBlank()) {
                        _error.value = "URL da imagem retornada vazia após o upload."
                        _loading.value = false
                        return@launch
                    }
                }

                val userRequest = UserRequest(
                    username = null,
                    name = _name.value[0].uppercaseChar() + _name.value.substring(1),
                    surname = if (_surname.value.isBlank()) null else _surname.value[0].uppercaseChar() + _surname.value.substring(
                        1
                    ),
                    email = _email.value,
                    password = _password.value,
                    phone = _phone.value.ifBlank { null },
                    imageUrl = finalImageUrl
                )

                val registeredUser = userRepository.createUser(userRequest)
                if (registeredUser != null) {
                    _signUpSuccess.value = true
                } else {
                    _error.value = "Falha no cadastro: Resposta do servidor vazia."
                }

            } catch (e: Exception) {
                _error.value = "Erro ao criar conta"
            } finally {
                _loading.value = false
            }
        }
    }

    fun resetSignUpSuccess() {
        _signUpSuccess.value = false
    }

}