package com.edu.achadosufc.data.model

import java.util.Date

data class MessageRequest(
    val id: Int?,
    val text: String,
    val sender: UserResponse,
    val createdAt: Date?,
)
