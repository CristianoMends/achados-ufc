package com.edu.achadosufc.data.model
import com.google.firebase.Timestamp

data class Chat(
    val participants: List<String> = emptyList(),
    val itemId: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Timestamp? = null
)
data class Conversation(
    val chatId: String,
    val otherUser: UserInfo,
    val lastMessage: String,
    val lastMessageTimestamp: Timestamp?,
    val itemInfo: ItemInfo? = null
)

data class UserInfo(
    val id: String = "",
    val name: String = "",
    val imageUrl: String? = null
)

data class ItemInfo(
    val id: String = "",
    val title: String = "",
    val imageUrl: String? = null
)

