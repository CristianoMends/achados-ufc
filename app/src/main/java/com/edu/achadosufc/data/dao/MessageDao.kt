package com.edu.achadosufc.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.edu.achadosufc.data.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query(
        """
        SELECT * FROM messages 
        WHERE (senderId = :userId1 AND recipientId = :userId2) 
           OR (senderId = :userId2 AND recipientId = :userId1)
        ORDER BY createdAt ASC
    """
    )
    fun getMessagesForChat(userId1: Int, userId2: Int): Flow<List<MessageEntity>>

    @Query(
        """
        SELECT COUNT(id) FROM messages 
        WHERE (senderId = :userId1 AND recipientId = :userId2) 
           OR (senderId = :userId2 AND recipientId = :userId1)
    """
    )
    suspend fun getMessageCountForChat(userId1: Int, userId2: Int): Int
}