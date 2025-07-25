package com.edu.achadosufc.data.repository

import com.edu.achadosufc.data.model.Chat
import com.edu.achadosufc.data.model.Conversation
import com.edu.achadosufc.data.model.ItemInfo
import com.edu.achadosufc.data.model.Message
import com.edu.achadosufc.data.model.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatRepository(
    private val itemRepository: ItemRepository,
    private val userRepository: UserRepository
) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getUserConversations(userId: String): List<Conversation> {
        val chatSnapshots = firestore.collection("chats")
            .whereArrayContains("participants", userId)
            .get()
            .await()

        val conversations = mutableListOf<Conversation>()

        for (doc in chatSnapshots.documents) {
            val chat = doc.toObject(Chat::class.java) ?: continue
            val otherUserId = chat.participants.firstOrNull { it != userId } ?: continue

            val user = userRepository.fetchUserByIdAndSave(otherUserId.toInt())
            val item = itemRepository.fetchItemByIdAndSave(chat.itemId.toInt())

            val otherUser = user?.let {
                UserInfo(
                    id = otherUserId,
                    name = it.name,
                    imageUrl = it.imageUrl
                )
            }

            val itemInfo = item?.let {
                ItemInfo(
                    id = it.id.toString(),
                    title = it.title,
                    imageUrl = it.imageUrl
                )
            }

            if (otherUser != null) {
                conversations.add(
                    Conversation(
                        chatId = doc.id,
                        otherUser = otherUser,
                        lastMessage = chat.lastMessage,
                        lastMessageTimestamp = chat.lastMessageTimestamp,
                        itemInfo = itemInfo
                    )
                )
            }
        }

        return conversations.sortedByDescending { it.lastMessageTimestamp }
    }


    fun listenUserConversations(
        userId: String,
        onUpdate: (List<Conversation>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("chats")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    try {
                        val conversations = snapshot.documents.mapNotNull { doc ->
                            val chat = doc.toObject(Chat::class.java) ?: return@mapNotNull null
                            val otherUserId = chat.participants.firstOrNull { it != userId }
                                ?: return@mapNotNull null

                            val user = userRepository.fetchUserByIdAndSave(otherUserId.toInt())
                                ?: return@mapNotNull null
                            val item = itemRepository.fetchItemByIdAndSave(chat.itemId.toInt())

                            val otherUser = UserInfo(
                                id = otherUserId,
                                name = user.name,
                                imageUrl = user.imageUrl
                            )

                            val itemInfo = item?.let {
                                ItemInfo(
                                    id = it.id.toString(),
                                    title = it.title,
                                    imageUrl = it.imageUrl
                                )
                            }

                            Conversation(
                                chatId = doc.id,
                                otherUser = otherUser,
                                lastMessage = chat.lastMessage,
                                lastMessageTimestamp = chat.lastMessageTimestamp,
                                itemInfo = itemInfo
                            )
                        }.sortedByDescending { it.lastMessageTimestamp }

                        withContext(kotlinx.coroutines.Dispatchers.Main) {
                            onUpdate(conversations)
                        }
                    } catch (e: Exception) {
                        withContext(kotlinx.coroutines.Dispatchers.Main) {
                            onError(e)
                        }
                    }
                }
            }
    }



    suspend fun getConversationsForItem(itemId: String, currentUserId: String): List<Conversation> {
        val querySnapshot = firestore.collection("chats")
            .whereEqualTo("itemId", itemId)
            .whereArrayContains("participants", currentUserId)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull { doc ->
            val chat = doc.toObject(Chat::class.java) ?: return@mapNotNull null
            val otherUserId = chat.participants.firstOrNull { it != currentUserId } ?: return@mapNotNull null

            val user = userRepository.fetchUserByIdAndSave(otherUserId.toInt()) ?: return@mapNotNull null

            val userInfo = UserInfo(
                id = otherUserId,
                name = user.name,
                imageUrl = user.imageUrl
            )

            Conversation(
                chatId = doc.id,
                otherUser = userInfo,
                lastMessage = chat.lastMessage,
                lastMessageTimestamp = chat.lastMessageTimestamp
            )
        }.sortedByDescending { it.lastMessageTimestamp }
    }



    suspend fun getMessages(chatId: String): List<Message> {
        val snapshot = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .await()

        return snapshot.toObjects(Message::class.java)
    }

    suspend fun sendMessage(
        chatId: String,
        text: String,
        senderId: String,
        recipientId: String,
        itemId: String
    ) {
        val message = Message(text = text, senderId = senderId)
        val chatRef = firestore.collection("chats").document(chatId)

        chatRef.collection("messages").add(message).await()

        val chatMetadata = mapOf(
            "participants" to listOf(senderId, recipientId),
            "itemId" to itemId,
            "lastMessage" to text,
            "lastMessageTimestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        chatRef.set(chatMetadata, SetOptions.merge()).await()
    }

    fun generateChatId(currentUserId: String, recipientId: String, itemId: String): String {
        return if (currentUserId > recipientId) {
            "${currentUserId}_${recipientId}_${itemId}"
        } else {
            "${recipientId}_${currentUserId}_${itemId}"
        }
    }

    fun listenToMessages(
        chatId: String,
        onMessagesUpdate: (List<Message>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = snapshot.toObjects(Message::class.java)
                    onMessagesUpdate(messages)
                }
            }
    }

}
