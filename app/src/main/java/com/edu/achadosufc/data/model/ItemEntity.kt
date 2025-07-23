package com.edu.achadosufc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val location: String,
    val isFound: Boolean,
    val date: String,
    val userId: Int,


    val userUsername: String?,
    val userEmail: String,
    val userImageUrl: String?,
    val userName: String,
    val userSurname: String?,
    val userPhone: String?


)

fun Item.toItemEntity(): ItemEntity {
    return ItemEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl,
        location = this.location,
        isFound = this.isFound,
        date = this.date,
        userId = this.user.id,

        userUsername = this.user.username,
        userEmail = this.user.email,
        userImageUrl = this.user.imageUrl,
        userName = this.user.name,
        userSurname = this.user.surname,
        userPhone = this.user.phone
    )
}

fun ItemEntity.toItem(): Item {
    return Item(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl,
        location = this.location,
        isFound = this.isFound,
        date = this.date,
        user = UserResponse(
            id = this.userId,
            username = this.userUsername,
            email = this.userEmail,
            imageUrl = this.userImageUrl,
            name = this.userName,
            phone = this.userPhone,
            surname = this.userSurname
        )
    )
}