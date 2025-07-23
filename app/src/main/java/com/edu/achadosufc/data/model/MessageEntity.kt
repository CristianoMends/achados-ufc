package com.edu.achadosufc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: Int,
    val text: String,
    val createdAt: Date,
    val isRead: Boolean,

    val senderId: Int,
    val senderUsername: String?,
    val senderImageUrl: String?,

    val recipientId: Int
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}