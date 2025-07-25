package com.edu.achadosufc.data.model

data class UserResponse(
    val id: Int,
    val username: String?,
    val name: String,
    val email: String,
    val phone: String?,
    val imageUrl: String?,
    val surname: String?
)
