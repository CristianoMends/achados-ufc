package com.edu.achadosufc.data.repository

import com.edu.achadosufc.data.service.ChatSocketService
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
        scope.launch {
            chatSocketService.incomingMessages.collect { message ->
                saveMessage(message)
            }
        }
        scope.launch {
            chatSocketService.chatHistory.collect { history ->
                saveMessageList(history)
            }
        }
    }

    fun getMessages(currentUserId: Int, otherUserId: Int): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForChat(currentUserId, otherUserId)
    }

    suspend fun fetchHistoryFromServerIfEmpty(currentUserId: Int, otherUserId: Int, itemId: Int) {
        val messageCount = messageDao.getMessageCountForChat(currentUserId, otherUserId)
        if (messageCount == 0) {
            chatSocketService.getChatHistory(otherUserId,itemId)
        }
    }

    fun sendMessage(senderId: Int, recipientId: Int, text: String, itemId: Int) {
        chatSocketService.sendPrivateMessage(senderId, recipientId, text, itemId)
    }

    private suspend fun saveMessageList(messages: List<MessageResponse>) {
        val entities = messages.map { it.toEntity() }
        messageDao.insertMessages(entities)
    }

    private suspend fun saveMessage(message: MessageResponse) {
        messageDao.insertMessages(listOf(message.toEntity()))
    }


}

private fun MessageResponse.toEntity(): MessageEntity {
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