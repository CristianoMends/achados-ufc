package com.edu.achadosufc.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Message(
    val text: String = "",
    val senderId: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null
)