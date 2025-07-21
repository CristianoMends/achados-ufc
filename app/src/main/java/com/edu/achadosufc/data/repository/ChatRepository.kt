package com.edu.achadosufc.data.repository

import ChatSocketService
import com.edu.achadosufc.data.dao.MessageDao
import com.edu.achadosufc.data.model.MessageEntity
import com.edu.achadosufc.data.model.MessageResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChatRepository(
    private val messageDao: MessageDao,
    private val chatSocketService: ChatSocketService
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        // Ouve continuamente por novas mensagens e salva no DB
        scope.launch {
            chatSocketService.incomingMessages.collect { message ->
                saveMessage(message)
            }
        }
        // Ouve pelo histórico e salva no DB
        scope.launch {
            chatSocketService.chatHistory.collect { history ->
                saveMessageList(history)
            }
        }
    }

    // O ViewModel vai observar este Flow para obter as mensagens
    fun getMessages(currentUserId: Int, otherUserId: Int): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForChat(currentUserId, otherUserId)
    }

    // A lógica principal: "buscar da rede somente se o DB estiver vazio"
    suspend fun fetchHistoryFromServerIfEmpty(currentUserId: Int, otherUserId: Int) {
        val messageCount = messageDao.getMessageCountForChat(currentUserId, otherUserId)
        if (messageCount == 0) {
            chatSocketService.getChatHistory(otherUserId)
        }
    }

    fun sendMessage(senderId: Int, recipientId: Int, text: String) {
        chatSocketService.sendPrivateMessage(senderId, recipientId, text)
    }

    // Funções auxiliares para salvar e converter os dados
    private suspend fun saveMessageList(messages: List<MessageResponse>) {
        val entities = messages.map { it.toEntity() }
        messageDao.insertMessages(entities)
    }

    private suspend fun saveMessage(message: MessageResponse) {
        messageDao.insertMessages(listOf(message.toEntity()))
    }
}

// Função de extensão para converter o modelo da rede para o do DB
fun MessageResponse.toEntity(): MessageEntity {
    return MessageEntity(
        id = this.id!!,
        text = this.text,
        createdAt = this.createdAt,
        isRead = this.isRead,
        senderId = this.sender.id,
        senderUsername = this.sender.username,
        senderImageUrl = this.sender.imageUrl,
        recipientId = this.recipient.id
    )
}