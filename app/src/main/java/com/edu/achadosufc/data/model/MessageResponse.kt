package com.edu.achadosufc.data.model

import java.util.Date

data class MessageResponse(
    val id: Int?,
    val text: String,
    val sender: UserResponse,
    val createdAt: Date,
    val recipient: UserResponse,
    val isRead: Boolean = false
)
