package com.edu.achadosufc.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.data.model.Conversation
import com.edu.achadosufc.data.model.Message
import com.edu.achadosufc.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations = _conversations.asStateFlow()

    fun getUserConversations(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val convos = chatRepository.getUserConversations(userId)
                _conversations.value = convos
            } catch (e: Exception) {
                Log.e("ChatVM", "Erro ao buscar conversas", e)
                _conversations.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getConversationsForItem(itemId: String, userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val conversations = chatRepository.getConversationsForItem(itemId, userId)
                _conversations.value = conversations
            } catch (e: Exception) {
                Log.e("ChatVM", "Erro ao buscar conversas do item", e)
                _conversations.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(chatId: String, text: String, senderId: String,recipientId: String, itemId: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                chatRepository.sendMessage(chatId, text, senderId, recipientId, itemId)
            } catch (e: Exception) {
                Log.e("ChatVM", "Erro ao enviar mensagem", e)
            }
        }
    }

    fun generateChatId(recipientId: String, itemId: String, currentUserId: String): String {
        return chatRepository.generateChatId(currentUserId, recipientId, itemId)
    }

    fun listenToMessages(chatId: String) {
        chatRepository.listenToMessages(
            chatId = chatId,
            onMessagesUpdate = { msgs ->
                _messages.value = msgs
            },
            onError = {
                Log.e("ChatVM", "Erro ao ouvir mensagens em tempo real", it)
            }
        )
    }

    fun listenUserConversations(userId: String) {
        chatRepository.listenUserConversations(
            userId = userId,
            onUpdate = { updatedConversations ->
                _conversations.value = updatedConversations
            },
            onError = { error ->
                Log.e("ChatVM", "Erro ao escutar conversas em tempo real", error)
            }
        )
    }


}
