package com.edu.achadosufc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val username: String?,
    val email: String,
    val imageUrl: String?,
    val name: String,
    val phone: String?,
    val surname: String?
)

fun UserResponse.toUserEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        email = this.email,
        imageUrl = this.imageUrl,
        name = this.name,
        phone = this.phone,
        surname = this.surname
    )
}

fun UserEntity.toUserResponse(): UserResponse {
    return UserResponse(
        id = this.id,
        username = this.username,
        email = this.email,
        imageUrl = this.imageUrl,
        name = this.name,
        phone = this.phone,
        surname = this.surname
    )
}