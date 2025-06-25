package com.edu.achadosufc.model.item

data class ItemRequest(
    val title: String,
    val description: String,
    val location: String,
    val isFound: Boolean,
    val userId: Int
)
