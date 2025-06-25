package com.edu.achadosufc.model.user

data class UserRequest (
    val username: String,
    val name: String,
    val email: String,
    val password: String,
    val phone: String?,
    val imageUrl: String?,
    val surname: String?
)