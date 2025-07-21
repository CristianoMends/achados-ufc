package com.edu.achadosufc.viewModel

import ChatSocketService
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.data.model.MessageRequest
import com.edu.achadosufc.data.model.MessageResponse
import com.edu.achadosufc.data.model.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

class ChatViewModel(
    private val loginViewModel: LoginViewModel,
    private val chatSocketService: ChatSocketService,

    ) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageResponse>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        Log.d("ChatViewModel", "ViewModel inicializado. Observando o usuário...")

        viewModelScope.launch {
            loginViewModel.loggedUser.collect { user ->
                if (user != null) {
                    Log.d(
                        "ChatViewModel",
                        "Usuário logado detectado com ID: ${user.id}. Conectando ao chat..."
                    )
                    chatSocketService.connect()

                } else {
                    Log.w(
                        "ChatViewModel",
                        "Usuário atual é nulo ou deslogou. Garantindo desconexão."
                    )
                    chatSocketService.disconnect()
                }
            }
        }

        viewModelScope.launch {
            chatSocketService.incomingMessages.collect { newMessage ->
                Log.d("ChatViewModel", "Nova mensagem em tempo real recebida: ${newMessage.text}")
                _messages.value += newMessage
            }
        }
    }

    fun getChatHistory(otherUserId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            chatSocketService.getChatHistory(otherUserId)
            _isLoading.value = false
        }
    }

    fun sendMessage(senderId: Int, recipientId: Int, text: String) {
        _isLoading.value = true
        if (text.isNotBlank()) {
            chatSocketService.sendPrivateMessage(senderId, recipientId, text)

            val currentUser = loginViewModel.loggedUser.value
            if (currentUser != null) {
                val sentMessage = MessageResponse(
                    id = Random.nextInt(Int.MIN_VALUE, 0),
                    text = text,
                    createdAt = Date(),
                    sender = UserResponse(
                        id = currentUser.id,
                        username = currentUser.username,
                        email = currentUser.email,
                        name = currentUser.name,
                        surname = currentUser.surname,
                        phone = currentUser.phone,
                        imageUrl = currentUser.imageUrl ?: ""
                    ),
                    recipient = UserResponse(
                        id = recipientId,
                        username = "",
                        email = "",
                        name = "",
                        surname = "",
                        phone = "",
                        imageUrl = ""
                    )
                )
                _messages.value += sentMessage
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatSocketService.disconnect()
    }
}