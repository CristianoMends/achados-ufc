package com.edu.achadosufc.data.model


data class Item(
    val id: Int = 0,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val location: String,
    val date: String,
    val isFound: Boolean = false,
    val user: UserResponse
)



