package com.edu.achadosufc.data.model

data class UserRequest (
    val username: String,
    val name: String,
    val email: String,
    val password: String,
    val phone: String?,
    val imageUrl: String?,
    val surname: String?
)